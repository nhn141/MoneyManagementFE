package DI.Composables.CategorySection

import DI.Models.Category.Category
import DI.Models.Category.CategoryIconStorage
import DI.ViewModels.CategoryViewModel
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

@Composable
fun CategoriesGrid(
    navController: NavController,
    categoryViewModel: CategoryViewModel
) {
    val categoriesResult by categoryViewModel.categories.collectAsState()
    val categories = categoriesResult?.getOrNull() ?: emptyList()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val iconStorage = remember { CategoryIconStorage(context) }
    var selectedIcon by remember { mutableStateOf("ic_more") }
    val addResult by categoryViewModel.addCategoryResult.collectAsState()
    val updateResult by categoryViewModel.updateCategoryResult.collectAsState()
    val deleteResult by categoryViewModel.deleteCategoryResult.collectAsState()

    val listState = rememberLazyGridState()
    val animatedVisibilityState = remember { Animatable(0f) }

    LaunchedEffect(categories) {
        animatedVisibilityState.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(addResult) {
        addResult?.let {
            if (it.isSuccess) {
                val addedCategory = it.getOrNull()
                Toast.makeText(context, "Category added successfully", Toast.LENGTH_SHORT).show()
                addedCategory?.let { cat ->
                    iconStorage.saveIcon(cat.categoryID, selectedIcon)
                }
                categoryViewModel.getCategories()
            } else {
                Toast.makeText(context, "Failed to add category", Toast.LENGTH_SHORT).show()
            }
            categoryViewModel.clearAddCategoryResult()
        }
    }

    LaunchedEffect(updateResult) {
        updateResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Category updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update category", Toast.LENGTH_SHORT).show()
            }
            categoryViewModel.clearUpdateCategoryResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Delete failed. Please remove all transactions under this category first.", Toast.LENGTH_SHORT).show()
            }
            categoryViewModel.clearDeleteCategoryResult()
        }
    }

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
            firstVisibleItem?.let { item ->
                val offset = item.offset.y
                val threshold = item.size.height / 2
                val targetIndex = if (offset > threshold) item.index + 1 else item.index
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDFF7E2),
                        Color(0xFFB5F2D0),
                        Color(0xFF00D09E).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = animatedVisibilityState.value },
            contentPadding = PaddingValues(
                top = 40.dp,
                bottom = 40.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                GeneralCategoryButton(
                    category = category,
                    onClick = {
                        navController.navigate("category_specific_type")
                    },
                    onDelete = { categoryToDelete ->
                        categoryViewModel.deleteCategory(categoryToDelete.categoryID)
                    },
                    viewModel = categoryViewModel
                )
            }
            item {
                AddCategoryButton(
                    onAddClick = {
                        showDialog = true
                    }
                )

                AddCategoryDialog(
                    showDialog = showDialog,
                    selectedIcon = selectedIcon,
                    onIconChange = { selectedIcon = it },
                    onDismiss = { showDialog = false },
                    onSave = { categoryName, _ ->
                        val newCategory = Category(
                            categoryID = "",
                            name = categoryName,
                            createdAt = ""
                        )
                        categoryViewModel.addCategory(newCategory)
                        showDialog = false
                    }
                )
            }
        }

        // Floating gradient overlay at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF1FFF3).copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.TopCenter)
        )
    }
}

