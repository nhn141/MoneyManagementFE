package DI.ViewModels

import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Analysis.PeriodData
import DI.Models.Analysis.PeriodGraph
import DI.Repositories.AnalysisRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository
) : ViewModel() {

    private var _periodGraph = MutableStateFlow<Result<PeriodGraph>?>(null)
    val periodGraph: StateFlow<Result<PeriodGraph>?> = _periodGraph.asStateFlow()

    private var _categoryBreakdown = MutableStateFlow<Result<List<CategoryBreakdown>>?>(null)
    val categoryBreakdown: StateFlow<Result<List<CategoryBreakdown>>?> = _categoryBreakdown.asStateFlow()
    init {
        getDailyTransactions()
        getWeeklyTransactions()
        getMonthlyTransactions()
        getYearlyTransactions()
    }

    private fun getDailyTransactions() {
        viewModelScope.launch {
            val result = analysisRepository.getMockDailyTransactions()
            result.onSuccess { dailyTransactions ->
                // Convert transactions into PeriodData
                val periodData = PeriodData(
                    labels = dailyTransactions.transactions.map { it.dayOfWeek },
                    income = dailyTransactions.transactions.map { if(it.type == "Income") it.amount else 0.0},
                    expenses = dailyTransactions.transactions.map { if(it.type == "Expense") it.amount else 0.0},
                    totalIncome = dailyTransactions.totalIncome,
                    totalExpenses = dailyTransactions.totalExpense
                )

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Daily", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    private fun weekNumberConfig(weekNumber: Int): String {
        return when (weekNumber) {
            1 -> "1st Week"
            2 -> "2nd Week"
            3 -> "3rd Week"
            4 -> "4th Week"
            else -> ""
        }
    }

    private fun getWeeklyTransactions() {
        viewModelScope.launch {
            val result = analysisRepository.getMockWeeklyTransactions()
            result.onSuccess { weeklyTransactions ->
                val periodData = PeriodData(
                    labels = weeklyTransactions.transactions.map { weekNumberConfig(it.weekNumber) },
                    income = weeklyTransactions.transactions.map { it.totalIncome },
                    expenses = weeklyTransactions.transactions.map { it.totalExpense },
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
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    private fun getMonthlyTransactions() {
        viewModelScope.launch {
            val result = analysisRepository.getMockMonthlyTransactions()
            result.onSuccess { monthlyTransactions ->
                val periodData = PeriodData(
                    labels = monthlyTransactions.transactions.map { it.month },
                    income = monthlyTransactions.transactions.map { it.totalIncome },
                    expenses = monthlyTransactions.transactions.map { it.totalExpense },
                    totalIncome = monthlyTransactions.totalIncome,
                    totalExpenses = monthlyTransactions.totalExpense
                )

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Monthly", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    private fun getYearlyTransactions() {
        viewModelScope.launch {
            val result = analysisRepository.getMockYearlyTransactions()
            result.onSuccess { yearlyTransactionsList ->
                val periodData = PeriodData(
                    labels = yearlyTransactionsList.map { it.year },
                    income = yearlyTransactionsList.map { it.totalIncome },
                    expenses = yearlyTransactionsList.map { it.totalExpense },
                    totalIncome = yearlyTransactionsList.sumOf { it.totalIncome },
                    totalExpenses = yearlyTransactionsList.sumOf { it.totalExpense }
                )

                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                val updatedMap = currentMap.toMutableMap().apply {
                    put("Yearly", periodData)
                }

                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = updatedMap
                    )
                )
            }.onFailure {
                val currentMap = _periodGraph.value?.getOrNull()?.dataByPeriod ?: emptyMap()
                _periodGraph.value = Result.success(
                    PeriodGraph(
                        isLoading = false,
                        dataByPeriod = currentMap // preserve previously fetched data
                    )
                )
            }
        }
    }

    fun getCategoryBreakdown(startDate: String, endDate: String) {
        viewModelScope.launch {
            Log.d("CategoryBreakdown", "Fetching category breakdown data for dates: $startDate to $endDate")
            val result = analysisRepository.getCategoryBreakdown(startDate, endDate)
            _categoryBreakdown.value = result
            Log.d("CategoryBreakdown", "Category breakdown data fetched successfully")
        }
    }

    fun getMockCategoryBreakdown(startDate: String, endDate: String) {
        viewModelScope.launch {
            val result = analysisRepository.getCategoryBreakdownMock(startDate, endDate)
            _categoryBreakdown.value = result
        }
    }
}




