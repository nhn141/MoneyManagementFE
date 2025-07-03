package DI.Models.NewsFeed

data class Notification(
    val id: String,
    val type: String, // "like", "comment", "reply"
    val content: String,
    val createdAt: String,
    val userName: String,
    val userAvatarUrl: String?,
    val postId: String
)