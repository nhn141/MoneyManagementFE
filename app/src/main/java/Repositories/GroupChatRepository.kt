package DI.Repositories

import API.ApiService
import DI.Models.Chat.*
import DI.Models.Group.AdminLeaveResult
import DI.Models.Group.CreateGroupRequest
import DI.Models.Group.Group
import DI.Models.Group.GroupChatHistoryDto
import DI.Models.Group.GroupMember
import DI.Models.Group.GroupMemberProfile
import DI.Models.Group.GroupMessage
import DI.Models.Group.SendGroupMessageRequest
import DI.Models.Group.UpdateGroupRequest
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupChatRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getUserGroups(): Result<List<Group>> = try {
        val response = apiService.getUserGroups()
        if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            val error = response.errorBody()?.string()
            Log.e("GroupRepo", "getUserGroups failed: $error")
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Log.e("GroupRepo", "Exception in getUserGroups", e)
        Result.failure(e)
    }

    suspend fun createGroup(request: CreateGroupRequest): Result<Group> = try {
        val response = apiService.createGroup(request)
        if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty body"))
        } else {
            val error = response.errorBody()?.string()
            Log.e("GroupRepo", "createGroup failed: $error")
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Log.e("GroupRepo", "Exception in createGroup", e)
        Result.failure(e)
    }

    suspend fun updateGroup(groupId: String, request: UpdateGroupRequest): Result<Group> = try {
        val response = apiService.updateGroup(groupId, request)
        if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty body"))
        } else {
            val error = response.errorBody()?.string()
            Log.e("GroupRepo", "updateGroup failed: $error")
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Log.e("GroupRepo", "Exception in updateGroup", e)
        Result.failure(e)
    }

    suspend fun getGroupMembers(groupId: String): Result<List<GroupMember>> = try {
        val response = apiService.getGroupMembers(groupId)
        if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getGroupMemberProfile(groupId: String, memberId: String): Result<GroupMemberProfile> = try {
        val response = apiService.getGroupMemberProfile(groupId, memberId)
        if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty profile"))
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addUserToGroup(groupId: String, userId: String): Result<Unit> = try {
        val response = apiService.addUserToGroup(groupId, userId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun removeUserFromGroup(groupId: String, userId: String): Result<Unit> = try {
        val response = apiService.removeUserFromGroup(groupId, userId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun assignCollaboratorRole(groupId: String, userId: String): Result<Unit> = try {
        val response = apiService.assignCollaboratorRole(groupId, userId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun leaveGroup(groupId: String): Result<Unit> = try {
        val response = apiService.leaveGroup(groupId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun adminLeaveGroup(groupId: String): Result<AdminLeaveResult> = try {
        val response = apiService.adminLeaveGroup(groupId)
        if (response.isSuccessful) {
            response.body()?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty body"))
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getGroupMessages(groupId: String): Result<GroupChatHistoryDto> = try {
        val response = apiService.getGroupMessages(groupId)
        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            val errorBody = response.errorBody()?.string()
            Result.failure(Exception("Failed to load messages: $errorBody"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendGroupMessage(request: SendGroupMessageRequest): Result<Unit> = try {
        val response = apiService.sendGroupMessage(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun markGroupMessagesRead(groupId: String): Result<Unit> = try {
        val response = apiService.markGroupMessagesRead(groupId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = response.errorBody()?.string()
            Result.failure(Exception(error))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
