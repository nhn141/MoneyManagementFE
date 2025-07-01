package DI.Models.NewsFeed

data class UpdatePostTargetRequest(
    val targetType: Int,
    val targetGroupIds: List<String>?
)
