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

    suspend fun addCategory(category: Category): Result<Category> {
        return try {
            val response = apiService.addCategory(category)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
