package DI.Repositories

import API.ApiService
import DI.Models.Group.CreateGroupRequest
import DI.Models.Group.Group
import DI.Models.Group.GroupMember
import DI.Models.Group.GroupMessage
import DI.Models.Group.SendGroupMessageRequest
import DI.Models.Group.UpdateGroupRequest
import DI.Models.UserInfo.Profile
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createGroup(request: CreateGroupRequest): Result<Group> {
        return try {
            val response = apiService.createGroup(request)
            if (response.isSuccessful) {
                response.body()?.let { group ->
                    Result.success(group)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to create group: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error creating group", e)
            Result.failure(e)
        }
    }

    suspend fun getAllGroups(): Result<List<Group>> {
        return try {
            val response = apiService.getAllGroups()
            Result.success(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching groups", e)
            Result.failure(e)
        }
    }

    suspend fun getGroupMessages(groupId: String): Result<List<GroupMessage>> {
        return try {
            val response = apiService.getGroupMessages(groupId)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching group messages", e)
            Result.failure(e)
        }
    }

    suspend fun sendGroupMessage(request: SendGroupMessageRequest): Result<GroupMessage> {
        return try {
            val response = apiService.sendGroupMessage(request)
            if (response.isSuccessful) {
                response.body()?.let { message ->
                    Result.success(message)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to send group message: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error sending group message", e)
            Result.failure(e)
        }
    }

    suspend fun markGroupMessagesAsRead(groupId: String): Result<String> {
        return try {
            val response = apiService.markGroupMessagesAsRead(groupId)
            if (response.isSuccessful) {
                Result.success("Messages marked as read")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to mark messages as read: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error marking messages as read", e)
            Result.failure(e)
        }
    }

    suspend fun getGroupMembers(groupId: String): Result<List<GroupMember>> {
        return try {
            val response = apiService.getGroupMembers(groupId)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching group members", e)
            Result.failure(e)
        }
    }

    suspend fun getGroupMemberProfile(groupId: String, memberId: String): Result<Profile> {
        return try {
            val response = apiService.getGroupMemberProfile(groupId, memberId)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error fetching member profile", e)
            Result.failure(e)
        }
    }

    suspend fun addUserToGroup(groupId: String, userId: String): Result<String> {
        return try {
            val response = apiService.addUserToGroup(groupId, userId)
            if (response.isSuccessful) {
                Result.success("User added to group")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to add user to group: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error adding user to group", e)
            Result.failure(e)
        }
    }

    suspend fun removeUserFromGroup(groupId: String, userId: String): Result<String> {
        return try {
            val response = apiService.removeUserFromGroup(groupId, userId)
            if (response.isSuccessful) {
                Result.success("User removed from group")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to remove user from group: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error removing user from group", e)
            Result.failure(e)
        }
    }

    suspend fun updateGroup(groupId: String, request: UpdateGroupRequest): Result<Group> {
        return try {
            val response = apiService.updateGroup(groupId, request)
            if (response.isSuccessful) {
                response.body()?.let { group ->
                    Result.success(group)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to update group: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error updating group", e)
            Result.failure(e)
        }
    }

    suspend fun leaveGroup(groupId: String): Result<String> {
        return try {
            val response = apiService.leaveGroup(groupId)
            if (response.isSuccessful) {
                Result.success("Left group successfully")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to leave group: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error leaving group", e)
            Result.failure(e)
        }
    }

    suspend fun adminLeaveGroup(groupId: String): Result<String> {
        return try {
            val response = apiService.adminLeaveGroup(groupId)
            if (response.isSuccessful) {
                Result.success("Admin left group successfully")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed for admin to leave group: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error admin leaving group", e)
            Result.failure(e)
        }
    }

    suspend fun assignCollaboratorRole(groupId: String, userId: String): Result<String> {
        return try {
            val response = apiService.assignCollaboratorRole(groupId, userId)
            if (response.isSuccessful) {
                Result.success("Collaborator role assigned successfully")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to assign collaborator role: $error"))
            }
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error assigning collaborator role", e)
            Result.failure(e)
        }
    }
}
