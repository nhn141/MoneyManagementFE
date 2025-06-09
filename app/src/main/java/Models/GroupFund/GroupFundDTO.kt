package DI.Models.GroupFund

data class GroupFundDto(
    val groupFundID: String,
    val groupID: String,
    val totalFundsIn: Double,
    val totalFundsOut: Double,
    val balance: Double,
    val createdAt: String,
    val updatedAt: String,
    // val description: String,
    // val groupTransactions: List<GroupTransactionDto>? = null
)