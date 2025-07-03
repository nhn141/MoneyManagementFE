package DI.Models

// ===== REACTIONS =====

data class CreateMessageReactionDTO(
    val messageId: String,
    val reactionType: String,
    val messageType: String // "direct" or "group"
)

data class RemoveMessageReactionDTO(
    val messageId: String,
    val reactionType: String,
    val messageType: String
)

data class MessageReactionDTO(
    val reactionId: String,
    val messageId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?, // nullable
    val reactionType: String,
    val createdAt: String,
    val messageType: String
)

data class MessageReactionSummaryDTO(
    val messageId: String,
    val reactionCounts: Map<String, Int>,
    val reactionDetails: Map<String, List<MessageReactionDTO>>,
    val hasUserReacted: Boolean,
    val userReactionTypes: List<String>
)

data class BatchMessageReactionsRequestDTO(
    val messageIds: List<String>,
    val messageType: String = "direct"
)

// ===== MENTIONS =====

data class MessageMentionDTO(
    val mentionId: String,
    val messageId: String,
    val mentionedUserId: String,
    val mentionedUserName: String,
    val mentionedUserAvatarUrl: String?, // nullable
    val mentionedByUserId: String,
    val mentionedByUserName: String,
    val startPosition: Int,
    val length: Int,
    val isRead: Boolean,
    val createdAt: String,
    val messageType: String,
    val groupId: String? // nullable
)

data class MentionNotificationDTO(
    val mentionId: String,
    val messageId: String,
    val messageContent: String,
    val mentionedByUserId: String,
    val mentionedByUserName: String,
    val mentionedByUserAvatarUrl: String?, // nullable
    val messageType: String,
    val groupId: String?, // nullable
    val groupName: String?, // nullable
    val createdAt: String
)

// ===== ENHANCED MESSAGE =====

data class EnhancedMessageDTO(
    val messageID: String,
    val senderId: String,
    val senderName: String,
    val senderAvatarUrl: String?, // nullable
    val content: String,
    val sentAt: String,
    val messageType: String,
    val receiverId: String?, // nullable
    val receiverName: String?, // nullable
    val groupId: String?, // nullable
    val groupName: String?, // nullable
    val reactions: MessageReactionSummaryDTO,
    val mentions: List<MessageMentionDTO>,
    val isEdited: Boolean,
    val editedAt: String? // nullable
)