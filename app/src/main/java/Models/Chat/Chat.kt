package DI.Models.Chat

data class Chat(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadMessagesCount: Int,
    val messages: List<ChatMessage> // Trả về all chats thì ko cần trả lịch sử tin nhắn -> Bỏ được
)
