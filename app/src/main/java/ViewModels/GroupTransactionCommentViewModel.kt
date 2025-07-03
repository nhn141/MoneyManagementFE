package DI.ViewModels

import DI.Models.GroupTransactionComment.CreateGroupTransactionCommentDto
import DI.Models.GroupTransactionComment.GroupTransactionCommentDto
import DI.Models.GroupTransactionComment.UpdateGroupTransactionCommentDto
import DI.Models.UiEvent.UiEvent
import DI.Repositories.GroupTransactionCommentRepository
import Utils.StringResourceProvider
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupTransactionCommentViewModel @Inject constructor(
    private val repository: GroupTransactionCommentRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    private val _comments = MutableStateFlow<Result<List<GroupTransactionCommentDto>>?>(null)
    val comments = _comments.asStateFlow()

    private val _commentEvent = MutableSharedFlow<UiEvent>()
    val commentEvent = _commentEvent.asSharedFlow()

    fun fetchComments(transactionId: String) {
        viewModelScope.launch {
            _comments.value = repository.getComments(transactionId)
        }
    }

    fun addComment(dto: CreateGroupTransactionCommentDto) {
        viewModelScope.launch {
            val result = repository.addComment(dto)
            if (result.isSuccess) {
                fetchComments(dto.groupTransactionId)
                _commentEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.comment_added)))
            } else {
                val error = result.exceptionOrNull()
                Log.e("GroupTransactionCommentVM", "Error adding comment", error)

                _commentEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun updateComment(dto: UpdateGroupTransactionCommentDto, transactionId: String) {
        viewModelScope.launch {
            val result = repository.updateComment(dto)
            if (result.isSuccess) {
                fetchComments(transactionId)
                _commentEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.comment_updated)))
            } else {
                val error = result.exceptionOrNull()
                Log.e("GroupTransactionCommentVM", "Error updating comment", error)

                _commentEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun deleteComment(commentId: String, transactionId: String) {
        viewModelScope.launch {
            val result = repository.deleteComment(commentId)
            if (result.isSuccess) {
                fetchComments(transactionId)
                _commentEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.comment_deleted)))
            } else {
                val error = result.exceptionOrNull()
                Log.e("GroupTransactionCommentVM", "Error deleting comment", error)

                _commentEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }
}
