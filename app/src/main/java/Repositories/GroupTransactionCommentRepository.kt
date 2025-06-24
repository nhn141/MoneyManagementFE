package DI.Repositories

import API.ApiService
import DI.Models.GroupTransactionComment.CreateGroupTransactionCommentDto
import DI.Models.GroupTransactionComment.GroupTransactionCommentDto
import DI.Models.GroupTransactionComment.UpdateGroupTransactionCommentDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupTransactionCommentRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getComments(transactionId: String): Result<List<GroupTransactionCommentDto>> {
        return try {
            val response = apiService.getGroupTransactionComments(transactionId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(dto: CreateGroupTransactionCommentDto): Result<GroupTransactionCommentDto> {
        return try {
            val response = apiService.addGroupTransactionComment(dto)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateComment(dto: UpdateGroupTransactionCommentDto): Result<GroupTransactionCommentDto> {
        return try {
            val response = apiService.updateGroupTransactionComment(dto)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            val response = apiService.deleteGroupTransactionComment(commentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
