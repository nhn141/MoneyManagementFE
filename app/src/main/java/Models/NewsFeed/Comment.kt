package DI.Models.NewsFeed

data class Comment(
    val commentId: String,
    val content: String,
    val createdAt: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String
)
