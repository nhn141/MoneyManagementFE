package DI.Models.Group

data class LatestGroupChat(
    val group: Group,
    val latestMessage: GroupMessage?,
    val unreadCount: Int
)
