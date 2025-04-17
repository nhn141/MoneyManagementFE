package DI.ViewModels

import DI.Models.BalanceInfo
import DI.Composables.CategorySection.Category
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.moneymanagement_frontend.R

class GeneralCategoryViewModel : ViewModel() {
    val categories = mutableListOf(
        Category("Food", R.drawable.ic_food),
        Category("Transport", R.drawable.ic_transport),
        Category("Medicine", R.drawable.ic_medicine),
        Category("Groceries", R.drawable.ic_groceries),
        Category("Rent", R.drawable.ic_rent),
        Category("Gifts", R.drawable.ic_gifts),
        Category("Savings", R.drawable.ic_savings),
        Category("Entertainment", R.drawable.ic_entertainment),
        Category("More", R.drawable.ic_more)
    )

    val balanceInfo: MutableState<BalanceInfo> = mutableStateOf(
        BalanceInfo("$7,783.00", "-$1,187.40", "$20,000.00")
    )

    fun updateBalance(newBalance: String, newExpense: String, newBudget: String) {
        balanceInfo.value = BalanceInfo(newBalance, newExpense, newBudget)
    }
}