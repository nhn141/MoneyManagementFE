package DI.ViewModels

import DI.API.TokenHandler.AuthStorage
import DI.Models.Category.Category
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Chat.LatestChat
import DI.Repositories.ChatRepository
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var hubConnection: HubConnection? = null

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _onlineUsers = mutableStateListOf<String>()
    val onlineUsers: List<String> = _onlineUsers

    private val _chats = MutableStateFlow<Result<List<Chat>>?>(null)
    val chats: StateFlow<Result<List<Chat>>?> = _chats

    private val _chatMessages = MutableStateFlow<Result<List<ChatMessage>>?>(null)
    val chatMessages: StateFlow<Result<List<ChatMessage>>?> = _chatMessages

    private val _latestChats = MutableStateFlow<Result<List<LatestChat>>?>(null)
    val latestChats: StateFlow<Result<List<LatestChat>>?> = _latestChats

    init {
        connectToSignalR()
    }

    fun connectToSignalR() {
        val token = AuthStorage.getToken(context)
        val userId = AuthStorage.getUserIdFromToken(context)
        hubConnection = HubConnectionBuilder.create("http://143.198.208.227:5000/hubs/chat")
            .withAccessTokenProvider(Single.defer {
                if (token != null) Single.just(token)
                else Single.error(Throwable("Token is null"))
            })
            .build()

        hubConnection?.on("ReceiveMessage", { message: ChatMessage ->
            Log.d("SignalR", "New message received: $message")

            val updatedMessages = _chatMessages.value?.getOrNull()?.toMutableList() ?: mutableListOf()
            updatedMessages.add(message)

            _chatMessages.value = Result.success(updatedMessages)

        }, ChatMessage::class.java)


        hubConnection?.on("UserOnline", { onlineUserId: String ->
            if (!_onlineUsers.contains(onlineUserId)) {
                _onlineUsers.add(onlineUserId)
            }
        }, String::class.java)

        hubConnection?.on("UserOffline", { offlineUserId: String ->
            _onlineUsers.remove(offlineUserId)
        }, String::class.java)


        hubConnection?.start()?.doOnComplete {
            println("Connected!")
            hubConnection?.invoke("JoinUserGroup", userId)
        }?.subscribe({}, { error ->
            println("Error connecting: ${error.message}")
        })
    }

    fun sendMessage(receiverId: String, content: String) {
        if (hubConnection?.connectionState != HubConnectionState.CONNECTED) {
            Log.e("SignalR", "Reconnecting before sending...")
            connectToSignalR() // Ideally make this suspend to await start
            return
        }

        val messageMap = mapOf(
            "receiverId" to receiverId,
            "content" to content
        )
        try {
            hubConnection?.invoke("SendMessageToUser", receiverId, messageMap)
        } catch (e: Exception) {
            Log.e("SignalR", "Failed to send", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        hubConnection?.stop()
    }

    fun getAllChats() {
        viewModelScope.launch {
            val result = repository.getAllChats()
            _chats.value = result
        }
    }

    fun getChatWithOtherUser(otherUserId: String) {
        viewModelScope.launch {
            val result = repository.getChatWithOtherUser(otherUserId)
            _chatMessages.value = result
        }
    }

    fun getLatestChats() {
        viewModelScope.launch {
            val result = repository.getLatestChats()
            result.onSuccess { chatMap ->
                val latestMessages = chatMap.values.toList()
                _latestChats.value = Result.success(latestMessages)
            }.onFailure {
                _latestChats.value = Result.failure(it)
            }
        }
    }
}