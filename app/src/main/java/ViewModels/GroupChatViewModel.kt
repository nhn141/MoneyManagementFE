package DI.ViewModels

import DI.API.TokenHandler.AuthStorage
import DI.Models.Chat.*

import DI.Models.Group.AdminLeaveResult
import DI.Models.Group.CreateGroupRequest
import DI.Models.Group.Group
import DI.Models.Group.GroupMember
import DI.Models.Group.GroupMemberProfile
import DI.Models.Group.GroupMessage
import DI.Models.Group.SendGroupMessageDTO
import DI.Models.Group.SendGroupMessageRequest
import DI.Models.Group.SimulatedLatestGroupChatDto
import DI.Models.Group.UpdateGroupRequest
import DI.Repositories.GroupChatRepository
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val repository: GroupChatRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var hubConnection: HubConnection? = null

    init {
        connectToSignalR()
        simulateLatestGroupChats()
    }

    private var signalRDisposable: Disposable? = null

    fun connectToSignalR() {
        val token = AuthStorage.getToken(context)
        val userId = AuthStorage.getUserIdFromToken(context)

        hubConnection = HubConnectionBuilder.create("http://143.198.208.227:5000/hubs/chat")
            .withAccessTokenProvider(Single.defer {
                if (token != null) Single.just(token)
                else Single.error(Throwable("Token is null"))
            })
            .build()

        // Lắng nghe sự kiện tin nhắn mới trong group
        hubConnection?.on("ReceiveGroupMessage", { jsonString: String ->
            try {
                Log.d("SignalR", "Raw JSON message: $jsonString")

                val message = Gson().fromJson(jsonString, GroupMessage::class.java)

                Log.d("SignalR", "Parsed GroupMessage: $message")

                viewModelScope.launch {
                    val updated = _groupMessages.value.toMutableList()
                    updated.add(message)
                    _groupMessages.value = updated
                }
            } catch (e: Exception) {
                Log.e("SignalR", "Failed to parse GroupMessage", e)
            }
        }, String::class.java)

        // Kết nối
        signalRDisposable = hubConnection?.start()?.doOnComplete {
            Log.d("SignalR", "Connected to hub")
            val testGroupId = "25a71c1e-bac7-457c-ad03-354378e47b7d"
            val testContent = "Tin nhắn test lúc ${System.currentTimeMillis()}"
            val messageMap = mapOf("groupId" to testGroupId, "content" to testContent)
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                try {
                    hubConnection?.invoke("JoinUserGroup", userId)
                    Log.d("SignalR", "Joined user group: $userId")
                } catch (e: Exception) {
                    Log.e("SignalR", "Failed to join group: ${e.message}")
                }
            }
        }?.subscribe({}, { error ->
            Log.e("SignalR", "Connection failed: ${error.message}")
        })
    }

    fun joinGroup(groupId: String) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            try {
                hubConnection?.invoke("JoinGroup", groupId)
                Log.d("SignalR", "Joined SignalR group: $groupId")
            } catch (e: Exception) {
                Log.e("SignalR", "Error joining group: ${e.message}")
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

    private val _simulatedGroupChats = MutableStateFlow<List<SimulatedLatestGroupChatDto>>(emptyList())
    val simulatedGroupChats: StateFlow<List<SimulatedLatestGroupChatDto>> = _simulatedGroupChats

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _updateGroupEvent = MutableSharedFlow<String>()
    val updateGroupEvent = _updateGroupEvent.asSharedFlow()

    private val _createGroupEvent = MutableSharedFlow<String>()
    val createGroupEvent = _createGroupEvent.asSharedFlow()

    init {
        // testGetUserGroups() //Done
        // testCreateGroup() //Done
        // testSendGroupMessage() //Done
        // testGetGroupMembers() //Done
        // testLeaveGroup() //Done 1/2 admin leave group => cant,
        // testUpdateGroup() //Done
        // testAdminLeaveGroup() //Done
        // testAssignCollaborator() //Done
        // testAddUserToGroup() //Done
        // testRemoveUserFromGroup() //Done
        // testGetGroupMemberProfile() //Done 1
        // testMarkMessagesRead()
        // testLoadGroupMessages()
    }

    private fun testGetUserGroups() {
        viewModelScope.launch {
            val result = repository.getUserGroups()
            result.onSuccess {
                Log.d(TAG, "✅ User groups loaded: ${it.map { g -> g.name }}")
            }.onFailure {
                Log.d(TAG, "❌ Failed to load user groups: ${it.localizedMessage}")
            }
        }
    }

    private fun testCreateGroup() {
        viewModelScope.launch {
            val request = CreateGroupRequest(
                name = "Test Group",
                description = "Testing group creation",
                initialMemberIds = emptyList()
            )
            val result = repository.createGroup(request)
            result.onSuccess {
                Log.d(TAG, "✅ Group created: ${it.name} (ID: ${it.groupId})")
            }.onFailure {
                Log.d(TAG, "❌ Failed to create group: ${it.localizedMessage}")
            }
        }
    }

    private fun testSendGroupMessage() {
        viewModelScope.launch {
            val result = repository.sendGroupMessage(
                SendGroupMessageRequest(
                    groupId = "727b116f-140c-4e1c-ad5a-ab35bc0ff089",
                    content = "Hello from ViewModel test!"
                )
            )
            result.onSuccess {
                Log.d(TAG, "✅ Message sent successfully")
            }.onFailure {
                Log.d(TAG, "❌ Failed to send message: ${it.localizedMessage}")
            }
        }
    }

    private fun testGetGroupMembers() {
        viewModelScope.launch {
            val result = repository.getGroupMembers("727b116f-140c-4e1c-ad5a-ab35bc0ff089")
            result.onSuccess {
                Log.d(TAG, "✅ Group members loaded: ${it.map { m -> m.displayName }}")
            }.onFailure {
                Log.d(TAG, "❌ Failed to load members: ${it.localizedMessage}")
            }
        }
    }

    private fun testLeaveGroup() {
        viewModelScope.launch {
            val result = repository.leaveGroup("727b116f-140c-4e1c-ad5a-ab35bc0ff089")
            result.onSuccess {
                Log.d(TAG, "✅ Left group successfully")
            }.onFailure {
                Log.d(TAG, "❌ Failed to leave group: ${it.localizedMessage}")
            }
        }
    }

    private fun testUpdateGroup() {
        viewModelScope.launch {
            val result = repository.updateGroup(
                groupId = "9887e399-ed7c-4753-9e13-000182d6ea09",
                request = UpdateGroupRequest(name = "Updated Group Name", description = "Updated description")
            )
            result.onSuccess {
                Log.d(TAG, "✅ Group updated successfully")
            }.onFailure {
                Log.d(TAG, "❌ Failed to update group: ${it.localizedMessage}")
            }
        }
    }

    private fun testAdminLeaveGroup() {
        viewModelScope.launch {
            val result = repository.adminLeaveGroup("9887e399-ed7c-4753-9e13-000182d6ea09")
            result.onSuccess {
                Log.d(TAG, "✅ Admin left group: result = $it")
            }.onFailure {
                Log.d(TAG, "❌ Failed to leave as admin: ${it.localizedMessage}")
            }
        }
    }

    private fun testAssignCollaborator() {
        viewModelScope.launch {
            val result = repository.assignCollaboratorRole(
                groupId = "25a71c1e-bac7-457c-ad03-354378e47b7d",
                userId = "9c73429b-cdfa-4c3e-b495-8815a41c947b"
            )
            result.onSuccess {
                Log.d(TAG, "✅ Assigned collaborator role")
            }.onFailure {
                Log.d(TAG, "❌ Failed to assign collaborator: ${it.localizedMessage}")
            }
        }
    }

    private fun testAddUserToGroup() {
        viewModelScope.launch {
            val result = repository.addUserToGroup(
                groupId = "25a71c1e-bac7-457c-ad03-354378e47b7d",
                userId = "9c73429b-cdfa-4c3e-b495-8815a41c947b"
            )
            result.onSuccess {
                Log.d(TAG, "✅ User added to group")
            }.onFailure {
                Log.d(TAG, "❌ Failed to add user: ${it.localizedMessage}")
            }
        }
    }

    private fun testRemoveUserFromGroup() {
        viewModelScope.launch {
            val result = repository.removeUserFromGroup(
                groupId = "25a71c1e-bac7-457c-ad03-354378e47b7d",
                userId = "9c73429b-cdfa-4c3e-b495-8815a41c947b"
            )
            result.onSuccess {
                Log.d(TAG, "✅ User removed from group")
            }.onFailure {
                Log.d(TAG, "❌ Failed to remove user: ${it.localizedMessage}")
            }
        }
    }

    private fun testGetGroupMemberProfile() {
        viewModelScope.launch {
            val result = repository.getGroupMemberProfile(
                groupId = "25a71c1e-bac7-457c-ad03-354378e47b7d",
                memberId = "e1d08206-12ef-4ce9-b2b7-f7294a6b7b89"
            )
            result.onSuccess {
                Log.d(TAG, "✅ Member profile loaded: $it")
            }.onFailure {
                Log.d(TAG, "❌ Failed to load member profile: ${it.localizedMessage}")
            }
        }
    }

    private fun testMarkMessagesRead() {
        viewModelScope.launch {
            val result = repository.markGroupMessagesRead("25a71c1e-bac7-457c-ad03-354378e47b7d")
            result.onSuccess {
                Log.d(TAG, "✅ Messages marked as read")
            }.onFailure {
                Log.d(TAG, "❌ Failed to mark messages as read: ${it.localizedMessage}")
            }
        }
    }

    private fun testLoadGroupMessages() {
        viewModelScope.launch {
            val result = repository.getGroupMessages("25a71c1e-bac7-457c-ad03-354378e47b7d")
            result.onSuccess { history ->
                Log.d(TAG, "✅ Loaded ${history.messages.size} messages in '${history.groupName}'")
                history.messages.forEach { msg ->
                    Log.d(TAG, "📩 [${msg.sentAt}] ${msg.senderName}: ${msg.content}")
                }
            }.onFailure {
                Log.d(TAG, "❌ Failed to load group messages: ${it.localizedMessage}")
            }
        }
    }

    fun loadUserGroups() {
        viewModelScope.launch {
            repository.getUserGroups().onSuccess {
                _groups.value = it
            }.onFailure {
                _error.value = "Failed to load groups: ${it.localizedMessage}"
            }
        }
    }

    fun loadGroupMessages(groupId: String) {
        viewModelScope.launch {
            repository.getGroupMessages(groupId).onSuccess {
                _groupMessages.value = it.messages
            }.onFailure {
                _error.value = "Failed to load group messages: ${it.localizedMessage}"
            }
        }
    }

//    fun sendGroupMessage(groupId: String, content: String) {
//        viewModelScope.launch {
//            val request = SendGroupMessageRequest(groupId, content)
//            repository.sendGroupMessage(request).onSuccess {
//                loadGroupMessages(groupId)
//            }.onFailure {
//                _error.value = "Failed to send message: ${it.localizedMessage}"
//            }
//        }
//    }

    fun sendGroupMessage(groupId: String, content: String) {
        if (hubConnection?.connectionState != HubConnectionState.CONNECTED) {
            Log.e("SignalR", "Reconnecting before sending...")
            connectToSignalR()
            return
        }

        val userId = AuthStorage.getUserIdFromToken(context) ?: return
        val userName = "Me"
        val avatar = "" // hoặc load từ Profile nếu có

        try {
            joinGroup(groupId)

            val dto = SendGroupMessageRequest(groupId, content)

            hubConnection?.invoke("SendMessageToGroup", dto)

            Log.d("SignalR", "Sent message to group $groupId: $content")
        } catch (e: Exception) {
            Log.e("SignalR", "Failed to send group message: ${e.message}")
            _error.value = "Failed to send group message: ${e.localizedMessage}"
        }
    }

    fun markMessagesRead(groupId: String) {
        viewModelScope.launch {
            repository.markGroupMessagesRead(groupId).onFailure {
                _error.value = "Failed to mark messages as read: ${it.localizedMessage}"
            }
        }
    }

    fun createGroup(request: CreateGroupRequest) {
        viewModelScope.launch {
            repository.createGroup(request)
                .onSuccess {
                    _createGroupEvent.emit("Group created successfully")
                    simulateLatestGroupChats() // Cập nhật danh sách nhóm
                }
                .onFailure {
                    _createGroupEvent.emit("Failed to create group: ${it.localizedMessage}")
            }
        }
    }

    fun updateGroup(groupId: String, request: UpdateGroupRequest) {
        viewModelScope.launch {
            repository.updateGroup(groupId, request).onSuccess {
                _updateGroupEvent.emit("Group updated successfully")
                loadGroupById(groupId)
            }.onFailure {
                _error.value = "Failed to update group: ${it.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            repository.getGroupMembers(groupId).onSuccess {
                _groupMembers.value = it
            }.onFailure {
                _error.value = "Failed to load group members: ${it.localizedMessage}"
            }
        }
    }

    fun loadMemberProfile(groupId: String, memberId: String) {
        viewModelScope.launch {
            repository.getGroupMemberProfile(groupId, memberId).onSuccess {
                _memberProfile.value = it
            }.onFailure {
                _error.value = "Failed to load member profile: ${it.localizedMessage}"
            }
        }
    }

    fun addUserToGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.addUserToGroup(groupId, userId).onFailure {
                _error.value = "Failed to add user to group: ${it.localizedMessage}"
            }
        }
    }

    fun removeUserFromGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.removeUserFromGroup(groupId, userId).onFailure {
                _error.value = "Failed to remove user from group: ${it.localizedMessage}"
            }
        }
    }

    fun assignCollaborator(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.assignCollaboratorRole(groupId, userId).onFailure {
                _error.value = "Failed to assign collaborator role: ${it.localizedMessage}"
            }
        }
    }

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            repository.leaveGroup(groupId).onFailure {
                _error.value = "Failed to leave group: ${it.localizedMessage}"
            }
        }
    }

    fun adminLeaveGroup(groupId: String, onResult: (AdminLeaveResult?) -> Unit) {
        viewModelScope.launch {
            val result = repository.adminLeaveGroup(groupId)
            result.onSuccess {
                onResult(it)
            }.onFailure {
                _error.value = "Admin failed to leave group: ${it.localizedMessage}"
                onResult(null)
            }
        }
    }

    fun simulateLatestGroupChats() {
        viewModelScope.launch {
            val finalResult = mutableListOf<SimulatedLatestGroupChatDto>()

            repository.getUserGroups().onSuccess { groups ->
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
            }.onFailure {
                _error.value = "Failed to load group list: ${it.localizedMessage}"
            }
        }
    }

    fun loadGroupById(groupId: String) {
        viewModelScope.launch {
            repository.getUserGroups().onSuccess { groupList ->
                _selectedGroup.value = groupList.find { it.groupId == groupId }
            }.onFailure {
                _error.value = "Failed to load group: ${it.localizedMessage}"
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
}
