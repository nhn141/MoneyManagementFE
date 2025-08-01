package DI.ViewModels

import DI.Models.GroupModeration.BanKickUserRequest
import DI.Models.GroupModeration.DeleteMessageRequest
import DI.Models.GroupModeration.GroupUserActionRequest
import DI.Models.GroupModeration.ModerationLogResponse
import DI.Models.GroupModeration.MuteUserRequest
import DI.Models.GroupModeration.UserGroupStatusDTO
import DI.Models.UiEvent.UiEvent
import DI.Repositories.GroupChatRepository
import DI.Repositories.GroupModerationRepository
import Utils.StringResourceProvider
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
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
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    companion object {
        private const val TAG = "GroupModerationVM"
    }

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
            if (result.isFailure) {
                logAndEmitError("muteUser", result.exceptionOrNull())
            }
            getAllMemberStatuses(request.groupId)
            _muteUserResult.emit(result)
        }
    }

    fun unmuteUser(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.unmuteUser(request)
            if (result.isFailure) {
                logAndEmitError("unmuteUser", result.exceptionOrNull())
            }
            getAllMemberStatuses(request.groupId)
            _unmuteUserResult.emit(result)
        }
    }

    fun banUser(request: BanKickUserRequest) {
        viewModelScope.launch {
            val result = repository.banUser(request)
            if (result.isFailure) {
                logAndEmitError("banUser", result.exceptionOrNull())
            }
            getAllMemberStatuses(request.groupId)
            _banUserResult.emit(result)
        }
    }

    fun unbanUser(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.unbanUser(request)
            if (result.isFailure) {
                logAndEmitError("unbanUser", result.exceptionOrNull())
            }
            getAllMemberStatuses(request.groupId)
            _unbanUserResult.emit(result)
        }
    }

    fun kickUser(request: BanKickUserRequest) {
        viewModelScope.launch {
            val result = repository.kickUser(request)
            if (result.isFailure) {
                logAndEmitError("kickUser", result.exceptionOrNull())
            }
            _kickUserResult.emit(result)
        }
    }

    fun deleteMessage(request: DeleteMessageRequest) {
        viewModelScope.launch {
            val result = repository.deleteMessage(request)
            if (result.isFailure) {
                logAndEmitError("deleteMessage", result.exceptionOrNull())
            }
            _deleteMessageResult.emit(result)
        }
    }

    fun grantModeratorRole(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.grantModeratorRole(request)
            if (result.isFailure) {
                logAndEmitError("grantModeratorRole", result.exceptionOrNull())
            }
            getAllMemberStatuses(request.groupId)
            _grantModeratorRoleResult.emit(result)
        }
    }

    fun revokeModeratorRole(request: GroupUserActionRequest) {
        viewModelScope.launch {
            val result = repository.revokeModeratorRole(request)
            if (result.isFailure) {
                logAndEmitError("revokeModeratorRole", result.exceptionOrNull())
            }
            getAllMemberStatuses(request.groupId)
            _revokeModeratorRoleResult.emit(result)
        }
    }

    fun getModerationLogs(groupId: String, page: Int, pageSize: Int) {
        viewModelScope.launch {
            val result = repository.getModerationLogs(groupId, page, pageSize)
            if (result.isFailure) {
                logAndEmitError("getModerationLogs", result.exceptionOrNull())
            }
            _moderationLogs.emit(result)
        }
    }

    fun getUserGroupStatus(groupId: String, userId: String) {
        viewModelScope.launch {
            repository.getUserGroupStatus(groupId).onSuccess {
                _userGroupStatus.value = it
            }.onFailure {
                logAndEmitError("getUserGroupStatus", it)
            }
        }
    }

    fun getAllMemberStatuses(groupId: String) {
        viewModelScope.launch {
            repository.getAllGroupMemberStatuses(groupId).onSuccess {
                _allMemberStatuses.value = it
            }.onFailure {
                logAndEmitError("getAllMemberStatuses", it)
            }
        }
    }

    private suspend fun logAndEmitError(method: String, error: Throwable?) {
        Log.e(TAG, "Error in $method", error)
        _error.emit(
            stringProvider.getString(R.string.operation_failed_try_again)
        )
    }
}
