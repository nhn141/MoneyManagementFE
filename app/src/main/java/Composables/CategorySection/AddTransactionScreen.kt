package DI.Composables.CategorySection

import DI.ViewModels.TransactionScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import DI.Composables.GeneralTemplate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreen(navController: NavController) {
    val viewModel: TransactionScreenViewModel = hiltViewModel()
    var type by remember { mutableStateOf("Expense") }
    GeneralTemplate(
        contentHeader = { AddTransactionHeaderSection(navController, type) { newType -> type = newType } },
        contentBody = { TransactionForm(viewModel, navController, type, onTypeChange = { type = it }) },
        fraction = 0.14f,
    )
}