package DI.Repositories

import API.ApiService
import DI.Composables.NewsFeedSection.NewsFeedHelper
import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.CreateCommentRequest
import DI.Models.NewsFeed.NewsFeedResponse
import DI.Models.NewsFeed.Post
import DI.Models.NewsFeed.PostDetail
import DI.Models.NewsFeed.ResultState
import android.net.Uri
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

    suspend fun createPost(content: String, category: String = "general", fileUri: Uri?): ResultState<Post> {
        return try {
            val contentPart = newsFeedHelper.createTextRequestBody(content)
            val categoryPart = newsFeedHelper.createTextRequestBody(category)
            val filePart = fileUri?.let { uri ->
                newsFeedHelper.createMultipartFromUri(uri)
            }

            val response = apiService.createPost(content, category, filePart)

            if (response.isSuccessful) {
                // Cleanup temp files sau khi upload thành công
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

}