package DI.Repositories

import API.ApiService
import DI.Models.Category.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getTransactions(): Result<List<Transaction>> {
        return try {
            val response = apiService.getTransactions()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val response = apiService.createTransaction(transaction)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create transaction"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}