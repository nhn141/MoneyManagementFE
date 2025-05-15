    package API

    import DI.Models.Analysis.CategoryBreakdown
    import DI.Models.Auth.RefreshTokenRequest
    import DI.Models.Auth.SignInRequest
    import DI.Models.Auth.SignUpRequest
    import DI.Models.Category.Category
    import DI.Models.Category.Transaction
    import DI.Models.Wallet
    import okhttp3.ResponseBody
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.PUT
    import retrofit2.http.DELETE
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

        @POST("Categories")
        suspend fun createCategory(@Body category: Category): Response<ResponseBody>

        @POST("Wallets")
        suspend fun createWallet(@Body wallet: Wallet): Response<ResponseBody>

        @GET("Transactions")
        suspend fun getTransactions(): List<Transaction>

        @PUT("Transactions")
        suspend fun updateTransaction(@Body transaction: Transaction): Response<Transaction>

        @POST("Transactions")
        suspend fun createTransaction(@Body transaction : Transaction): Response<Transaction>

        @GET("Transactions/{id}")
        suspend fun getTransactionById(@retrofit2.http.Path("id") id: String): Response<Transaction>

        @DELETE("Transactions/{id}")
        suspend fun deleteTransaction(@retrofit2.http.Path("id") id: String): Response<ResponseBody>

        @GET("Transactions/date-range")
        suspend fun getTransactionsByDateRange(
            @Query("startDate") startDate: String,
            @Query("endDate") endDate: String
        ): Response<List<Transaction>>

        @GET("Transactions/search")
        suspend fun searchTransactions(
            @Query("startDate") startDate: String? = null,
            @Query("endDate") endDate: String? = null,
            @Query("type") type: String? = null,
            @Query("category") category: String? = null,
            @Query("amountRange") amountRange: String? = null,
            @Query("keywords") keywords: String? = null,
            @Query("timeRange") timeRange: String? = null,
            @Query("dayOfWeek") dayOfWeek: String? = null
        ): Response<List<Transaction>>

        @GET("Statistics")
        suspend fun getCategoryBreakdown(@Query("startDate") startDate: String, @Query("endDate") endDate: String): List<CategoryBreakdown>
    }