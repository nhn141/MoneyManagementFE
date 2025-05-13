package API

import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Auth.RefreshTokenRequest
import DI.Models.Auth.SignInRequest
import DI.Models.Auth.SignUpRequest
import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Ocr.OcrData
import DI.Models.Wallet
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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




    @GET("Wallets")
    suspend fun getWallets(): List<Wallet>

    @GET("Transactions")
    suspend fun getTransactions(): List<Transaction>

    @POST("Categories")
    suspend fun createCategory(@Body category: Category): Response<ResponseBody>

    @POST("Wallets")
    suspend fun createWallet(@Body wallet: Wallet): Response<ResponseBody>

    @POST("Transactions")
    suspend fun createTransaction(@Body transaction: Transaction): Response<ResponseBody>

    @GET("Statistics/category-breakdown")
    suspend fun getCategoryBreakdown(@Query("startDate") startDate: String, @Query("endDate") endDate: String): List<CategoryBreakdown>

    @POST("Gemini/extract-ocr")
    suspend fun extractOcr(@Body ocrString: String): OcrData

    @GET("Messages/chats")
    suspend fun getAllChats(): List<Chat>

    @GET("Messages/{receiverId}")
    suspend fun getChatWithOtherUser(@Path("receiverId") otherUserId: String): List<ChatMessage>
}