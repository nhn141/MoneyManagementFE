package DI.Models.Group

import java.util.Date

// Matches backend GroupMemberDTO exactly
data class GroupMember(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val role: GroupRole,
    val joinedAt: Date
)
