package Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CategoryScreen(viewModel: GeneralCategoryViewModel, navController: NavController) {
    val balance = viewModel.balanceInfo.value
    val categories = viewModel.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00D09E))
    ) {
        HeaderSection(BalanceInfo("$7,783.00", "-$1,187.00", "$20,000.00"), navController) // A composable for the header (title, back button, etc.)
        Spacer(modifier = Modifier.height(16.dp))
        CategoriesGrid(categories, navController)
    }
}