package DI.ViewModels

import DI.Composables.TransactionSection.GeneralTransactionItem
import DI.Composables.TransactionSection.toGeneralTransactionItem
import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Transaction.CashFlow
import DI.Models.Transaction.TransactionSearchRequest
import DI.Models.UiEvent.UiEvent
import DI.Repositories.CategoryRepository
import DI.Repositories.TransactionRepository
import Utils.StringResourceProvider
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Enum để xác định loại hành động giao dịch
enum class TransactionAction {
    DELETE // Có thể thêm CREATE, UPDATE nếu cần
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    private val _selectedType = mutableStateOf("All")
    val selectedType: State<String> = _selectedType

    private val _allTransactions = mutableStateOf<List<GeneralTransactionItem>>(emptyList())
    val allTransactions: List<GeneralTransactionItem> get() = _allTransactions.value

    private val _originalTransactions = mutableStateOf<List<GeneralTransactionItem>>(emptyList())

    private val _searchParams = mutableStateOf<TransactionSearchRequest?>(null)
    val searchParams: State<TransactionSearchRequest?> = _searchParams

    private val _cashFlow = mutableStateOf<CashFlow?>(null)
    val cashFlow: State<CashFlow?> = _cashFlow

    val filteredTransactions: State<List<GeneralTransactionItem>> = derivedStateOf {
        when (_selectedType.value) {
            "Income" -> allTransactions.filter { it.isIncome }
            "Expense" -> allTransactions.filter { !it.isIncome }
            else -> allTransactions
        }
    }

    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: List<Category> get() = _categories.value

    private val _selectedTransaction = mutableStateOf<Transaction?>(null)
    val selectedTransaction: State<Transaction?> = _selectedTransaction

    // Flow để phát các sự kiện UI (như thông báo)
    internal val _transactionEvent = MutableSharedFlow<UiEvent>()
    val transactionEvent: SharedFlow<UiEvent> = _transactionEvent.asSharedFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            if (result.isSuccess) {
                _categories.value = result.getOrThrow()
                fetchTransactions()
            } else {
                Log.e(
                    "TransactionScreenVM",
                    "Load categories failed: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            val result = transactionRepository.getTransactions()
            if (result.isSuccess) {
                val transactions = result.getOrThrow().map { it.toGeneralTransactionItem() }
                    .sortedByDescending { it.timestamp }
                _allTransactions.value = transactions
                _originalTransactions.value = transactions
                calculateCashFlow(result.getOrNull() ?: emptyList())
            }
        }
    }

    private fun calculateCashFlow(allTransactions: List<Transaction>) {
        if (allTransactions.isEmpty()) {
            return
        }
        val totalIncome = allTransactions.filter { it.type.equals("Income", ignoreCase = true) }
            .sumOf { it.amount }
        val totalExpense = allTransactions.filter { it.type.equals("Expense", ignoreCase = true) }
            .sumOf { it.amount }
        val netCashFlow = totalIncome - totalExpense

        val newCashFlow = CashFlow(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netCashFlow = netCashFlow
        )
        _cashFlow.value = newCashFlow
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
                transactionID = "hello",
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

    fun loadTransactionById(id: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = transactionRepository.getTransactionById(id)
            if (result.isSuccess) {
                _selectedTransaction.value = result.getOrThrow()
                onResult(true)
            } else {
                Log.e(
                    "TransactionVM",
                    "Failed to load transaction by ID: ${result.exceptionOrNull()?.message}"
                )
                _selectedTransaction.value = null
                onResult(false)
            }
        }
    }

    fun updateTransaction(
        transactionID: String,
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
                transactionID = transactionID,
                amount = amount,
                description = description,
                categoryID = categoryId,
                walletID = walletId,
                type = type,
                transactionDate = transactionDate
            )

            val result = transactionRepository.updateTransaction(transaction)
            onResult(result.isSuccess)

            if (result.isSuccess) {
                fetchTransactions()
                onResult(true)
            } else {
                Log.e("TransactionVM", "Update failed: ${result.exceptionOrNull()?.message}")
                onResult(false)
            }
        }
    }

    fun deleteTransaction(id: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = transactionRepository.deleteTransaction(id)
            if (result.isSuccess) {
                fetchTransactions()
                onResult(true)
            } else {
                Log.e("TransactionVM", "Delete failed: ${result.exceptionOrNull()?.message}")
                onResult(false)
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
            _searchParams.value = request
            Log.d("SearchTransactions", "Searching with params: $request")
            val result = transactionRepository.searchTransactions(request)
            if (result.isSuccess) {
                _allTransactions.value = result.getOrThrow().map { it.toGeneralTransactionItem() }
            } else {
                Log.e("SearchTransactions", result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetSearch() {
        _searchParams.value = null
        _allTransactions.value = _originalTransactions.value
        _selectedType.value = "All"
    }

    fun notifyShareAction(success: Boolean, isGroup: Boolean) {
        viewModelScope.launch {
            _transactionEvent.emit(
                UiEvent.ShowMessage(
                    stringProvider.getString(
                        if (success) {
                            if (isGroup) R.string.share_to_group_success else R.string.share_to_friend_success
                        } else {
                            if (isGroup) R.string.share_to_group_failed else R.string.share_to_friend_failed
                        }
                    )
                )
            )
        }
    }

    fun notifyTransactionAction(action: TransactionAction, success: Boolean) {
        viewModelScope.launch {
            _transactionEvent.emit(
                UiEvent.ShowMessage(
                    stringProvider.getString(
                        when (action) {
                            TransactionAction.DELETE -> if (success) R.string.transaction_deleted_success else R.string.transaction_deleted_failed
                        }
                    )
                )
            )
        }
    }
}