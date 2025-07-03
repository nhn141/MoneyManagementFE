package DI.ViewModels

import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.CreateCommentRequest
import DI.Models.NewsFeed.LatestSocialNotification
import DI.Models.NewsFeed.Notification
import DI.Models.NewsFeed.Post
import DI.Models.NewsFeed.PostDetail
import DI.Models.NewsFeed.ReplyCommentRequest
import DI.Models.NewsFeed.ReplyCommentResponse
import DI.Models.NewsFeed.ResultState
import DI.Repositories.NewsFeedRepository
import DI.Repositories.ProfileRepository
import Utils.StringResourceProvider
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    private val repository: NewsFeedRepository,
    private val stringProvider: StringResourceProvider,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasMorePosts = MutableStateFlow(true)
    val hasMorePosts: StateFlow<Boolean> = _hasMorePosts.asStateFlow()

    private val _postCreationState = MutableStateFlow<ResultState<Post>?>(null)
    val postCreationState: StateFlow<ResultState<Post>?> = _postCreationState.asStateFlow()

    private val _likeState = MutableStateFlow<ResultState<Unit>?>(null)
    val likeState: StateFlow<ResultState<Unit>?> = _likeState.asStateFlow()

    private val _commentState = MutableStateFlow<ResultState<List<Comment>>>(ResultState.Loading)
    val commentState: StateFlow<ResultState<List<Comment>>> = _commentState

    private val _postDetail = MutableStateFlow<ResultState<PostDetail>>(ResultState.Loading)
    val postDetail: StateFlow<ResultState<PostDetail>> = _postDetail

    private val _updateTargetState = MutableStateFlow<ResultState<Unit>?>(null)
    val updateTargetState: StateFlow<ResultState<Unit>?> = _updateTargetState.asStateFlow()

    private val _replyState = MutableStateFlow<ResultState<ReplyCommentResponse>?>(null)
    val replyState: StateFlow<ResultState<ReplyCommentResponse>?> = _replyState.asStateFlow()

    private val _deleteReplyState = MutableStateFlow<ResultState<Unit>?>(null)
    val deleteReplyState: StateFlow<ResultState<Unit>?> = _deleteReplyState.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

    private val readNotificationIds = mutableSetOf<String>()
    private var currentPage = 1
    private val pageSize = 10

    init {
        loadNextPost()
    }

    fun getLatestSocialInfo(): LatestSocialNotification {
        val latestPost = _posts.value.firstOrNull()
        if (latestPost == null) {
            return LatestSocialNotification(
                latestPost = stringProvider.getString(R.string.no_posts_available)
            )
        }

        val latestPostContent = "\"${latestPost.content}\""
        val latestPostAuthor = latestPost.authorName ?: stringProvider.getString(R.string.unknown_author)
        return LatestSocialNotification(
            latestPost = stringProvider.getString(
                R.string.latest_post_format,
                latestPostAuthor,
                latestPostContent
            )
        )
    }

    fun loadNextPost() {
        if (_isLoading.value || !_hasMorePosts.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.getNewsFeed(currentPage, pageSize)) {
                is ResultState.Success -> {
                    val data = result.data
                    _posts.update { it + data.posts }
                    _hasMorePosts.value = data.hasMorePosts
                    currentPage++
                    checkNewNotifications()
                }

                is ResultState.Error -> {
                    _error.value = result.message
                }

                ResultState.Loading -> {
                    // Không cần xử lý
                }
            }

            _isLoading.value = false
        }
    }

    fun refresh() {
        currentPage = 1
        _hasMorePosts.value = true
        _posts.value = emptyList()
        _notifications.value = emptyList()
        _unreadNotificationCount.value = 0
        loadNextPost()
    }

    private fun checkNewNotifications() {
        viewModelScope.launch {
            val profileResult = profileRepository.getProfile()
            val currentUserId = if (profileResult.isSuccess) {
                profileResult.getOrNull()?.id ?: run {
                    _error.value = "Không thể lấy thông tin người dùng: ID rỗng"
                    return@launch
                }
            } else {
                _error.value = "Không thể lấy thông tin người dùng: ${profileResult.exceptionOrNull()?.message}"
                return@launch
            }

            val notifications = mutableListOf<Notification>()
            _posts.value
                .filter { it.authorId == currentUserId }
                .forEach { post ->
                    when (val result = repository.getPostDetail(post.postId)) {
                        is ResultState.Success -> {
                            val postDetail = result.data
                            postDetail.likes.forEach { like ->
                                if (like.likeId !in readNotificationIds) {
                                    notifications.add(
                                        Notification(
                                            id = like.likeId,
                                            type = "like",
                                            createdAt = like.createdAt,
                                            userName = like.userName,
                                            userAvatarUrl = null,
                                            postId = post.postId
                                        )
                                    )
                                }
                            }
                            postDetail.comments.forEach { comment ->
                                if (comment.commentId !in readNotificationIds) {
                                    notifications.add(
                                        Notification(
                                            id = comment.commentId,
                                            type = "comment",
                                            createdAt = comment.createdAt,
                                            userName = comment.authorName,
                                            userAvatarUrl = comment.authorAvatarUrl,
                                            postId = post.postId
                                        )
                                    )
                                }
                                comment.replies
                                    .filter { comment.authorId == currentUserId }
                                    .forEach { reply ->
                                        if (reply.replyId !in readNotificationIds) {
                                            notifications.add(
                                                Notification(
                                                    id = reply.replyId,
                                                    type = "reply",
                                                    createdAt = reply.createdAt,
                                                    userName = reply.authorName,
                                                    userAvatarUrl = reply.authorAvatarUrl,
                                                    postId = post.postId
                                                )
                                            )
                                        }
                                    }
                            }
                        }
                        is ResultState.Error -> {
                            _error.value = result.message
                        }
                        else -> {}
                    }
                }
            _notifications.value = notifications.sortedByDescending { it.createdAt }
            _unreadNotificationCount.value = notifications.count { it.id !in readNotificationIds }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        readNotificationIds.add(notificationId)
        _unreadNotificationCount.value = _notifications.value.count { it.id !in readNotificationIds }
    }

    fun clearPostCreationState() {
        _postCreationState.value = null
    }

    fun createPost(
        content: String,
        category: String = "general",
        fileUri: Uri?,
        targetType: Int?,
        targetGroupIds: String?
    ) {
        viewModelScope.launch {
            _postCreationState.value = ResultState.Loading
            val result =
                repository.createPost(content, category, fileUri, targetType, targetGroupIds)
            _postCreationState.value = result

            if (result is ResultState.Success) {
                _posts.update { listOf(result.data) + it }
                checkNewNotifications()
            }
            Log.d("NewsFeedViewModel", "createPost: $result content: $content category: $category fileUri: $fileUri targetType: $targetType targetGroupIds: $targetGroupIds")
        }
    }

    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            _postDetail.value = ResultState.Loading
            val result = repository.getPostDetail(postId)
            _postDetail.value = result
            if (result is ResultState.Success) {
                checkNewNotifications()
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            _likeState.value = ResultState.Loading

            val result = repository.likePost(postId)

            when (result) {
                is ResultState.Success -> {
                    _likeState.value = result
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(
                                    isLikedByCurrentUser = true,
                                    likesCount = post.likesCount + 1
                                )
                            } else {
                                post
                            }
                        }
                    }
                    checkNewNotifications()
                }

                is ResultState.Error -> {
                    _likeState.value = result
                }

                ResultState.Loading -> {
                    // Không cần xử lý
                }
            }
        }
    }

    fun unlikePost(postId: String) {
        viewModelScope.launch {
            _likeState.value = ResultState.Loading

            val result = repository.unlikePost(postId)

            when (result) {
                is ResultState.Success -> {
                    _likeState.value = result
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(
                                    isLikedByCurrentUser = false,
                                    likesCount = (post.likesCount - 1).coerceAtLeast(0)
                                )
                            } else {
                                post
                            }
                        }
                    }
                    checkNewNotifications()
                }

                is ResultState.Error -> {
                    _likeState.value = result
                }

                ResultState.Loading -> {
                    // Không cần xử lý
                }
            }
        }
    }

    fun createComment(postId: String, content: String) {
        viewModelScope.launch {
            _commentState.value = ResultState.Loading

            val result = repository.createComment(CreateCommentRequest(postId, content))

            if (result is ResultState.Success) {
                _posts.update { posts ->
                    posts.map { post ->
                        if (post.postId == postId) {
                            post.copy(commentsCount = post.commentsCount + 1)
                        } else post
                    }
                }
                fetchComments(postId)
                checkNewNotifications()
            } else if (result is ResultState.Error) {
                _commentState.value = ResultState.Error(result.message ?: "Create comment failed")
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        viewModelScope.launch {
            val result = repository.deleteComment(commentId)

            if (result is ResultState.Success) {
                _posts.update { posts ->
                    posts.map { post ->
                        if (post.postId == postId) {
                            post.copy(commentsCount = (post.commentsCount - 1).coerceAtLeast(0))
                        } else post
                    }
                }
                fetchComments(postId)
                checkNewNotifications()
            } else if (result is ResultState.Error) {
                _commentState.value = ResultState.Error(result.message ?: "Delete comment failed")
            }
        }
    }

    fun fetchComments(postId: String) {
        viewModelScope.launch {
            _commentState.value = ResultState.Loading
            when (val result = repository.getPostDetail(postId)) {
                is ResultState.Success -> {
                    _commentState.value = ResultState.Success(result.data.comments)
                    checkNewNotifications()
                }

                is ResultState.Error -> {
                    _commentState.value = ResultState.Error(result.message)
                }

                else -> Unit
            }
        }
    }

    fun updatePostTarget(postId: String, targetType: Int, targetGroupIds: List<String>?) {
        viewModelScope.launch {
            _updateTargetState.value = ResultState.Loading
            val result = repository.updatePostTarget(postId, targetType, targetGroupIds)
            _updateTargetState.value = result
            Log.d("NewsFeedViewModel", "updatePostTarget: $result postId: $postId targetType: $targetType targetGroupIds: $targetGroupIds")
            if (result is ResultState.Success) {
                _posts.update { posts ->
                    posts.map { post ->
                        if (post.postId == postId) {
                            post.copy(
                                targetType = targetType,
                                targetGroupIds = targetGroupIds
                            )
                        } else {
                            post
                        }
                    }
                }
                refreshPost(postId)
            }
        }
    }

    private fun refreshPost(postId: String) {
        viewModelScope.launch {
            when (val result = repository.getPostDetail(postId)) {
                is ResultState.Success -> {
                    val updatedPost = result.data
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                Post(
                                    postId = post.postId,
                                    content = post.content,
                                    authorId = post.authorId,
                                    authorName = post.authorName,
                                    createdAt = post.createdAt,
                                    commentsCount = post.commentsCount,
                                    likesCount = post.likesCount,
                                    isLikedByCurrentUser = post.isLikedByCurrentUser,
                                    targetType = updatedPost.targetType,
                                    targetGroupIds = updatedPost.targetGroupIds,
                                    authorAvatarUrl = post.authorAvatarUrl,
                                    mediaType = post.mediaType,
                                    mediaUrl = post.mediaUrl
                                )
                            } else {
                                post
                            }
                        }
                    }
                    checkNewNotifications()
                }
                is ResultState.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }

    fun clearUpdateTargetState() {
        _updateTargetState.value = null
    }

    fun replyToComment(request: ReplyCommentRequest, postId: String) {
        viewModelScope.launch {
            _replyState.value = ResultState.Loading

            val result = repository.replyToComment(request)

            _replyState.value = result.fold(
                onSuccess = {
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(commentsCount = post.commentsCount + 1)
                            } else post
                        }
                    }
                    fetchComments(postId)
                    checkNewNotifications()
                    ResultState.Success(it)
                },
                onFailure = {
                    ResultState.Error(it.message ?: "Unknown error")
                }
            )
        }
    }

    fun deleteReply(replyId: String, postId: String) {
        viewModelScope.launch {
            _deleteReplyState.value = ResultState.Loading
            val result = repository.deleteReply(replyId)
            _deleteReplyState.value = result.fold(
                onSuccess = {
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(commentsCount = (post.commentsCount - 1).coerceAtLeast(0))
                            } else post
                        }
                    }
                    fetchComments(postId)
                    checkNewNotifications()
                    ResultState.Success(Unit)
                },
                onFailure = {
                    ResultState.Error(it.message ?: "Unknown error")
                }
            )
        }
    }
}
