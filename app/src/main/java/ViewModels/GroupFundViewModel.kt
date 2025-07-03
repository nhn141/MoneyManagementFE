package DI.ViewModels

import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.GroupFundDto
import DI.Models.GroupFund.UpdateGroupFundDto
import DI.Models.UiEvent.UiEvent
import DI.Repositories.GroupFundRepository
import Utils.StringResourceProvider
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupFundViewModel @Inject constructor(
    private val repository: GroupFundRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    companion object {
        private const val TAG = "GroupFundViewModel"
    }

    private val _groupFunds = MutableStateFlow<Result<List<GroupFundDto>>?>(null)
    val groupFunds: StateFlow<Result<List<GroupFundDto>>?> = _groupFunds.asStateFlow()

    private val _addGroupFundEvent = MutableSharedFlow<UiEvent>()
    val addGroupFundEvent = _addGroupFundEvent.asSharedFlow()

    private val _updateGroupFundEvent = MutableSharedFlow<UiEvent>()
    val updateGroupFundEvent = _updateGroupFundEvent.asSharedFlow()

    private val _deleteGroupFundEvent = MutableSharedFlow<UiEvent>()
    val deleteGroupFundEvent = _deleteGroupFundEvent.asSharedFlow()

    fun fetchGroupFunds(groupId: String) {
        Log.d(TAG, "Calling fetchGroupFunds with id: $groupId")
        viewModelScope.launch {
            try {
                val result = repository.getGroupFundsByGroupId(groupId)
                Log.d(TAG, "Fetch result: ${result.isSuccess}")
                _groupFunds.value = result
            } catch (e: Exception) {
                logError("fetchGroupFunds", e)
            }
        }
    }

    fun createGroupFund(dto: CreateGroupFundDto) {
        viewModelScope.launch {
            val result = repository.createGroupFund(dto)
            if (result.isSuccess) {
                fetchGroupFunds(dto.groupID)
                _addGroupFundEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_fund_created_success))
                )
            } else {
                logAndEmitError("createGroupFund", result.exceptionOrNull(), _addGroupFundEvent)
            }
        }
    }

    fun updateGroupFund(id: String, dto: UpdateGroupFundDto, groupId: String) {
        viewModelScope.launch {
            val result = repository.updateGroupFund(id, dto)
            if (result.isSuccess) {
                fetchGroupFunds(groupId)
                _updateGroupFundEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_fund_updated_success))
                )
            } else {
                logAndEmitError("updateGroupFund", result.exceptionOrNull(), _updateGroupFundEvent)
            }
        }
    }

    fun deleteGroupFund(id: String, groupId: String) {
        viewModelScope.launch {
            val result = repository.deleteGroupFund(id)
            if (result.isSuccess) {
                fetchGroupFunds(groupId)
                _deleteGroupFundEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_fund_deleted_success))
                )
            } else {
                logAndEmitError("deleteGroupFund", result.exceptionOrNull(), _deleteGroupFundEvent)
            }
        }
    }

    private suspend fun logAndEmitError(
        method: String,
        error: Throwable?,
        flow: MutableSharedFlow<UiEvent>
    ) {
        Log.e(TAG, "Error in $method", error)
        flow.emit(
            UiEvent.ShowMessage(
                stringProvider.getString(R.string.operation_failed_try_again)
            )
        )
    }

    private fun logError(method: String, error: Throwable?) {
        Log.e(TAG, "Error in $method", error)
    }
}
