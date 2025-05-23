package DI.Composables.CategorySection

import DI.Composables.GeneralTemplate
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun AddTransactionScreen(navController: NavController) {
    GeneralTemplate(
        contentHeader = { AddTransactionHeaderSection(navController) },
        contentBody = { TransactionForm() },
        fraction = 0.14f,
    )
}