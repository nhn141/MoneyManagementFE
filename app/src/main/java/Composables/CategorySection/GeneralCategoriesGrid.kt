package DI.Composables.CategorySection

import DI.Models.Category.Category
import DI.Models.Category.CategoryIconStorage
import DI.ViewModels.CategoryViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneymanagement_frontend.R

@Composable
fun CategoriesGrid(
    navController: NavController
) {
    val viewModel: CategoryViewModel = hiltViewModel()
    val categoriesResult by viewModel.categories.collectAsState()
    val categories = categoriesResult?.getOrNull() ?: emptyList()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val iconStorage = remember { CategoryIconStorage(context) }
    var selectedIcon by remember { mutableIntStateOf(R.drawable.ic_more) }
    val addResult by viewModel.addCategoryResult.collectAsState()
    val updateResult by viewModel.updateCategoryResult.collectAsState()
    val deleteResult by viewModel.deleteCategoryResult.collectAsState()

    LaunchedEffect(addResult) {
        addResult?.let {
            if (it.isSuccess) {
                val addedCategory = it.getOrNull()
                Toast.makeText(context, "Category added successfully", Toast.LENGTH_SHORT).show()
                addedCategory?.let { cat ->
                    iconStorage.saveIcon(cat.categoryID, selectedIcon)
                }
                viewModel.getCategories()
            } else {
                Toast.makeText(context, "Failed to add category", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearAddCategoryResult()
        }
    }

    LaunchedEffect(updateResult) {
        updateResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Category updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update category", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearUpdateCategoryResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Delete failed. Please remove all transactions under this category first.", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearDeleteCategoryResult()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
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
                },
                onDelete = { categoryToDelete ->
                    viewModel.deleteCategory(categoryToDelete.categoryID)
                }
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
                    viewModel.addCategory(newCategory)
                    showDialog = false
                }
            )
        }
    }
}

