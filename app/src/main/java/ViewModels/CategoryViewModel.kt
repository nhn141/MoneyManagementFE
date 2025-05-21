package DI.ViewModels

import DI.Models.Category.Category
import DI.Repositories.CategoryRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {
    private val _categories = MutableStateFlow<Result<List<Category>>?>(null)
    val categories: StateFlow<Result<List<Category>>?> = _categories

    private val _addCategoryResult = MutableStateFlow<Result<Category>?>(null)
    val addCategoryResult: StateFlow<Result<Category>?> = _addCategoryResult

    private val _selectedCategory = MutableStateFlow<Result<Category>?>(null)
    val selectedCategory: StateFlow<Result<Category>?> = _selectedCategory

    private val _updateCategoryResult = MutableStateFlow<Result<Category>?>(null)
    val updateCategoryResult: StateFlow<Result<Category>?> = _updateCategoryResult

    private val _deleteCategoryResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteCategoryResult: StateFlow<Result<Unit>?> = _deleteCategoryResult

    fun getCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            _categories.value = result
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            val result = repository.addCategory(category)
            _addCategoryResult.value = result
            if (result.isSuccess) {
                getCategories()
            }
        }
    }

    fun getCategoryById(id: String) {
        viewModelScope.launch {
            val result = repository.getCategoryById(id)
            _selectedCategory.value = result
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            val result = repository.updateCategory(category)
            _updateCategoryResult.value = result
            if (result.isSuccess) {
                getCategories()
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val result = repository.deleteCategory(id)
            _deleteCategoryResult.value = result
            if (result.isSuccess) {
                getCategories()
            }
        }
    }

    fun clearAddCategoryResult() {
        _addCategoryResult.value = null
    }

    fun clearUpdateCategoryResult() {
        _updateCategoryResult.value = null
    }

    fun clearDeleteCategoryResult() {
        _deleteCategoryResult.value = null
    }
}
