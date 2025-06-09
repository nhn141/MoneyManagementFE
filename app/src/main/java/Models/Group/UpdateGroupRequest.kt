package DI.Models.Group

data class UpdateGroupRequest(
    val groupName: String,
    val groupDescription: String?,
    val avatarUrl: String? = null
)
