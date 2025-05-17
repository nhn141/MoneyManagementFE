package DI.Composables.CategorySection

import DI.Models.BalanceInfo
import DI.ViewModels.CategoryViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun GeneralCategoryScreen(navController: NavController) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val categoriesResult = categoryViewModel.categories.collectAsState().value

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
    }

    GeneralTemplate(
        contentHeader = {
            HeaderSection(
                BalanceInfo("$7,783.00", "-$1,187.00", "$20,000.00"),
                navController
            )
        },
        contentBody = {
            categoriesResult?.onSuccess { categories ->
                CategoriesGrid(navController = navController)
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0068FF))
                }
            }
        },
        fraction = 0.35f
    )
}


