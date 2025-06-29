package DI.ViewModels

import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.GroupFundDto
import DI.Models.GroupFund.UpdateGroupFundDto
import DI.Models.UiEvent.UiEvent
import DI.Repositories.GroupFundRepository
import Utils.StringResourceProvider
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

    private val _groupFunds = MutableStateFlow<Result<List<GroupFundDto>>?>(null)
    val groupFunds: StateFlow<Result<List<GroupFundDto>>?> = _groupFunds.asStateFlow()

    private val _addGroupFundEvent = MutableSharedFlow<UiEvent>()
    val addGroupFundEvent = _addGroupFundEvent.asSharedFlow()

    private val _updateGroupFundEvent = MutableSharedFlow<UiEvent>()
    val updateGroupFundEvent = _updateGroupFundEvent.asSharedFlow()

    private val _deleteGroupFundEvent = MutableSharedFlow<UiEvent>()
    val deleteGroupFundEvent = _deleteGroupFundEvent.asSharedFlow()

    fun fetchGroupFunds(groupId: String) {
        viewModelScope.launch {
            val result = repository.getGroupFundsByGroupId(groupId)
            _groupFunds.value = result
        }
    }

    fun createGroupFund(dto: CreateGroupFundDto) {
        viewModelScope.launch {
            val result = repository.createGroupFund(dto)
            if (result.isSuccess) {
                fetchGroupFunds(dto.groupID.toString())
                _addGroupFundEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_fund_created_success))
                )
            } else {
                _addGroupFundEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: stringProvider.getString(R.string.unknown_error)
                        )
                    )
                )
            }
        }
    }

    fun updateGroupFund(id: String, dto: UpdateGroupFundDto) {
        viewModelScope.launch {
            val result = repository.updateGroupFund(id, dto)
            if (result.isSuccess) {
                fetchGroupFunds(dto.groupFundID.toString())
                _updateGroupFundEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_fund_updated_success))
                )
            } else {
                _updateGroupFundEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: stringProvider.getString(R.string.unknown_error)
                        )
                    )
                )
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
                _deleteGroupFundEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: stringProvider.getString(R.string.unknown_error)
                        )
                    )
                )
            }
        }
    }
}
