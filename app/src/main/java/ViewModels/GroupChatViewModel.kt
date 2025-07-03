package DI.ViewModels

import DI.API.TokenHandler.AuthStorage
import DI.Models.Group.AdminLeaveResult
import DI.Models.Group.CreateGroupRequest
import DI.Models.Group.Group
import DI.Models.Group.GroupMember
import DI.Models.Group.GroupMemberProfile
import DI.Models.Group.GroupMessage
import DI.Models.Group.SendGroupMessageRequest
import DI.Models.Group.SimulatedLatestGroupChatDto
import DI.Models.Group.TransactionMessageInfo
import DI.Models.Group.UpdateGroupRequest
import DI.Models.GroupTransactionComment.GroupTransactionCommentDto
import DI.Models.UiEvent.UiEvent
import DI.Repositories.GroupChatRepository
import Utils.StringResourceProvider
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel
@Inject
constructor(
    private val repository: GroupChatRepository,
    private val stringProvider: StringResourceProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "GroupChatViewModel"
    }

    private var hubConnection: HubConnection? = null

    init {
        simulateLatestGroupChats()
    }

    private var signalRDisposable: Disposable? = null

    fun connectToSignalR() {
        val token = AuthStorage.getToken(context)
        val userId = AuthStorage.getUserIdFromToken(context)

        hubConnection =
            HubConnectionBuilder.create("http://143.198.208.227:5000/hubs/chat")
                .withAccessTokenProvider(
                    Single.defer {
                        if (token != null) Single.just(token)
                        else Single.error(Throwable("Token is null"))
                    }
                )
                .build()

        // Lắng nghe sự kiện tin nhắn mới trong group
        hubConnection?.on("ReceiveGroupMessage", { groupMessage: GroupMessage ->
            try {
                Log.d(TAG, "Raw JSON message: $groupMessage")

                val updated = _groupMessages.value.toMutableList()
                updated.add(groupMessage)
                _groupMessages.value = updated
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse GroupMessage", e)
            }
        }, GroupMessage::class.java)

        // Kết nối
        signalRDisposable = hubConnection?.start()?.doOnComplete {
            Log.d(TAG, "Connected to hub")
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                try {
                    hubConnection?.invoke("JoinUserGroup", userId)
                    Log.d(TAG, "Joined user group: $userId")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to join group: ${e.message}")
                }
            }
        }?.subscribe({}, { error ->
            Log.e(TAG, "Connection failed: ${error.message}")
        })
    }

    fun joinGroup(groupId: String) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            try {
                hubConnection?.invoke("JoinGroup", groupId)
                Log.d(TAG, "Joined SignalR group: $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Error joining group: ${e.message}")
            }
        }
    }

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _selectedGroup = MutableStateFlow<Group?>(null)
    val selectedGroup: StateFlow<Group?> = _selectedGroup

    private val _groupMessages = MutableStateFlow<List<GroupMessage>>(emptyList())
    val groupMessages: StateFlow<List<GroupMessage>> = _groupMessages

    private val _groupMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val groupMembers: StateFlow<List<GroupMember>> = _groupMembers

    private val _memberProfile = MutableStateFlow<GroupMemberProfile?>(null)
    val memberProfile: StateFlow<GroupMemberProfile?> = _memberProfile

    private val _simulatedGroupChats =
        MutableStateFlow<List<SimulatedLatestGroupChatDto>>(emptyList())
    val simulatedGroupChats: StateFlow<List<SimulatedLatestGroupChatDto>> = _simulatedGroupChats

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _updateGroupEvent = MutableSharedFlow<String>()
    val updateGroupEvent = _updateGroupEvent.asSharedFlow()

    private val _createGroupEvent = MutableSharedFlow<String>()
    val createGroupEvent = _createGroupEvent.asSharedFlow()

    private val _groupMemberChangeEvent = MutableSharedFlow<String>()
    val groupMemberChangeEvent = _groupMemberChangeEvent.asSharedFlow()

    private val _transactionMessages = MutableStateFlow<List<TransactionMessageInfo>>(emptyList())
    val transactionMessages: StateFlow<List<TransactionMessageInfo>> = _transactionMessages

    private val _groupTransactionComments =
        MutableStateFlow<Map<String, List<GroupTransactionCommentDto>>>(emptyMap())
    val groupTransactionComments: StateFlow<Map<String, List<GroupTransactionCommentDto>>> =
        _groupTransactionComments

    private val _groupAvatarUrl = MutableStateFlow<String?>(null)
    val groupAvatarUrl: StateFlow<String?> = _groupAvatarUrl

    private val _isAvatarLoading = MutableStateFlow(false)
    val isAvatarLoading: StateFlow<Boolean> = _isAvatarLoading

    fun loadUserGroups() {
        viewModelScope.launch {
            repository.getUserGroups().onSuccess {
                _groups.value = it
            }.onFailure {
                logAndEmitError("loadUserGroups", it)
            }
        }
    }

    fun loadGroupMessages(groupId: String) {
        viewModelScope.launch {
            repository.getGroupMessages(groupId).onSuccess {
                _groupMessages.value = it.messages
                filterTransactionMessages()
            }.onFailure {
                logAndEmitError("loadGroupMessages", it)
            }
        }
    }

    fun sendGroupMessage(groupId: String, content: String) {
        if (hubConnection?.connectionState != HubConnectionState.CONNECTED) {
            Log.e("SignalR", "Reconnecting before sending...")
            connectToSignalR()
            return
        }

        try {
            joinGroup(groupId)

            val dto = SendGroupMessageRequest(groupId, content)
            // Do NOT call loadGroupMessages here to avoid duplicate messages
            hubConnection?.invoke("SendMessageToGroup", dto)

            Log.d("SignalR", "Sent message to group $groupId: $content")
        } catch (e: Exception) {
            Log.e("SignalR", "Failed to send group message: ${e.message}")
            logAndEmitError("sendGroupMessage", e)
        }
    }

    fun markMessagesRead(groupId: String) {
        viewModelScope.launch {
            repository.markGroupMessagesRead(groupId).onFailure {
                logAndEmitError("markMessagesRead", it)
            }
        }
    }

    fun createGroup(request: CreateGroupRequest) {
        viewModelScope.launch {
            repository
                .createGroup(request)
                .onSuccess {
                    _createGroupEvent.emit("Group created successfully")
                    simulateLatestGroupChats()
                }
                .onFailure {
                    logAndEmitError("createGroup", it)
                }
        }
    }

    fun updateGroup(groupId: String, request: UpdateGroupRequest) {
        viewModelScope.launch {
            repository.updateGroup(groupId, request).onSuccess {
                _updateGroupEvent.emit("Group updated successfully")
                loadGroupById(groupId)
            }.onFailure {
                logAndEmitError("updateGroup", it)
            }
        }
    }

    fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            repository.getGroupMembers(groupId).onSuccess {
                _groupMembers.value = it
            }.onFailure {
                logAndEmitError("loadGroupMembers", it)
            }
        }
    }

    fun loadMemberProfile(groupId: String, memberId: String) {
        viewModelScope.launch {
            repository.getGroupMemberProfile(groupId, memberId).onSuccess {
                _memberProfile.value = it
            }.onFailure {
                logAndEmitError("loadMemberProfile", it)
            }
        }
    }

    fun addUserToGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.addUserToGroup(groupId, userId).onSuccess {
                _groupMemberChangeEvent.emit("add")
            }.onFailure {
                logAndEmitError("addUserToGroup", it)
            }
            loadGroupMembers(groupId)
        }
    }

    fun removeUserFromGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.removeUserFromGroup(groupId, userId).onSuccess {
                _groupMemberChangeEvent.emit("remove")
            }.onFailure {
                logAndEmitError("removeUserFromGroup", it)
            }
            loadGroupMembers(groupId)
        }
    }

    fun assignCollaborator(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.assignCollaboratorRole(groupId, userId).onFailure {
                logAndEmitError("assignCollaborator", it)
            }
            loadGroupMembers(groupId)
        }
    }

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            repository.leaveGroup(groupId).onFailure {
                logAndEmitError("leaveGroup", it)
            }
            loadGroupMembers(groupId)
        }
    }

    fun adminLeaveGroup(groupId: String, onResult: (AdminLeaveResult?) -> Unit) {
        viewModelScope.launch {
            val result = repository.adminLeaveGroup(groupId)
            result.onSuccess {
                onResult(it)
                loadGroupMembers(groupId)
            }.onFailure {
                logAndEmitError("adminLeaveGroup", it)
                onResult(null)
            }
        }
    }

    fun simulateLatestGroupChats() {
        viewModelScope.launch {
            val finalResult = mutableListOf<SimulatedLatestGroupChatDto>()

            repository
                .getUserGroups()
                .onSuccess { groups ->
                    groups.forEach { group ->
                        repository.getGroupMessages(group.groupId).onSuccess { history ->
                            val lastMsg = history.messages.lastOrNull()
                            finalResult.add(
                                SimulatedLatestGroupChatDto(
                                    groupId = group.groupId,
                                    groupName = group.name,
                                    groupImageUrl = group.imageUrl,
                                    latestMessageContent = lastMsg?.content,
                                    unreadCount = history.unreadCount,
                                    sendAt = lastMsg?.sentAt ?: "",
                                )
                            )
                        }
                    }
                    _simulatedGroupChats.value = finalResult
                }
                .onFailure {
                    _error.value = "Failed to load group list: ${it.localizedMessage}"
                }
                _simulatedGroupChats.value = finalResult
            }.onFailure {
                logAndEmitError("simulateLatestGroupChats", it)
            }
        }
    }

    fun loadGroupById(groupId: String) {
        viewModelScope.launch {
            repository.getUserGroups().onSuccess { groupList ->
                _selectedGroup.value = groupList.find { it.groupId == groupId }
            }.onFailure {
                logAndEmitError("loadGroupById", it)
            }
        }
    }

    private fun filterTransactionMessages() {
        val rawMessages = _groupMessages.value
        val filtered =
            rawMessages
                .filter { it.content.contains("💰") || it.content.contains("💸") }
                .mapNotNull { msg ->
                    val id = extractTransactionIdFromMessage(msg.content)
                    id?.let { TransactionMessageInfo(it, msg) }
                }
        _transactionMessages.value = filtered
    }

    private fun extractTransactionIdFromMessage(content: String): String? {
        val pattern = Regex("transactionId=([a-fA-F0-9\\-]+)")
        return pattern.find(content)?.groupValues?.getOrNull(1)
    }

    fun getGroupAvatar(groupId: String) {
        viewModelScope.launch {
            _isAvatarLoading.value = true
            repository.getGroupAvatar(groupId).onSuccess { avatarUrl ->
                _groupAvatarUrl.value = avatarUrl
                _isAvatarLoading.value = false
            }.onFailure {
                logAndEmitError("getGroupAvatar", it)
                _isAvatarLoading.value = false
            }
        }
    }

    fun uploadGroupAvatar(groupId: String, file: File) {
        _isAvatarLoading.value = true
        viewModelScope.launch {
            repository.uploadGroupAvatar(groupId, file).onSuccess { avatarUrl ->
                _groupAvatarUrl.value = avatarUrl
                _isAvatarLoading.value = false
            }.onFailure {
                logAndEmitError("uploadGroupAvatar", it)
                _isAvatarLoading.value = false
            }
        }
    }

    fun updateGroupAvatar(groupId: String, avatarUrl: String) {
        viewModelScope.launch {
            _isAvatarLoading.value = true
            repository.updateGroupAvatar(groupId, avatarUrl).onSuccess {
                _groupAvatarUrl.value = avatarUrl
                _isAvatarLoading.value = false
            }.onFailure {
                logAndEmitError("updateGroupAvatar", it)
                _isAvatarLoading.value = false
            }
        }
    }

    fun deleteGroupAvatar(groupId: String) {
        viewModelScope.launch {
            _isAvatarLoading.value = true
            repository.deleteGroupAvatar(groupId).onSuccess {
                _groupAvatarUrl.value = null
                _isAvatarLoading.value = false
            }.onFailure {
                logAndEmitError("deleteGroupAvatar", it)
                _isAvatarLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        signalRDisposable?.dispose()
        hubConnection?.stop()
    }

    private suspend fun logAndEmitError(method: String, error: Throwable?) {
        Log.e(TAG, "Error in $method", error)
        _error.emit(
            stringProvider.getString(R.string.operation_failed_try_again)
        )
    }

    private fun logAndEmitError(method: String, error: Throwable) {
        viewModelScope.launch {
            logAndEmitError(method, error as Throwable?)
        }
    }
}
