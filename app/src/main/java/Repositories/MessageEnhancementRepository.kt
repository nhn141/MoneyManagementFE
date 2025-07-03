package DI.Repositories

import API.ApiService
import DI.Models.BatchMessageReactionsRequestDTO
import DI.Models.CreateMessageReactionDTO
import DI.Models.EnhancedMessageDTO
import DI.Models.MentionNotificationDTO
import DI.Models.MessageMentionDTO
import DI.Models.MessageReactionDTO
import DI.Models.MessageReactionSummaryDTO
import DI.Models.RemoveMessageReactionDTO
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageEnhancementRepository @Inject constructor(
    private val apiService: ApiService
) {

    companion object {
        private const val TAG = "MessageEnhancementRepo"
    }

    // ===== Reactions =====

    suspend fun addReaction(request: CreateMessageReactionDTO): Result<MessageReactionDTO> {
        return try {
            val response = apiService.addReaction(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Add reaction failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to add reaction: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during addReaction: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun removeReaction(request: RemoveMessageReactionDTO): Result<Unit> {
        return try {
            val response = apiService.removeReaction(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Remove reaction failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to remove reaction: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during removeReaction: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getMessageReactions(messageId: String, messageType: String = "direct"): Result<MessageReactionSummaryDTO> {
        return try {
            val response = apiService.getMessageReactions(messageId, messageType)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Get reactions failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to get reactions: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during getMessageReactions: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getMultipleMessageReactions(request: BatchMessageReactionsRequestDTO): Result<Map<String, MessageReactionSummaryDTO>> {
        return try {
            val response = apiService.getMultipleMessageReactions(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Get multiple reactions failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to get multiple reactions: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during getMultipleMessageReactions: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    // ===== Mentions =====

    suspend fun getMessageMentions(messageId: String): Result<List<MessageMentionDTO>> {
        return try {
            val response = apiService.getMessageMentions(messageId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Get mentions failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to get mentions: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during getMessageMentions: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getUnreadMentions(): Result<List<MentionNotificationDTO>> {
        return try {
            val response = apiService.getUnreadMentions()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Get unread mentions failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to get unread mentions: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during getUnreadMentions: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun markMentionAsRead(mentionId: String): Result<Unit> {
        return try {
            val response = apiService.markMentionAsRead(mentionId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Mark mention as read failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to mark mention as read: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during markMentionAsRead: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun markAllMentionsAsRead(): Result<Int> {
        return try {
            val response = apiService.markAllMentionsAsRead()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Mark all mentions as read failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to mark all mentions as read: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during markAllMentionsAsRead: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    // ===== Enhanced Messages =====

    suspend fun getEnhancedMessage(messageId: String, messageType: String = "direct"): Result<EnhancedMessageDTO> {
        return try {
            val response = apiService.getEnhancedMessage(messageId, messageType)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Message not found"))
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Get enhanced message failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to get enhanced message: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during getEnhancedMessage: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getEnhancedMessages(request: BatchMessageReactionsRequestDTO): Result<List<EnhancedMessageDTO>> {
        return try {
            val response = apiService.getEnhancedMessages(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                val error = response.errorBody()?.string()
                Log.e(TAG, "Get enhanced messages failed: ${response.code()} - $error")
                Result.failure(Exception("Failed to get enhanced messages: $error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during getEnhancedMessages: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}
