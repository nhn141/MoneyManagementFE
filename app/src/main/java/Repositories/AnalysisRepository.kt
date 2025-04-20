package DI.Repositories

import API.ApiService
import DI.Models.Analysis.TransactionByCalendar
import DI.Models.Analysis.WeeklyTransactions
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getMockWeeklyTransactions(): Result<WeeklyTransactions> {
        return try {
            val mockTransactions = listOf(
                TransactionByCalendar(
                    title = "Grocery Shopping",
                    date = "2025-04-14",
                    time = "10:00 AM",
                    dayOfWeek = "Monday",
                    amount = 50.0,
                    type = "Expense",
                    category = "Groceries"
                ),
                TransactionByCalendar(
                    title = "Salary",
                    date = "2025-04-15",
                    time = "9:00 AM",
                    dayOfWeek = "Tuesday",
                    amount = 1500.0,
                    type = "Income",
                    category = "Salary"
                ),
                TransactionByCalendar(
                    title = "Electric Bill",
                    date = "2025-04-16",
                    time = "2:00 PM",
                    dayOfWeek = "Wednesday",
                    amount = 100.0,
                    type = "Expense",
                    category = "Utilities"
                ),
                TransactionByCalendar(
                    title = "Freelance Work",
                    date = "2025-04-17",
                    time = "6:00 PM",
                    dayOfWeek = "Thursday",
                    amount = 300.0,
                    type = "Income",
                    category = "Freelance"
                )
            )

            val totalIncome = mockTransactions.filter { it.type == "Income" }.sumOf { it.amount }
            val totalExpense = mockTransactions.filter { it.type == "Expense" }.sumOf { it.amount }

            val mockResult = WeeklyTransactions(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                transactions = mockTransactions
            )

            Result.success(mockResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}