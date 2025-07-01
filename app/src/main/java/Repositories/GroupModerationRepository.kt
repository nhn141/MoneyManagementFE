package DI.Repositories

import API.ApiService
import DI.Composables.GroupModeration.BanKickUserRequest
import DI.Composables.GroupModeration.DeleteMessageRequest
import DI.Composables.GroupModeration.GroupUserActionRequest
import DI.Composables.GroupModeration.ModerationLogResponse
import DI.Composables.GroupModeration.MuteUserRequest
import DI.Composables.GroupModeration.UserGroupStatusDTO
import javax.inject.Inject

class GroupModerationRepository @Inject constructor(
    private val apiService: ApiService)
{
    suspend fun muteUser(request: MuteUserRequest): Result<Unit> = try {
        val response = apiService.muteUser(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to mute user"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun unmuteUser(request: GroupUserActionRequest): Result<Unit> = try {
        val response = apiService.unmuteUser(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unmute user"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun banUser(request: BanKickUserRequest): Result<Unit> = try {
        val response = apiService.banUser(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to ban user"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun unbanUser(request: GroupUserActionRequest): Result<Unit> = try {
        val response = apiService.unbanUser(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unban user"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun kickUser(request: BanKickUserRequest): Result<Unit> = try {
        val response = apiService.kickUser(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to kick user"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteMessage(request: DeleteMessageRequest): Result<Unit> = try {
        val response = apiService.deleteMessage(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete message"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun grantModeratorRole(request: GroupUserActionRequest): Result<Unit> = try {
        val response = apiService.grantModRole(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to grant moderator role"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun revokeModeratorRole(request: GroupUserActionRequest): Result<Unit> = try {
        val response = apiService.revokeModRole(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to revoke moderator role"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getModerationLogs(groupId: String, page: Int, pageSize: Int): Result<ModerationLogResponse> = try {
        val response = apiService.getModerationLogs(groupId, page, pageSize)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Failed to fetch moderation logs"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserGroupStatus(groupId: String): Result<UserGroupStatusDTO> {
        return try {
            val response = apiService.getUserGroupStatus(groupId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get user group status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllGroupMemberStatuses(groupId: String): Result<List<UserGroupStatusDTO>> {
        return try {
            val response = apiService.getAllGroupMemberStatuses(groupId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get all group member statuses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
