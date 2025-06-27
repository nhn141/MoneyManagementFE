package DI.Models.NewsFeed

data class CreateCommentRequest(
    val postId: String,
    val content: String
)