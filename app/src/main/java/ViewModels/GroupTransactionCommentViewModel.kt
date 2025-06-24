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
                _commentEvent.emit(UiEvent.ShowMessage(result.exceptionOrNull()?.message ?: "Error adding comment"))
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
                _commentEvent.emit(UiEvent.ShowMessage(result.exceptionOrNull()?.message ?: "Error updating comment"))
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
                _commentEvent.emit(UiEvent.ShowMessage(result.exceptionOrNull()?.message ?: "Error deleting comment"))
            }
        }
    }

    fun testFetchComments() {
        val transactionId = "562314bd-a35c-4b99-8195-a844b8ea3350"
        viewModelScope.launch {
            Log.d("TEST", "Fetching comments for transactionId = $transactionId")
            val result = repository.getComments(transactionId)
            if (result.isSuccess) {
                Log.d("TEST", "✅ Fetched ${result.getOrNull()?.size} comments")
            } else {
                Log.e("TEST", "❌ Fetch failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun testAddComment() {
        val transactionId = "562314bd-a35c-4b99-8195-a844b8ea3350"
        val content = "Test comment from FE"
        viewModelScope.launch {
            Log.d("TEST", "Adding comment to transaction = $transactionId")
            val result = repository.addComment(CreateGroupTransactionCommentDto(transactionId, content))
            if (result.isSuccess) {
                Log.d("TEST", "✅ Comment added: ${result.getOrNull()?.commentId}")
            } else {
                Log.e("TEST", "❌ Add failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun testUpdateComment() {
        val commentId = "b111a73b-90bf-4bee-acf8-3928c9f71577"
        val newContent = "Updated test comment"
        viewModelScope.launch {
            Log.d("TEST", "Updating comment = $commentId")
            val result = repository.updateComment(UpdateGroupTransactionCommentDto(commentId, newContent))
            if (result.isSuccess) {
                Log.d("TEST", "✅ Comment updated")
            } else {
                Log.e("TEST", "❌ Update failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun testDeleteComment() {
        val commentId = "b111a73b-90bf-4bee-acf8-3928c9f71577"
        viewModelScope.launch {
            Log.d("TEST", "Deleting comment = $commentId")
            val result = repository.deleteComment(commentId)
            if (result.isSuccess) {
                Log.d("TEST", "✅ Comment deleted")
            } else {
                Log.e("TEST", "❌ Delete failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

}
