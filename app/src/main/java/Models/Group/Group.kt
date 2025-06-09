package DI.Models.Group

import java.util.Date

// Matches backend GroupDTO exactly
data class Group(
    val groupId: String,
    val name: String, // Exact backend field name
    val description: String?,
    val imageUrl: String?,
    val createdAt: String, // Keep as Date to match backend
    val creatorId: String,
    val creatorName: String,
    val memberCount: Int,
    val role: GroupRole
)
