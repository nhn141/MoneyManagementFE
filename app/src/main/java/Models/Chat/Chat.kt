package DI.Models.Chat

data class Chat(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val messages: List<ChatMessage>,
    val avatarUrl: String? = null
)
