package DI.Models.NewsFeed

data class PostDetail(
    val postId: String,
    val content: String?,
    val createdAt: String?,
    val createdBy: String?,
    val authorAvatarUrl: String?,
    val likesCount: Int,
    val isLikedByCurrentUser: Boolean,
    val commentsCount: Int,
    val mediaUrl: String?,
    val mediaType: String?,
    val targetType: Int,
    val targetGroupIds: String?,
    val comments: List<Comment>,
    val likes: List<Like>
)

