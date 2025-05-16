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

    fun clearAddCategoryResult() {
        _addCategoryResult.value = null
    }
}
