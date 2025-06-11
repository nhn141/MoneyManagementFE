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

//    init {
//        Log.d("GroupFundViewModel", "ViewModel initialized")
//        viewModelScope.launch {
//            val response = repository.getGroupFundsByGroupId("727b116f-140c-4e1c-ad5a-ab35bc0ff089")
////            val response = repository.createGroupFund(CreateGroupFundDto("727b116f-140c-4e1c-ad5a-ab35bc0ff089", "YEP"))
////            val response = repository.updateGroupFund("aaef3a4f-f7b4-486b-b0bb-aa37aecf91f4", UpdateGroupFundDto("aaef3a4f-f7b4-486b-b0bb-aa37aecf91f4", "Hello", 100000.0))
////            val response = repository.deleteGroupFund("aaef3a4f-f7b4-486b-b0bb-aa37aecf91f4")
//              Log.d("TEST", "Fetch result: $response")
//        }
//    }

    private val _groupFunds = MutableStateFlow<Result<List<GroupFundDto>>?>(null)
    val groupFunds: StateFlow<Result<List<GroupFundDto>>?> = _groupFunds.asStateFlow()

    private val _addGroupFundEvent = MutableSharedFlow<UiEvent>()
    val addGroupFundEvent = _addGroupFundEvent.asSharedFlow()

    private val _updateGroupFundEvent = MutableSharedFlow<UiEvent>()
    val updateGroupFundEvent = _updateGroupFundEvent.asSharedFlow()

    private val _deleteGroupFundEvent = MutableSharedFlow<UiEvent>()
    val deleteGroupFundEvent = _deleteGroupFundEvent.asSharedFlow()

    fun fetchGroupFunds(groupId: String) {
        Log.d("GroupFundViewModel", "Calling fetchGroupFunds with id: $groupId")
        viewModelScope.launch {
            try {
                val result = repository.getGroupFundsByGroupId(groupId)
                Log.d("GroupFundViewModel", "Fetch result: ${result.isSuccess}")
                _groupFunds.value = result
            } catch (e: Exception) {
                Log.e("GroupFundViewModel", "Error fetching funds", e)
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

    fun updateGroupFund(id: String, dto: UpdateGroupFundDto, groupId: String) {
        viewModelScope.launch {
            val result = repository.updateGroupFund(id, dto)
            if (result.isSuccess) {
                fetchGroupFunds(groupId)
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
