package DI.Repositories

import API.ApiService
import DI.Models.Wallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getWallets(): Result<List<Wallet>> {
        return try {
            val response = apiService.getWallets()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createWallet(wallet: Wallet): Result<Unit> {
        return try {
            val response = apiService.createWallet(wallet)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create wallet"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}