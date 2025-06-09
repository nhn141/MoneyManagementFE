package DI.Models.Group

data class AdminLeaveResult(
    val success: Boolean,
    val action: String,
    val groupId: String,
    val newAdminId: String?
)
