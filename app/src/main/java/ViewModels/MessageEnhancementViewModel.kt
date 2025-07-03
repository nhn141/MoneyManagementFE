package DI.ViewModels

import DI.Models.BatchMessageReactionsRequestDTO
import DI.Models.CreateMessageReactionDTO
import DI.Models.EnhancedMessageDTO
import DI.Models.MentionNotificationDTO
import DI.Models.MessageMentionDTO
import DI.Models.MessageReactionSummaryDTO
import DI.Models.RemoveMessageReactionDTO
import DI.Repositories.MessageEnhancementRepository
import DI.Models.UiEvent.UiEvent
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
class MessageEnhancementViewModel @Inject constructor(
    private val repository: MessageEnhancementRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    companion object {
        private const val TAG = "MessageEnhancementVM"
    }

    // ===== StateFlows =====

    private val _reactionSummary = MutableStateFlow<Result<MessageReactionSummaryDTO>?>(null)
    val reactionSummary: StateFlow<Result<MessageReactionSummaryDTO>?> = _reactionSummary.asStateFlow()

    private val _multipleReactions = MutableStateFlow<Result<Map<String, MessageReactionSummaryDTO>>?>(null)
    val multipleReactions: StateFlow<Result<Map<String, MessageReactionSummaryDTO>>?> = _multipleReactions.asStateFlow()

    private val _mentions = MutableStateFlow<Result<List<MessageMentionDTO>>?>(null)
    val mentions: StateFlow<Result<List<MessageMentionDTO>>?> = _mentions.asStateFlow()

    private val _unreadMentions = MutableStateFlow<Result<List<MentionNotificationDTO>>?>(null)
    val unreadMentions: StateFlow<Result<List<MentionNotificationDTO>>?> = _unreadMentions.asStateFlow()

    private val _enhancedMessage = MutableStateFlow<Result<EnhancedMessageDTO>?>(null)
    val enhancedMessage: StateFlow<Result<EnhancedMessageDTO>?> = _enhancedMessage.asStateFlow()

    private val _enhancedMessages = MutableStateFlow<Result<List<EnhancedMessageDTO>>?>(null)
    val enhancedMessages: StateFlow<Result<List<EnhancedMessageDTO>>?> = _enhancedMessages.asStateFlow()

    // ===== UI Events =====

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // ===== Reactions =====

    fun addReaction(dto: CreateMessageReactionDTO) {
        viewModelScope.launch {
            val result = repository.addReaction(dto)
            if (result.isSuccess) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.reaction_added_successfully)
                    )
                )
                getMessageReactions(dto.messageId, dto.messageType)
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Error adding reaction", error)

                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun removeReaction(dto: RemoveMessageReactionDTO) {
        viewModelScope.launch {
            val result = repository.removeReaction(dto)
            if (result.isSuccess) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.reaction_removed_successfully)
                    )
                )
                getMessageReactions(dto.messageId, dto.messageType)
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Error removing reaction", error)

                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun getMessageReactions(messageId: String, messageType: String = "direct") {
        viewModelScope.launch {
            val result = repository.getMessageReactions(messageId, messageType)
            _reactionSummary.value = result
        }
    }

    fun getMultipleMessageReactions(request: BatchMessageReactionsRequestDTO) {
        viewModelScope.launch {
            val result = repository.getMultipleMessageReactions(request)
            _multipleReactions.value = result
        }
    }

    // ===== Mentions =====

    fun getMessageMentions(messageId: String) {
        viewModelScope.launch {
            val result = repository.getMessageMentions(messageId)
            _mentions.value = result
        }
    }

    fun getUnreadMentions() {
        viewModelScope.launch {
            val result = repository.getUnreadMentions()
            _unreadMentions.value = result
        }
    }

    fun markMentionAsRead(mentionId: String) {
        viewModelScope.launch {
            val result = repository.markMentionAsRead(mentionId)
            if (result.isSuccess) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.mention_marked_read_successfully)
                    )
                )
                getUnreadMentions()
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Error marking mention as read", error)

                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    fun markAllMentionsAsRead() {
        viewModelScope.launch {
            val result = repository.markAllMentionsAsRead()
            if (result.isSuccess) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.all_mentions_marked_read_successfully)
                    )
                )
                getUnreadMentions()
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Error marking all mentions as read", error)

                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(R.string.operation_failed_try_again)
                    )
                )
            }
        }
    }

    // ===== Enhanced Messages =====

    fun getEnhancedMessage(messageId: String, messageType: String = "direct") {
        viewModelScope.launch {
            val result = repository.getEnhancedMessage(messageId, messageType)
            _enhancedMessage.value = result
        }
    }

    fun getEnhancedMessages(request: BatchMessageReactionsRequestDTO) {
        viewModelScope.launch {
            val result = repository.getEnhancedMessages(request)
            _enhancedMessages.value = result
        }
    }
}
