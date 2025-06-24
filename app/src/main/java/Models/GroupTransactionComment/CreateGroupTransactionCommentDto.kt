package DI.Models.GroupTransactionComment

data class CreateGroupTransactionCommentDto(
    val groupTransactionId: String,
    val content: String
)