package DI.Models.GroupTransaction

data class GroupTransactionDto(
    val groupTransactionID: String,
    val groupFundID: String,
    val userWalletID: String,
    val userWalletName: String?,
    val userCategoryID: String,
    val userCategoryName: String?,
    val amount: Double,
    val description: String,
    val transactionDate: String,
    val type: String,
    val createdByUserId: String,
    val createdByUserName: String?,
    val createdAt: String
)