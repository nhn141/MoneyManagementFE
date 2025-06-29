package DI.Repositories

import API.ApiService
import DI.Models.GroupTransaction.CreateGroupTransactionDto
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.GroupTransaction.UpdateGroupTransactionDto
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupTransactionRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getGroupTransactionsByGroupFundId(groupFundId: String): Result<List<GroupTransactionDto>> {
        return try {
            Log.d("GroupTransactionRepo", "Fetching transactions for: $groupFundId")
            val response = apiService.getGroupTransactionsByGroupFundId(groupFundId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupTransactionRepo", "Get failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to get group transactions: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupTransactionRepo", "Exception: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun createGroupTransaction(request: CreateGroupTransactionDto): Result<GroupTransactionDto> {
        return try {
            val response = apiService.createGroupTransaction(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupTransactionRepo", "Create failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to create group transaction: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupTransactionRepo", "Exception during create: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun updateGroupTransaction(id: String, request: UpdateGroupTransactionDto): Result<GroupTransactionDto> {
        return try {
            val response = apiService.updateGroupTransaction(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupTransactionRepo", "Update failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to update group transaction: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupTransactionRepo", "Exception during update: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteGroupTransaction(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteGroupTransaction(id)
            if (response.isSuccessful) {
                Log.d("GroupTransactionRepo", "Transaction deleted successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupTransactionRepo", "Delete failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to delete group transaction: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupTransactionRepo", "Exception during delete: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}
