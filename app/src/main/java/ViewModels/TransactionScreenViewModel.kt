package DI.ViewModels

import DI.Composables.TransactionSection.GeneralTransactionItem
import DI.Composables.TransactionSection.toGeneralTransactionItem
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import DI.Models.Category.Transaction
import DI.Models.Transaction.TransactionSearchRequest
import DI.Repositories.TransactionRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class TransactionScreenViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _selectedType = mutableStateOf("All")
    val selectedType: State<String> = _selectedType

    private val _allTransactions = mutableStateOf<List<GeneralTransactionItem>>(emptyList())
    val allTransactions: List<GeneralTransactionItem> get() = _allTransactions.value

    val filteredTransactions: State<List<GeneralTransactionItem>> = derivedStateOf {
        when (_selectedType.value) {
            "Income" -> allTransactions.filter { it.isIncome }
            "Expense" -> allTransactions.filter { !it.isIncome }
            else -> allTransactions
        }
    }

    init {
        Log.d("TransactionScreenViewModel", "ViewModel initialized")
        fetchTransactions()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            val result = transactionRepository.getTransactions()
            if (result.isSuccess) {
                _allTransactions.value = result.getOrThrow().map { it.toGeneralTransactionItem() }

            }
        }
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = if (_selectedType.value == type) "All" else type
    }

    fun createTransaction(
        amount: Double,
        description: String,
        categoryId: String,
        walletId: String,
        type: String,
        transactionDate: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                transactionID = "",
                amount = amount,
                description = description,
                categoryID = categoryId,
                walletID = walletId,
                type = type,
                transactionDate = transactionDate
            )
            val result = transactionRepository.createTransaction(transaction)
            onResult(result.isSuccess)
            if (result.isSuccess) {
                fetchTransactions()
            }
        }
    }
    fun fetchTransactionsByDateRange(from: String, to: String) {
        viewModelScope.launch {
            val result = transactionRepository.getTransactionsByDateRange(from, to)
            if (result.isSuccess) {
                Log.d("FetchSuccess", result.getOrThrow().toString())
                _allTransactions.value = result.getOrThrow().map { it.toGeneralTransactionItem() }
            } else {
                Log.e("FetchError", result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun searchTransactions(request: TransactionSearchRequest) {
        viewModelScope.launch {
            val result = transactionRepository.searchTransactions(request)
            if (result.isSuccess) {
                _allTransactions.value = result.getOrThrow().map { it.toGeneralTransactionItem() }
            } else {
                Log.e("SearchTransactions", result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

}

