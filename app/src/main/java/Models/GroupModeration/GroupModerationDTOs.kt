package DI.Models.GroupModeration

data class GroupMemberModeration(
    val id: String,
    val groupMemberId: String,
    val userId: String,
    val groupId: String,
    val isMuted: Boolean,
    val isBanned: Boolean,
    val mutedUntil: String?,  // Có thể là một DateTime hoặc String tùy vào cách backend gửi
    val muteReason: String?,
    val banReason: String?,
    val moderatorId: String,
    val createdAt: String,
    val updatedAt: String?
)

data class GroupMessageModeration(
    val id: String,
    val groupMessageId: String,
    val groupId: String,
    val isDeleted: Boolean,
    val deletionReason: String?,
    val moderatorId: String,
    val createdAt: String
)

data class MuteUserRequest(
    val groupId: String,
    val targetUserId: String,
    val reason: String,
    val durationInMinutes: Int = 60
)

data class GroupUserActionRequest(
    val groupId: String,
    val targetUserId: String
)

enum class ModerationActionType {
    MUTE,
    UNMUTE,
    BAN,
    UNBAN,
    KICK,
    DELETE_MESSAGE,
    GRANT_MOD_ROLE,
    REVOKE_MOD_ROLE
}

data class GroupModerationAction(
    val id: String,
    val groupId: String,
    val moderatorId: String,
    val targetUserId: String,
    val actionType: ModerationActionType,
    val reason: String?,
    val messageId: String?,
    val expiresAt: String?,
    val createdAt: String
)

data class UnmuteUserRequest(
    val targetUserId: String
)

data class BanKickUserRequest(
    val groupId: String,
    val targetUserId: String,
    val reason: String
)

data class DeleteMessageRequest(
    val groupId: String,
    val messageId: String,
    val reason: String
)

data class GrantRoleRequest(
    val userId: String
)

data class ModerationLogResponse(
    val logs: List<ModerationLog>,
    val totalCount: Int
)

data class ModerationLog(
    val action: String,
    val userId: String,
    val timestamp: String,
    val details: String
)

data class ModerationActionDTO(
    val id: String,
    val groupId: String,
    val moderatorId: String,
    val moderatorName: String,
    val targetUserId: String,
    val targetUserName: String,
    val actionType: String,
    val reason: String?,
    val createdAt: String,
    val expiresAt: String?
)

data class UserGroupStatusDTO(
    val groupId: String,
    val userId: String,
    val isMuted: Boolean,
    val isBanned: Boolean,
    val mutedAt: String?,
    val mutedUntil: String?,
    val muteReason: String?,
    val banReason: String?,
    val lastModerationUpdate: String?
)