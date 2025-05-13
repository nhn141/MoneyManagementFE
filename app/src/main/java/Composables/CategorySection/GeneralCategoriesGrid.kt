package DI.Composables.CategorySection

import DI.Composables.TransactionSection.TransactionIconButton
import DI.Models.Category.Category
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CategoriesGrid(
    navController: NavController,
    categories: List<Category>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(40.dp))
            .background(Color.White),
        contentPadding = PaddingValues(
            top = 64.dp,
            bottom = 32.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            GeneralCategoryButton(
                category = category,
                onClick = {
                    navController.navigate("category_specific_type")
                }
            )
        }
    }
}
