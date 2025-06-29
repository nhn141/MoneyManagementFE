package DI.Repositories

import API.ApiService
import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.GroupFundDto
import DI.Models.GroupFund.UpdateGroupFundDto
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupFundRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getGroupFundsByGroupId(groupId: String): Result<List<GroupFundDto>> {
        return try {
            val response = apiService.getGroupFundsByGroupId(groupId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupFundRepository", "Get failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to get group funds: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupFundRepository", "Exception: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun createGroupFund(request: CreateGroupFundDto): Result<GroupFundDto> {
        return try {
            val response = apiService.createGroupFund(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupFundRepository", "Create failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to create group fund: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupFundRepository", "Exception during create: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun updateGroupFund(id: String, request: UpdateGroupFundDto): Result<GroupFundDto> {
        return try {
            val response = apiService.updateGroupFund(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupFundRepository", "Update failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to update group fund: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupFundRepository", "Exception during update: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteGroupFund(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteGroupFund(id)
            if (response.isSuccessful) {
                Log.d("GroupFundRepository", "Group fund deleted successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GroupFundRepository", "Delete failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to delete group fund: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("GroupFundRepository", "Exception during delete: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}
