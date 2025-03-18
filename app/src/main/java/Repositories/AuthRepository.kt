package Repositories

import API.ApiService
import Models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.getAllUsers().find { it.email == email && it.password == password }
            if (response != null) Result.success(response) else Result.failure(Exception("Invalid credentials"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val newUser = User(name, email, password)
            val response = apiService.registerUser(newUser)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}