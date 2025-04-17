package DI.API.TokenHandler

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    companion object {
        private val _tokenExpiredFlow = MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1)
        val tokenExpiredFlow = _tokenExpiredFlow.asSharedFlow()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = AuthStorage.getToken(context)
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.d("AuthInterceptor", "Token expired!")
            // Emit token expiration event
            _tokenExpiredFlow.tryEmit(Unit)
        }

        return response
    }
}