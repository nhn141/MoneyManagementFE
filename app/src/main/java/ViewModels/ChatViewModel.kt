package DI.ViewModels

import DI.API.TokenHandler.AuthStorage
import DI.Models.Category.Category
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Chat.LatestChat
import DI.Models.Group.Group
import DI.Models.Group.GroupMessage
import DI.Repositories.ChatRepository
import DI.Repositories.GroupRepository
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val groupRepository: GroupRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var hubConnection: HubConnection? = null

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _onlineUsers = mutableStateListOf<String>()
    val onlineUsers: List<String> = _onlineUsers

    private val _chats = MutableStateFlow<Result<List<Chat>>?>(null)
    val chats: StateFlow<Result<List<Chat>>?> = _chats.asStateFlow()

    private val _chatMessages = MutableStateFlow<Result<List<ChatMessage>>?>(null)
    val chatMessages: StateFlow<Result<List<ChatMessage>>?> = _chatMessages.asStateFlow()

    private val _latestChats = MutableStateFlow<Result<List<LatestChat>>?>(null)
    val latestChats: StateFlow<Result<List<LatestChat>>?> = _latestChats.asStateFlow()

    // Group-related state flows
    private val _groups = MutableStateFlow<Result<List<Group>>?>(null)
    val groups: StateFlow<Result<List<Group>>?> = _groups.asStateFlow()

    private val _groupMessages = MutableStateFlow<Result<List<GroupMessage>>?>(null)
    val groupMessages: StateFlow<Result<List<GroupMessage>>?> = _groupMessages.asStateFlow()

    private val _groupMessagesList = mutableStateListOf<GroupMessage>()
    val groupMessagesList: List<GroupMessage> = _groupMessagesList

    // Unified chat data combining direct chats and groups
    data class UnifiedChatItem(
        val type: String, // "direct" or "group"
        val id: String, // friendId or groupId
        val title: String, // friend name or group name
        val lastMessage: String,
        val timestamp: String,
        val unreadCount: Int,
        val avatarUrl: String = "",
        val isOnline: Boolean = false // only for direct chats
    )

    private val _unifiedChats = MutableStateFlow<Result<List<UnifiedChatItem>>?>(null)
    val unifiedChats: StateFlow<Result<List<UnifiedChatItem>>?> = _unifiedChats.asStateFlow()
    init {
        connectToSignalR()
        getLatestChats()
        getAllGroups()
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
            
            // Update unified chats when new direct message arrives
            getLatestChats()

        }, ChatMessage::class.java)// Listen for group messages
        hubConnection?.on("ReceiveGroupMessage", { message: GroupMessage ->
            Log.d("SignalR", "New group message received: $message")
            
            val updatedMessages = _groupMessages.value?.getOrNull()?.toMutableList() ?: mutableListOf()
            updatedMessages.add(message)
            _groupMessages.value = Result.success(updatedMessages)
            
            _groupMessagesList.add(message)
            
            // Update unified chats when new group message arrives
            updateUnifiedChats()
        }, GroupMessage::class.java)


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
            val result = chatRepository.getAllChats()
            _chats.value = result
        }
    }

    fun getChatWithOtherUser(otherUserId: String) {
        viewModelScope.launch {
            val result = chatRepository.getChatWithOtherUser(otherUserId)
            _chatMessages.value = result
        }
    }

    fun getLatestChats() {
        viewModelScope.launch {
            val result = chatRepository.getLatestChats()
            result.onSuccess { chatMap ->
                val latestMessages = chatMap.values.toList()
                Log.d("ChatViewModel", "Latest Chats: $latestMessages")
                _latestChats.value = Result.success(latestMessages)
                updateUnifiedChats()
            }.onFailure {
                _latestChats.value = Result.failure(it)
            }
        }
    }    // Group-related methods
    fun getAllGroups() {
        viewModelScope.launch {
            Log.d("ChatViewModel", "getAllGroups() called")
            val result = groupRepository.getAllGroups()
            _groups.value = result
            result.onSuccess { groups ->
                Log.d("ChatViewModel", "getAllGroups() success, groups count: ${groups.size}")
                groups.forEach { group ->
                    Log.d("ChatViewModel", "Group: id=${group.groupId}, name=${group.name}")
                }
                updateUnifiedChats()
            }.onFailure { error ->
                Log.e("ChatViewModel", "getAllGroups() failed", error)
            }
        }
    }

    fun getGroupMessages(groupId: String) {
        viewModelScope.launch {
            val result = groupRepository.getGroupMessages(groupId)
            _groupMessages.value = result
        }
    }    fun sendGroupMessage(groupId: String, content: String) {
        if (hubConnection?.connectionState != HubConnectionState.CONNECTED) {
            Log.e("SignalR", "Reconnecting before sending group message...")
            connectToSignalR()
            return
        }

        try {
            hubConnection?.invoke("SendMessageToGroup", groupId, content)
        } catch (e: Exception) {
            Log.e("SignalR", "Failed to send group message", e)
        }
    }

    fun markGroupMessagesAsRead(groupId: String) {
        viewModelScope.launch {
            val result = groupRepository.markGroupMessagesAsRead(groupId)
            if (result.isSuccess) {
                getAllGroups()
                updateUnifiedChats()
            }
        }
    }    // Update unified chats combining both direct messages and groups
    private fun updateUnifiedChats() {
        try {
            Log.d("ChatViewModel", "updateUnifiedChats() called")
            val latestChats = _latestChats.value?.getOrNull() ?: emptyList()
            val groups = _groups.value?.getOrNull() ?: emptyList()
            val currentUserId = AuthStorage.getUserIdFromToken(context)
            
            Log.d("ChatViewModel", "LatestChats count: ${latestChats.size}, Groups count: ${groups.size}")
            
            val unifiedList = mutableListOf<UnifiedChatItem>()// Add direct chats
        latestChats.forEach { chat ->
            val isCurrentUserSender = chat.latestMessage.senderId == currentUserId
            val chatId = if (isCurrentUserSender) 
                chat.latestMessage.receiverId 
            else 
                chat.latestMessage.senderId
            val chatTitle = if (isCurrentUserSender) 
                chat.latestMessage.receiverName ?: "Unknown User"
            else 
                chat.latestMessage.senderName ?: "Unknown User"
            
            // Only add chats with valid data
            if (!chatId.isNullOrEmpty() && chatTitle.isNotEmpty()) {
                unifiedList.add(
                    UnifiedChatItem(
                        type = "direct",
                        id = chatId,
                        title = chatTitle,
                        lastMessage = if (isCurrentUserSender) 
                            "You: ${chat.latestMessage.content ?: ""}" 
                        else 
                            chat.latestMessage.content ?: "",
                        timestamp = chat.latestMessage.sentAt ?: java.time.Instant.now().toString(),
                        unreadCount = chat.unreadCount,
                        avatarUrl = chat.avatarUrl ?: "",
                        isOnline = false // Will be updated with friend online status
                    )
                )
            }
        }        // Add groups
        groups.forEach { group ->
            try {
                Log.d("ChatViewModel", "Processing group: id=${group.groupId}, name=${group.name}")
                // Only add groups that have valid essential data
                if (!group.groupId.isNullOrEmpty() && !group.name.isNullOrEmpty()) {                    // Use current timestamp if no timestamp available or if it's empty
                    val groupTimestamp = if (group.createdAt.isNullOrBlank()) {
                        java.time.Instant.now().toString()
                    } else {
                        group.createdAt
                    }
                    
                    unifiedList.add(
                        UnifiedChatItem(
                            type = "group",
                            id = group.groupId,
                            title = group.name,
                            lastMessage = group.description ?: "Group chat",
                            timestamp = groupTimestamp,
                            unreadCount = 0,
                            avatarUrl = "",
                            isOnline = false
                        )
                    )
                    Log.d("ChatViewModel", "Successfully added group to unified list")
                } else {
                    Log.w("ChatViewModel", "Skipping group with invalid data: id=${group.groupId}, name=${group.name}")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error processing group: ${group}", e)
            }
        }
          // Sort by timestamp (most recent first)
        val sortedList = unifiedList.sortedByDescending { it.timestamp }
        _unifiedChats.value = Result.success(sortedList)
        Log.d("ChatViewModel", "updateUnifiedChats() completed successfully with ${sortedList.size} items")
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error in updateUnifiedChats()", e)
            // Set empty list to prevent crashes
            _unifiedChats.value = Result.success(emptyList())
        }
    }private fun getProfileDisplayName(): String {
        // Get current user's display name from AuthStorage
        return try {
            AuthStorage.getUserIdFromToken(context) ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun markAllMessagesAsReadFromSingleChat(friendId: String) {
        viewModelScope.launch {
            val result = chatRepository.markAllMessagesAsReadFromSingleChat(friendId)
            if (result.isSuccess) {
                getLatestChats()
                updateUnifiedChats()
            }
        }
    }
}