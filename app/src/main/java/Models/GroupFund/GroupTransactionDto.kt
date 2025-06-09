package DI.Models.GroupFund

data class GroupTransactionDto(
    val groupTransactionID: String,
    val groupFundID: String,
    val userWalletID: String,
    val userCategoryID: String,
    val amount: Double,
    val description: String?,
    val transactionDate: String,
    val type: String
)