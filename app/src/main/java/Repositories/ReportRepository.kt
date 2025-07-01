package DI.Repositories

import API.ApiService
import DI.Models.Reports.ReportRequest
import android.util.Log
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun generateReport(request: ReportRequest): Result<ResponseBody> {
        return try {
            val response = apiService.generateReport(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("ReportRepository", "Report generated successfully")
                    Result.success(body)
                } else {
                    Log.e("ReportRepository", "Empty body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ReportRepository", "Failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to generate report: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "Exception: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

}