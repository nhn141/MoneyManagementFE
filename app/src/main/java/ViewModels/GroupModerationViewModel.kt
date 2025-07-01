package DI.ViewModels

import DI.Composables.GroupModeration.BanKickUserRequest
import DI.Composables.GroupModeration.DeleteMessageRequest
import DI.Composables.GroupModeration.GroupUserActionRequest
import DI.Composables.GroupModeration.ModerationLogResponse
import DI.Composables.GroupModeration.MuteUserRequest
import DI.Composables.GroupModeration.UserGroupStatusDTO
import DI.Repositories.GroupChatRepository
import DI.Repositories.GroupModerationRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupModerationViewModel @Inject constructor(
    private val repository: GroupModerationRepository,
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _muteUserResult = MutableSharedFlow<Result<Unit>>()
    val muteUserResult = _muteUserResult.asSharedFlow()

    private val _unmuteUserResult = MutableSharedFlow<Result<Unit>>()
    val unmuteUserResult = _unmuteUserResult.asSharedFlow()

    private val _banUserResult = MutableSharedFlow<Result<Unit>>()
    val banUserResult = _banUserResult.asSharedFlow()

    private val _unbanUserResult = MutableSharedFlow<Result<Unit>>()
    val unbanUserResult = _unbanUserResult.asSharedFlow()

    private val _kickUserResult = MutableSharedFlow<Result<Unit>>()
    val kickUserResult = _kickUserResult.asSharedFlow()

    private val _deleteMessageResult = MutableSharedFlow<Result<Unit>>()
    val deleteMessageResult = _deleteMessageResult.asSharedFlow()

    private val _grantModeratorRoleResult = MutableSharedFlow<Result<Unit>>()
    val grantModeratorRoleResult = _grantModeratorRoleResult.asSharedFlow()

    private val _revokeModeratorRoleResult = MutableSharedFlow<Result<Unit>>()
    val revokeModeratorRoleResult = _revokeModeratorRoleResult.asSharedFlow()

    private val _moderationLogs = MutableSharedFlow<Result<ModerationLogResponse>>()
    val moderationLogs = _moderationLogs.asSharedFlow()

    private val _userGroupStatus = MutableStateFlow<UserGroupStatusDTO?>(null)
    val userGroupStatus: StateFlow<UserGroupStatusDTO?> get() = _userGroupStatus

    private val _allMemberStatuses = MutableStateFlow<List<UserGroupStatusDTO>>(emptyList())
    val allMemberStatuses: StateFlow<List<UserGroupStatusDTO>> get() = _allMemberStatuses

    fun muteUser(request: MuteUserRequest) {
        viewModelScope.launch {
            val result = repository.muteUser(request)
            getAllMemberStatuses(request.groupId)
            _muteUserResult.emit(result)
        }
    }

    fun unmuteUser(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.unmuteUser(request)
            getAllMemberStatuses(request.groupId)
            _unmuteUserResult.emit(result)
        }
    }

    fun banUser(request: BanKickUserRequest) {
        viewModelScope.launch {
            val result = repository.banUser(request)
            getAllMemberStatuses(request.groupId)
            _banUserResult.emit(result)
        }
    }

    fun unbanUser(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.unbanUser(request)
            getAllMemberStatuses(request.groupId)
            _unbanUserResult.emit(result)
        }
    }

    fun kickUser(request: BanKickUserRequest) {
        viewModelScope.launch {
            val result = repository.kickUser(request)
            _kickUserResult.emit(result)
        }
    }

    fun deleteMessage(request: DeleteMessageRequest) {
        viewModelScope.launch {
            val result = repository.deleteMessage(request)
            _deleteMessageResult.emit(result)
        }
    }

    fun grantModeratorRole(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.grantModeratorRole(request)
            getAllMemberStatuses(request.groupId)
            _grantModeratorRoleResult.emit(result)
        }
    }

    fun revokeModeratorRole(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.revokeModeratorRole(request)
            getAllMemberStatuses(request.groupId)
            _revokeModeratorRoleResult.emit(result)
        }
    }

    fun getModerationLogs(groupId: String, page: Int, pageSize: Int) {
        viewModelScope.launch {
            val result = repository.getModerationLogs(groupId, page, pageSize)
            _moderationLogs.emit(result)
        }
    }

    fun getUserGroupStatus(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.getUserGroupStatus(groupId).onSuccess {
                _userGroupStatus.value = it
            }.onFailure {
                _error.value = "Failed to load user status: ${it.localizedMessage}"
            }
        }
    }

    fun getAllMemberStatuses(groupId: String) {
        viewModelScope.launch {
            repository.getAllGroupMemberStatuses(groupId).onSuccess {
                _allMemberStatuses.value = it
            }.onFailure {
                _error.value = "Failed to load group members status: ${it.localizedMessage}"
            }
        }
    }
}
