package DI.ViewModels

import DI.Composables.TransactionSection.GeneralTransactionItem
import DI.Composables.TransactionSection.getGeneralTransactionData
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor() : ViewModel() {

    private val _selectedType = mutableStateOf<String>("All")
    val selectedType: State<String> = _selectedType

    private val allTransactions = getGeneralTransactionData()

    val filteredTransactions: State<Map<String, List<GeneralTransactionItem>>> = derivedStateOf {
        val filtered = when (_selectedType.value) {
            "Income" -> allTransactions.filter { it.isIncome }
            "Expense" -> allTransactions.filter { !it.isIncome }
            else -> allTransactions
        }

        // Group theo tháng
        filtered.groupBy { item ->
            if (item.timestamp.contains("April")) "April"
            else if (item.timestamp.contains("March")) "March"
            else "Other"
        }
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = if (_selectedType.value == type) "All" else type
    }
}
