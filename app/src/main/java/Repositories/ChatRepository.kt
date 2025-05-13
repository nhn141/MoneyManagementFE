package DI.Repositories

import API.ApiService
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAllChats(): Result<List<Chat>> {
        return try {
            val response = apiService.getAllChats()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatWithOtherUser(otherUserId: String): Result<List<ChatMessage>> {
        return try {
            val response = apiService.getChatWithOtherUser(otherUserId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
