package DI.Models.Group

data class SendGroupMessageRequest(
    val groupId: String,
    val content: String
)
