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
    private var _categories = MutableStateFlow<Result<List<Category>>?>(null)
    val categories: StateFlow<Result<List<Category>>?> = _categories

    fun getCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            _categories.value = result
        }
    }
}