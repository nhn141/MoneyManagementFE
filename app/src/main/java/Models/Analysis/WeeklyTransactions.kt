package DI.Models.Analysis

data class WeeklyTransactions(
    val totalIncome: Double,
    val totalExpense: Double,
    val transactions: List<TransactionByCalendar>
)
