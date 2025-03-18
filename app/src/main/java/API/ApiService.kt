package API

import Models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("Users")
    suspend fun getAllUsers(): List<User>

    @POST("Users")
    suspend fun registerUser(@Body user: User): User
}