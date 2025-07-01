package DI.Models.Chat

data class ChatMessage(
    val messageID: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val sentAt: String,
    val senderName: String,
    val receiverName: String
)
