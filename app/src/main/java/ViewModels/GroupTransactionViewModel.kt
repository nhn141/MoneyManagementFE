package DI.ViewModels

import DI.Models.GroupTransaction.CreateGroupTransactionDto
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.GroupTransaction.UpdateGroupTransactionDto
import DI.Models.UiEvent.UiEvent
import DI.Repositories.GroupTransactionRepository
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
class GroupTransactionViewModel @Inject constructor(
    private val repository: GroupTransactionRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    private val _groupTransactions = MutableStateFlow<Result<List<GroupTransactionDto>>?>(null)
    val groupTransactions: StateFlow<Result<List<GroupTransactionDto>>?> = _groupTransactions.asStateFlow()

    private val _addGroupTransactionEvent = MutableSharedFlow<UiEvent>()
    val addGroupTransactionEvent = _addGroupTransactionEvent.asSharedFlow()

    private val _updateGroupTransactionEvent = MutableSharedFlow<UiEvent>()
    val updateGroupTransactionEvent = _updateGroupTransactionEvent.asSharedFlow()

    private val _deleteGroupTransactionEvent = MutableSharedFlow<UiEvent>()
    val deleteGroupTransactionEvent = _deleteGroupTransactionEvent.asSharedFlow()

    fun fetchGroupTransactions(groupFundId: String) {
        Log.d("GroupTransactionVM", "Fetching transactions for groupFundId: $groupFundId")
        viewModelScope.launch {
            try {
                val result = repository.getGroupTransactionsByGroupFundId(groupFundId)
                _groupTransactions.value = result
            } catch (e: Exception) {
                Log.e("GroupTransactionVM", "Error fetching transactions", e)
            }
        }
    }

    fun createGroupTransaction(dto: CreateGroupTransactionDto) {
        viewModelScope.launch {
            val result = repository.createGroupTransaction(dto)
            if (result.isSuccess) {
                fetchGroupTransactions(dto.groupFundID)
                _addGroupTransactionEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_transaction_created_success))
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e("GroupTransactionVM", "Error creating group transaction", error)

                _addGroupTransactionEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun updateGroupTransaction(id: String, dto: UpdateGroupTransactionDto, groupFundId: String) {
        viewModelScope.launch {
            val result = repository.updateGroupTransaction(id, dto)
            if (result.isSuccess) {
                fetchGroupTransactions(groupFundId)
                _updateGroupTransactionEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.group_transaction_updated_success))
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e("GroupTransactionVM", "Error updating group transaction", error)

                _updateGroupTransactionEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun deleteGroupTransaction(id: String, groupFundId: String) {
        viewModelScope.launch {
            val result = repository.deleteGroupTransaction(id)
            if (result.isSuccess) {
                fetchGroupTransactions(groupFundId)
                _deleteGroupTransactionEvent.emit(
                    UiEvent.ShowMessage(stringProvider.getString(R.string.transaction_deleted_success))
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e("GroupTransactionVM", "Error deleting group transaction", error)

                _deleteGroupTransactionEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }
}
