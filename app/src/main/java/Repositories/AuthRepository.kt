package Repositories

import API.ApiService
import DI.Models.Auth.SignInRequest
import DI.Models.Auth.SignUpRequest
import DI.Models.Auth.RefreshTokenRequest
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val request = SignInRequest(email, password)
            val response = apiService.signIn(request)

            if (response.isSuccessful) {
                val token = response.body()?.string()
                if (!token.isNullOrEmpty()) {
                    Result.success(token)
                } else {
                    Result.failure(Exception("Empty token received"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed: $error"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(firstName: String, lastName: String, email: String, password: String, confirmPassword: String): Result<String> {
        return try {

            val request = SignUpRequest(firstName, lastName, email, password, confirmPassword)
            val response = apiService.signUp(request)

            if(response.isSuccessful) {
                val result = response.body()?.string()
                if (!result.isNullOrEmpty()) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Empty result received"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Request for refresh token failed: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(token: String): Result<String> {
        return try {
            val request = RefreshTokenRequest(token)
            val response = apiService.refreshToken(request)

            if (response.isSuccessful) {
                val refreshToken = response.body()?.string()
                if (!refreshToken.isNullOrEmpty()) {
                    Result.success(refreshToken)
                } else {
                    Result.failure(Exception("Empty refresh token received"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Request for refresh token failed: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
