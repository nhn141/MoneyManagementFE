package DI.ViewModels

import DI.Repositories.AnalysisRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository
) : ViewModel() {
    private var _periodGraph = MutableStateFlow<Result<PeriodGraph>?>(null)
    val periodGraph: StateFlow<Result<PeriodGraph>?> = _periodGraph

    init {
        getMockWeeklyTransactions()
    }

    private fun getMockWeeklyTransactions() {
        viewModelScope.launch {
            val result = analysisRepository.getMockWeeklyTransactions()
            result.onSuccess { weeklyTransactions ->
                // Convert transactions into PeriodData
                val periodData = PeriodData(
                    labels = weeklyTransactions.transactions.map { it.dayOfWeek },
                    income = weeklyTransactions.transactions.map { if(it.type == "Income") it.amount else 0.0},
                    expenses = weeklyTransactions.transactions.map { if(it.type == "Expense") it.amount else 0.0},
                    totalIncome = weeklyTransactions.totalIncome,
                    totalExpenses = weeklyTransactions.totalExpense
                )

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Weekly", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure { e ->
                _periodGraph.value = Result.failure(e)
            }
        }
    }

    private fun loadMockData() {
        val mockData = mapOf(
            "Daily" to PeriodData(
                labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                income = listOf(100.0, 120.0, 90.0, 110.0, 130.0, 95.0, 105.0),
                expenses = listOf(80.0, 95.0, 75.0, 90.0, 100.0, 85.0, 95.0),
                totalIncome = 700.0,
                totalExpenses = 420.0
            ),
            "Weekly" to PeriodData(
                labels = listOf("Week 1", "Week 2", "Week 3", "Week 4"),
                income = listOf(2000.0, 1800.0, 2200.0, 2100.0),
                expenses = listOf(1500.0, 1600.0, 1700.0, 1800.0),
                totalIncome = 8000.0,
                totalExpenses = 7200.0
            ),
            "Monthly" to PeriodData(
                labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                income = listOf(10000.0, 11000.0, 12000.0, 13000.0, 14000.0, 18000.0),
                expenses = listOf(8000.0, 9000.0, 10000.0, 11000.0, 12000.0, 15000.0),
                totalIncome = 100000.0,
                totalExpenses = 120000.0
            ),
            "Yearly" to PeriodData(
                labels = listOf("2023", "2024", "2025", "2026"),
                income = listOf(50000.0, 55000.0, 60000.0, 65000.0),
                expenses = listOf(45000.0, 50000.0, 55000.0, 60000.0),
                totalIncome = 200000.0,
                totalExpenses = 240000.0
            )
        )
    }
}

data class PeriodData(
    val labels: List<String>,
    val income: List<Double>,
    val expenses: List<Double>,
    val totalIncome: Double,
    val totalExpenses: Double
)

data class PeriodGraph(
    val isLoading: Boolean = true,
    val dataByPeriod: Map<String, PeriodData> = emptyMap()
)
