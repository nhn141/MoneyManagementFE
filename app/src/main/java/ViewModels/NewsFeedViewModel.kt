package DI.ViewModels

import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.CreateCommentRequest
import DI.Models.NewsFeed.ResultState
import DI.Models.NewsFeed.Post
import DI.Models.NewsFeed.PostDetail
import DI.Models.NewsFeed.ReplyCommentRequest
import DI.Models.NewsFeed.ReplyCommentResponse
import DI.Repositories.NewsFeedRepository
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    private val repository: NewsFeedRepository
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



    private var currentPage = 1
    private val pageSize = 1

    init {
        loadNextPost()
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
        loadNextPost()
    }

    fun clearPostCreationState() {
        _postCreationState.value = null
    }

    fun createPost(
        content: String,
        category: String = "general",
        fileUri: Uri?,
        targetType: Int? = null,
        targetGroupIds: String? = null
    ) {
        viewModelScope.launch {
            _postCreationState.value = ResultState.Loading
            val result = repository.createPost(content, category, fileUri, targetType, targetGroupIds)
            _postCreationState.value = result

            if (result is ResultState.Success) {
                _posts.update { listOf(result.data) + it }
            }
        }
    }



    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            _postDetail.value = ResultState.Loading
            val result = repository.getPostDetail(postId)
            _postDetail.value = result
        }
    }


    fun likePost(postId: String) {
        viewModelScope.launch {
            _likeState.value = ResultState.Loading

            val result = repository.likePost(postId)

            when (result) {
                is ResultState.Success -> {
                    _likeState.value = result
                    // Cập nhật thủ công vì không có dữ liệu từ server
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(isLikedByCurrentUser = true, likesCount = post.likesCount + 1)
                            } else {
                                post
                            }
                        }
                    }
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
                    // Cập nhật thủ công vì không có dữ liệu từ server
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(isLikedByCurrentUser = false, likesCount = (post.likesCount - 1).coerceAtLeast(0))
                            } else {
                                post
                            }
                        }
                    }
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

                // Gọi lại fetch để lấy comment mới
                fetchComments(postId)
            } else if (result is ResultState.Error) {
                _commentState.value = ResultState.Error(result.message ?: "Create comment failed")
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        viewModelScope.launch {
            val result = repository.deleteComment(commentId)

            if (result is ResultState.Success) {
                // Cập nhật số lượng comment
                _posts.update { posts ->
                    posts.map { post ->
                        if (post.postId == postId) {
                            post.copy(commentsCount = (post.commentsCount - 1).coerceAtLeast(0))
                        } else post
                    }
                }

                // Gọi lại
                fetchComments(postId)
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
                }
                is ResultState.Error -> {
                    _commentState.value = ResultState.Error(result.message)
                }
                else -> Unit
            }
        }
    }

    fun updatePostTarget(postId: String, targetType: Int, targetGroupIds: List<String>) {
        viewModelScope.launch {
            _updateTargetState.value = ResultState.Loading
            val result = repository.updatePostTarget(postId, targetType, targetGroupIds)
            _updateTargetState.value = result
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
                    // Cập nhật commentsCount
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(commentsCount = post.commentsCount + 1)
                            } else post
                        }
                    }

                    // gọi lại fetch comment
                    fetchComments(postId)
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
                    // Cập nhật số lượng comment
                    _posts.update { posts ->
                        posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(commentsCount = (post.commentsCount - 1).coerceAtLeast(0))
                            } else post
                        }
                    }
                    // Gọi lại fetch comments
                    fetchComments(postId)
                    ResultState.Success(Unit)
                },
                onFailure = {
                    ResultState.Error(it.message ?: "Unknown error")
                }
            )
        }
    }

}
