package DI.Models.Group

data class CreateGroupRequest(
    val groupName: String,
    val groupDescription: String?,
    val memberIds: List<String>? = null
)
