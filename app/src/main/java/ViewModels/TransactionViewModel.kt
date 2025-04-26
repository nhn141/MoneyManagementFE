package DI.ViewModels

import DI.Models.Category.Transaction
import DI.Repositories.TransactionRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _transactions = MutableStateFlow<Result<List<Transaction>>?>(null)
    val transactions: StateFlow<Result<List<Transaction>>?> = _transactions

    fun fetchTransactions() {
        viewModelScope.launch {
            val result = repository.getTransactions()
            _transactions.value = result
        }
    }

    private val _transactionState = MutableStateFlow<Result<Unit>?>(null)
    val transactionState: StateFlow<Result<Unit>?> = _transactionState

    fun createTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val result = repository.createTransaction(transaction)
            _transactionState.value = result
        }
    }
}
