package API

import DI.Models.Auth.RefreshTokenRequest
import DI.Models.Auth.SignInRequest
import DI.Models.Auth.SignUpRequest
import DI.Models.Category.Category
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("Accounts/SignUp")
    suspend fun signUp(@Body request: SignUpRequest): Response<ResponseBody>

    @POST("Accounts/SignIn")
    suspend fun signIn(@Body request: SignInRequest): Response<ResponseBody>

    @POST("Accounts/RefreshToken")
    suspend fun refreshToken(@Body token: RefreshTokenRequest): Response<ResponseBody>

    @GET("Categories")
    suspend fun getCategories(): List<Category>




}