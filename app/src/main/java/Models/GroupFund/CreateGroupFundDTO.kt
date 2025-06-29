package DI.Models.GroupFund

data class CreateGroupFundDto(
    val groupID: String,
    val description: String? = null,
    val savingGoal: Double
)