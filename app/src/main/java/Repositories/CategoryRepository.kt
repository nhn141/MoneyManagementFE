package DI.Repositories

import API.ApiService
import DI.Models.Category.Category
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getCategories() : Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategory(category: Category): Result<Unit> {
        return try {
            val response = apiService.createCategory(category)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
