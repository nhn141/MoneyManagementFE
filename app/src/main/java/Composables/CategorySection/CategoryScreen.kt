package DI.Composables.CategorySection

import DI.Composables.GeneralTemplate
import DI.Models.BalanceInfo
import DI.ViewModels.CategoryViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CategoryScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    balanceInfo: BalanceInfo
) {
    GeneralTemplate(
        contentHeader = { HeaderSection(balanceInfo, navController) },
        contentBody = {
            CategoriesGrid(
                navController = navController,
                categoryViewModel = categoryViewModel
            )
        },
    )
}