package DI.Models.GroupTransaction

data class UpdateGroupTransactionDto(
    val groupTransactionID: String,
    val userWalletID: String,
    val userCategoryID: String,
    val amount: Double,
    val description: String?,
    val transactionDate: String,
    val type: String = "expense"
)