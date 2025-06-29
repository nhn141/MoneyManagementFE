package DI.Models.GroupTransactionComment

data class GroupTransactionCommentDto(
    val commentId: String,
    val groupTransactionId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val content: String,
    val createdAt: String,
    val updatedAt: String?
)