package DI.Models.NewsFeed

data class NewsFeedResponse(
    val posts: List<Post>,
    val totalCount: Int,
    val hasMorePosts: Boolean
)