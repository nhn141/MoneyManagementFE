# API DTO Migration Guide

This guide provides step-by-step instructions for migrating your existing API service and repositories to use DTOs for proper API communication.

## Current State vs Target State

### Current Implementation
```kotlin
// Current API service uses internal models directly
@POST("Groups")
suspend fun createGroup(@Body request: CreateGroupRequest): Response<Group>

// Current repository uses models directly
suspend fun createGroup(request: CreateGroupRequest): Result<Group> {
    val response = apiService.createGroup(request)
    return if (response.isSuccessful) {
        Result.success(response.body()!!)
    } else {
        Result.failure(Exception("Failed"))
    }
}
```

### Target Implementation
```kotlin
// Updated API service uses DTOs
@POST("Groups")
suspend fun createGroup(@Body request: CreateGroupDTO): Response<GroupDTO>

// Updated repository handles DTO conversion
suspend fun createGroup(request: CreateGroupRequest): Result<Group> {
    val dto = request.toDTO() // Convert to DTO
    val response = apiService.createGroup(dto)
    return if (response.isSuccessful) {
        val group = response.body()!!.toModel() // Convert from DTO
        Result.success(group)
    } else {
        Result.failure(Exception("Failed"))
    }
}
```

## Migration Steps

### Step 1: Update Import Statements

Add DTO imports to your repository files:

```kotlin
// Add these imports to existing repositories
import DI.Models.Group.*     // For GroupDTO, GroupMemberDTO, etc.
import DI.Models.Chat.*      // For MessageDTO, ChatDTO, etc.
import DI.Models.Friend.*    // For FriendDTO, FriendRequestDTO, etc.
import DI.Models.Extensions.* // For extension functions (.toDTO(), .toModel())
```

### Step 2: Update Group Repository

Update `GroupRepository.kt`:

```kotlin
class GroupRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context // Add context for mappers
) {
    
    suspend fun createGroup(request: CreateGroupRequest): Result<Group> {
        return try {
            // Convert internal request to DTO
            val dto = request.toDTO()
            
            // Make API call (assuming API is updated to accept DTOs)
            val response = apiService.createGroup(dto)
            
            if (response.isSuccessful) {
                response.body()?.let { groupDTO ->
                    // Convert DTO back to internal model
                    val group = groupDTO.toModel()
                    Result.success(group)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllGroups(): Result<List<Group>> {
        return try {
            // If API returns DTOs, convert to models
            val groupDTOs = apiService.getAllGroups() // This would return List<GroupDTO>
            val groups = groupDTOs.map { it.toModel() }
            Result.success(groups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateGroup(groupId: String, request: UpdateGroupRequest): Result<Group> {
        return try {
            val dto = request.toDTO()
            val response = apiService.updateGroup(groupId, dto)
            
            if (response.isSuccessful) {
                response.body()?.let { groupDTO ->
                    Result.success(groupDTO.toModel())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendGroupMessage(request: SendGroupMessageRequest): Result<GroupMessage> {
        return try {
            val dto = request.toDTO()
            val response = apiService.sendGroupMessage(dto)
            
            if (response.isSuccessful) {
                response.body()?.let { messageDTO ->
                    Result.success(messageDTO.toModel())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Step 3: Update Chat Repository

Update `ChatRepository.kt`:

```kotlin
class ChatRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    
    suspend fun getChatWithOtherUser(otherUserId: String): Result<List<ChatMessage>> {
        return try {
            // If API returns DTOs
            val messageDTOs = apiService.getChatWithOtherUser(otherUserId)
            val messages = messageDTOs.map { it.toModel() }
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLatestChats(): Result<Map<String, LatestChat>> {
        return try {
            // Convert LatestChatResponses to use DTOs internally if needed
            val response = apiService.getLatestChats()
            Result.success(response) // May need conversion depending on API changes
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Step 4: Update Friend Repository

Update `FriendRepository.kt`:

```kotlin
class FriendRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    suspend fun getAllFriends(): Result<List<Friend>> {
        return try {
            // If API returns DTOs
            val friendDTOs = apiService.getAllFriends()
            val friends = friendDTOs.map { it.toModel() }
            Result.success(friends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addFriend(request: AddFriendRequest): Result<Friend> {
        return try {
            // Convert to DTO if needed
            val dto = AddFriendDTO(friendId = request.friendId)
            val response = apiService.addFriend(dto)
            
            if (response.isSuccessful) {
                response.body()?.let { friendDTO ->
                    Result.success(friendDTO.toModel())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFriendRequests(): Result<List<FriendRequest>> {
        return try {
            val requestDTOs = apiService.getFriendRequests()
            val requests = requestDTOs.map { it.toModel() }
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Step 5: Gradual Migration Strategy

Since you can't update everything at once, here's a gradual approach:

#### Phase 1: Add DTO Support (Current)
- ✅ Create all DTO classes
- ✅ Create mapper classes  
- ✅ Create extension functions
- Keep existing API service unchanged

#### Phase 2: Update Repositories (Next)
- Update repositories to use DTOs internally
- Convert between models and DTOs in repository layer
- Keep ViewModels unchanged

#### Phase 3: Update API Service (Later)
- Coordinate with backend team to update API contracts
- Update ApiService interface to use DTOs
- Remove old model-based endpoints

#### Phase 4: Cleanup (Final)
- Remove unused conversion code
- Optimize mapper performance
- Update documentation

### Step 6: Testing Strategy

Create tests for each mapper:

```kotlin
class GroupMapperTest {
    @Test
    fun `should convert Group to GroupDTO correctly`() {
        val group = Group(
            groupId = "test-id",
            groupName = "Test Group",
            groupDescription = "Test Description",
            createdAt = "2024-01-01T10:00:00",
            memberCount = 5,
            avatarUrl = "http://example.com/avatar.jpg",
            unreadCount = 3
        )
        
        val dto = group.toDTO()
        
        assertEquals("test-id", dto.groupId)
        assertEquals("Test Group", dto.name)
        assertEquals("Test Description", dto.description)
        assertEquals(5, dto.memberCount)
        assertEquals("http://example.com/avatar.jpg", dto.avatarUrl)
        assertEquals(3, dto.unreadCount)
    }
    
    @Test
    fun `should convert GroupDTO to Group correctly`() {
        val dto = GroupDTO(
            groupId = "test-id",
            name = "Test Group",
            description = "Test Description",
            createdAt = Date(),
            memberCount = 5,
            avatarUrl = "http://example.com/avatar.jpg",
            unreadCount = 3
        )
        
        val group = dto.toModel()
        
        assertEquals("test-id", group.groupId)
        assertEquals("Test Group", group.groupName)
        assertEquals("Test Description", group.groupDescription)
        assertEquals(5, group.memberCount)
        assertEquals("http://example.com/avatar.jpg", group.avatarUrl)
        assertEquals(3, group.unreadCount)
    }
}
```

## Error Handling

DTOs include proper error handling for:

1. **Date Conversion Failures**: Falls back to current date or original string
2. **Null Values**: Uses appropriate defaults
3. **Missing Fields**: Provides sensible defaults
4. **Type Mismatches**: Logs errors and continues with safe values

## Performance Considerations

1. **Minimize Conversions**: Convert only at repository boundaries
2. **Reuse Mappers**: Use object singletons for mapper classes
3. **Batch Conversions**: Process lists efficiently
4. **Cache Results**: Cache converted objects when appropriate

## Next Steps

1. **Start with Group Repository**: Begin migration with group-related endpoints
2. **Test Thoroughly**: Ensure existing functionality works unchanged
3. **Monitor Performance**: Watch for any performance regressions
4. **Coordinate with Backend**: Plan API contract updates
5. **Update Documentation**: Keep API documentation current
