package DI.Composables.CategorySection


import DI.ViewModels.CategoryViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun CategoryScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00D09E),
                        Color(0xFFDFF7E2),
                        Color(0xFFB5F2D0)
                    )
                )
            )
    ) {
        // Enhanced Header
        CategoryHeader(navController = navController)

        // Categories Grid
        CategoriesGrid(
            navController = navController,
            categoryViewModel = categoryViewModel
        )
    }
}