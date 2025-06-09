package DI.Models.Group

data class GroupMessage(
    val messageId: String,
    val groupId: String,
    val senderId: String,
    val content: String,
    val timestamp: String, // Maps to timestamp in DTO, keep as String for API compatibility
    val senderName: String,
    val senderAvatarUrl: String? = null
)
