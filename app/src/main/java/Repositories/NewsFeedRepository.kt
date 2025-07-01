package DI.Repositories

import API.ApiService
import DI.Composables.NewsFeedSection.NewsFeedHelper
import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.CreateCommentRequest
import DI.Models.NewsFeed.NewsFeedResponse
import DI.Models.NewsFeed.Post
import DI.Models.NewsFeed.PostDetail
import DI.Models.NewsFeed.ReplyCommentRequest
import DI.Models.NewsFeed.ReplyCommentResponse
import DI.Models.NewsFeed.ResultState
import DI.Models.NewsFeed.UpdatePostTargetRequest
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsFeedRepository @Inject constructor(
    private val apiService: ApiService,
    private val newsFeedHelper: NewsFeedHelper)
{
    suspend fun getNewsFeed(page: Int, pageSize: Int): ResultState<NewsFeedResponse> {
        return try {
            val response = apiService.getNewsFeed(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultState.Success(it)
                } ?: ResultState.Error("Empty response body")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                ResultState.Error("Error ${response.code()}: $errorMsg")
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun createPost(
        content: String,
        category: String = "general",
        fileUri: Uri?,
        targetType: Int?,
        targetGroupIds: String?
    ): ResultState<Post> {
        return try {
            val filePart = fileUri?.let { uri ->
                newsFeedHelper.createMultipartFromUri(uri)
            } ?: MultipartBody.Part.createFormData(
                name = "file",
                filename = "",
                body = "".toRequestBody("application/octet-stream".toMediaTypeOrNull())
            )

            val response = apiService.createPost(
                content = content,
                category = category,
                targetType = targetType,
                targetGroupIds = targetGroupIds,
                file = filePart
            )

            if (response.isSuccessful) {
                newsFeedHelper.cleanupTempFiles()
                response.body()?.let {
                    ResultState.Success(it)
                } ?: ResultState.Error("Empty response body")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                ResultState.Error("Error ${response.code()}: $errorMsg")
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun likePost(postId: String): ResultState<Unit> {
        return try {
            val response = apiService.likePost(postId)
            if (response.isSuccessful) {
                ResultState.Success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                ResultState.Error("Like failed: ${response.code()} - $errorMsg")
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun unlikePost(postId: String): ResultState<Unit> {
        return try {
            val response = apiService.unlikePost(postId)
            if (response.isSuccessful) {
                ResultState.Success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                ResultState.Error("Unlike failed: ${response.code()} - $errorMsg")
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun createComment(request: CreateCommentRequest): ResultState<Comment> {
        return try {
            val response = apiService.createComment(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultState.Success(it)
                } ?: ResultState.Error("Empty response")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                ResultState.Error("Create comment failed: ${response.code()} - $errorMsg")
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun deleteComment(commentId: String): ResultState<Unit> {
        return try {
            val response = apiService.deleteComment(commentId)
            if (response.isSuccessful) {
                ResultState.Success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                ResultState.Error("Delete comment failed: ${response.code()} - $errorMsg")
            }
        } catch (e: IOException) {
            ResultState.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ResultState.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun getPostDetail(postId: String): ResultState<PostDetail> {
        return try {
            val response = apiService.getPostDetail(postId)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Error("Exception occurred: ${e.message}")
        }
    }

    suspend fun updatePostTarget(postId: String, targetType: Int, targetGroupIds: List<String>?): ResultState<Unit> {
        return try {
            val request = UpdatePostTargetRequest(
                targetType = targetType,
                targetGroupIds = targetGroupIds
            )
            Log.d("Repository", "Sending request: postId=$postId, targetType=$targetType, targetGroupIds=$targetGroupIds")
            val response = apiService.updatePostTarget(postId, request)
            Log.d("Repository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("Repository", "Update successful for postId=$postId")
                ResultState.Success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.e("Repository", "Update failed: code=${response.code()}, error=$errorMsg")
                ResultState.Error("Update target failed: ${response.code()} - $errorMsg")
            }
        } catch (e: IOException) {
            Log.e("Repository", "Network error: ${e.message}")
            ResultState.Error("Không có kết nối mạng: ${e.message}")
        } catch (e: Exception) {
            Log.e("Repository", "Unexpected error: ${e.message}")
            ResultState.Error("Lỗi không xác định: ${e.message}")
        }
    }

    suspend fun replyToComment(request: ReplyCommentRequest): Result<ReplyCommentResponse> {
        return try {
            Log.d("ReplyToComment", "Starting replyToComment with request: $request")
            val response = apiService.replyToComment(request)
            Log.d("ReplyToComment", "API response: code=${response.code()}, message=${response.message()}, body=${response.body()}")
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("ReplyToComment", "Reply successful: $it")
                    Result.success(it)
                } ?: run {
                    Log.e("ReplyToComment", "Empty response body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ReplyToComment", "Reply failed: code=${response.code()}, message=${response.message()}, errorBody=$errorBody")
                Result.failure(Exception("Reply failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("ReplyToComment", "Exception in replyToComment: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteReply(replyId: String): Result<Unit> {
        return try {
            val response = apiService.deleteReply(replyId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}