package DI.Composables.CategorySection

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController


@Composable
fun AddTransactionScreen(navController: NavController) {
    GeneralTemplate(
        contentHeader = { AddTransactionHeaderSection(navController) },
        contentBody = { TransactionForm() },
        fraction = 0.14f,
    )
}
@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    val navController = rememberNavController()
    AddTransactionScreen(navController = navController)
}