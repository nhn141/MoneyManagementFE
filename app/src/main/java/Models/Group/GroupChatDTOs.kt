package DI.Models.Group

data class CreateGroupRequest(
    val name: String,
    val description: String? = null,
    val initialMemberIds: List<String>? = null
)

data class Group(
    val groupId: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val createdAt: String, // ISO 8601 datetime, dùng String hoặc LocalDateTime nếu có converter
    val creatorId: String,
    val creatorName: String,
    val memberCount: Int,
    val role: GroupRole
)

enum class GroupRole {
    Member,
    Collaborator,
    Admin
}

data class GroupMember(
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val role: Int,
    val joinedAt: String // hoặc LocalDateTime nếu có converter
)

data class GroupMessage(
    val messageId: String,
    val groupId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatarUrl: String? = null,
    val content: String,
    val sentAt: String
)

data class GroupChatHistoryDto(
    val groupId: String,
    val groupName: String,
    val groupImageUrl: String?,
    val messages: List<GroupMessage>,
    val unreadCount: Int
)

data class SendGroupMessageRequest(
    val groupId: String,
    val content: String
)

data class GroupChatHistory(
    val groupId: String,
    val groupName: String,
    val groupImageUrl: String? = null,
    val messages: List<GroupMessage> = emptyList(),
    val unreadCount: Int
)

data class UpdateGroupRequest(
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null
)

data class AdminLeaveResult(
    val success: Boolean,
    val action: String, // "leave" hoặc "delete"
    val groupId: String,
    val newAdminId: String? = null
)

data class GroupMemberProfile(
    val userId: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val role: GroupRole,
)

data class SimulatedLatestGroupChatDto(
    val groupId: String,
    val groupName: String,
    val groupImageUrl: String?,
    val latestMessageContent: String?,
    val unreadCount: Int = 0,
    val sendAt: String,
)

data class SendGroupMessageDTO(
    val groupId: String,
    val content: String
)

data class TransactionMessageInfo(
    val transactionId: String,
    val message: GroupMessage
)

data class GroupChatItemDto(
    val groupId: String,
    val groupName: String,
    val latestMessageContent: String,
    val sendAt: String,
    val unreadCount: Int,
    val transactionId: String?, // null nếu không phải transaction
    val isTransactionMessage: Boolean
)

data class AvatarDTO(
    val avatarUrl: String
)

data class MemberWithStatus(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val role: Int,
    val joinedAt: String,
    val isMuted: Boolean,
    val isBanned: Boolean,
    val mutedAt: String?,
    val mutedUntil: String?,
    val muteReason: String?,
    val banReason: String?,
    val lastModerationUpdate: String?
)
