package DI

import DI.API.ModuleCurrency.CurrencyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrencyApi {
    private const val BASE_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(CurrencyApiService::class.java)

    suspend fun getUsdToVndRate(): Double? {
        return try {
            val response = service.getUsdRates()
            val usdMap = response["usd"] as? Map<*, *>
            usdMap?.get("vnd") as? Double
        } catch (e: Exception) {
            null
        }
    }
}
