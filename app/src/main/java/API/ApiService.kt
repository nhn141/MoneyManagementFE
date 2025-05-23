package API

import DI.Models.Analysis.BarChart.DailySummary
import DI.Models.Analysis.BarChart.MonthlySummary
import DI.Models.Analysis.BarChart.WeeklySummary
import DI.Models.Analysis.BarChart.YearlySummary
import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Auth.RefreshTokenRequest
import DI.Models.Auth.SignInRequest
import DI.Models.Auth.SignUpRequest
import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Chat.LatestChat
import DI.Models.Chat.LatestChatResponses
import DI.Models.Friend.AcceptFriendRequestResponse
import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.AddFriendResponse
import DI.Models.Friend.DeleteFriendResponse
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.Models.Friend.RejectFriendRequestResponse
import DI.Models.Ocr.OcrData
import DI.Models.UserInfo.AvatarUploadResponse
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UpdatedProfile
import DI.Models.Wallet
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Authentication

    @POST("Accounts/SignUp")
    suspend fun signUp(@Body request: SignUpRequest): Response<ResponseBody>

    @POST("Accounts/SignIn")
    suspend fun signIn(@Body request: SignInRequest): Response<ResponseBody>

    @POST("Accounts/RefreshToken")
    suspend fun refreshToken(@Body token: RefreshTokenRequest): Response<ResponseBody>

    // Aggregation

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

    // Ocr

    @POST("Gemini/extract-ocr")
    suspend fun extractOcr(@Body ocrString: String): OcrData

    // Chat

    @GET("Messages/chats")
    suspend fun getAllChats(): List<Chat>

    @GET("Messages/{receiverId}")
    suspend fun getChatWithOtherUser(@Path("receiverId") otherUserId: String): List<ChatMessage>

    @GET("Messages/latest")
    suspend fun getLatestChats(): LatestChatResponses

    @POST("Messages/read/{otherUserId}")
    suspend fun markAllMessagesAsReadFromSingleChat(@Path("otherUserId") otherUserId: String): Response<ResponseBody>

    // Friend

    @GET("Friends")
    suspend fun getAllFriends(): List<Friend>

    @POST("Friends/add")
    suspend fun addFriend(@Body request: AddFriendRequest): Response<AddFriendResponse>

    @GET("Friends/requests")
    suspend fun getFriendRequests(): List<FriendRequest>

    @POST("Friends/accept/{friendId}")
    suspend fun acceptFriendRequest(@Path("friendId") friendId: String): Response<AcceptFriendRequestResponse>

    @POST("Friends/reject/{friendId}")
    suspend fun rejectFriendRequest(@Path("friendId") friendId: String): Response<RejectFriendRequestResponse>

    @DELETE("Friends/{friendId}")
    suspend fun deleteFriend(@Path("friendId") friendId: String): Response<DeleteFriendResponse>

    // Profile

    @GET("Accounts/profile")
    suspend fun getProfile(): Profile

    @Multipart
    @POST("Accounts/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<AvatarUploadResponse>

    @PUT("Accounts/profile")
    suspend fun updateProfile(@Body updatedProfile: UpdatedProfile): Response<Void>

    @GET("Accounts/users/{userId}")
    suspend fun getOtherUserProfile(@Path("userId") userId: String): Profile

    // Calendar
    @GET("Calendar/daily")
    suspend fun getDailySummary(@Query("date") date: String): DailySummary

    @GET("Calendar/weekly")
    suspend fun getWeeklySummary(@Query("startDate") startDate: String): WeeklySummary

    @GET("Calendar/monthly")
    suspend fun getMonthlySummary(@Query("year") year: String, @Query("month") month: String): MonthlySummary

    @GET("Calendar/yearly")
    suspend fun getYearlySummary(@Query("year") year: String): YearlySummary


}