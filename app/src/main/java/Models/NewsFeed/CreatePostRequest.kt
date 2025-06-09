package DI.Models.NewsFeed

data class CreatePostRequest(
    val content: String,
    val mediaFile: String?
)
