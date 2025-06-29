package DI.Models.GroupFund

data class UpdateGroupFundDto(
    val groupFundID: String,
    val description: String? = null,
    val savingGoal: Double
)