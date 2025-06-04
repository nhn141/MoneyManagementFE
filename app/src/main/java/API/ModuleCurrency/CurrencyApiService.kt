package DI.API.ModuleCurrency

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v1/currencies/usd.json")
    suspend fun getUsdRates(): Map<String, Any>
}