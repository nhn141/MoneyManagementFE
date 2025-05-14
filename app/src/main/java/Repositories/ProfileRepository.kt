package DI.Repositories

import API.ApiService
import DI.Models.UserInfo.Profile
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
){
    suspend fun getProfile(): Result<Profile> {
        return try {
            val response = apiService.getProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadAvatar(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadAvatar(body)

            if (response.isSuccessful) {
                // Optional: parse body or return confirmation
                Result.success("Upload successful")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UploadError", "Failed with code: ${response.code()}, error: $errorBody")
                Result.failure(Exception("Upload failed: ${response.code()} ${errorBody.orEmpty()}"))
            }

        } catch (e: Exception) {
            Log.e("UploadException", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

}