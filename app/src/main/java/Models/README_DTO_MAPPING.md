# DTO Mapping Documentation

This document describes the Data Transfer Object (DTO) mapping strategy implemented for the Money Management application.

## Overview

The application uses DTOs to maintain a clean separation between internal models and API communication. This ensures:

1. **API Compatibility**: DTOs match the expected API contract
2. **Internal Flexibility**: Internal models can evolve independently
3. **Type Safety**: Proper date handling and field mapping
4. **Maintainability**: Clear conversion logic between layers

## Structure

### Models vs DTOs

**Internal Models** (for app logic):
- `Group`, `GroupMember`, `GroupMessage`
- `Chat`, `ChatMessage`, `LatestChat`  
- `Friend`, `FriendRequest`

**DTOs** (for API communication):
- `GroupDTO`, `GroupMemberDTO`, `GroupMessageDTO`
- `ChatDTO`, `MessageDTO`, `ChatHistoryDTO`
- `FriendDTO`, `FriendRequestDTO`

## Key Mapping Considerations

### 1. Date Handling
- **DTOs**: Use `java.util.Date` objects
- **Models**: Use `String` for API compatibility
- **Mappers**: Handle conversion between formats using `DateMapper` utility

### 2. Property Name Differences
- `Group.groupName` ↔ `GroupDTO.name`
- `Group.groupDescription` ↔ `GroupDTO.description`
- `ChatMessage.messageID` ↔ `MessageDTO.messageId`
- `ChatMessage.sentAt` ↔ `MessageDTO.timestamp`

### 3. Additional Fields
Some DTOs include fields not present in internal models:
- `MessageDTO.isRead` (defaulted to `true`)
- `MessageDTO.senderAvatarUrl` (may be `null`)
- `GroupDTO.unreadCount`

## Usage Examples

### Using Mappers Directly
```kotlin
// Convert Group to GroupDTO
val groupDTO = GroupMapper.toDTO(group)

// Convert API response to internal model
val group = GroupMapper.fromDTO(apiGroupDTO)
```

### Using Extension Functions
```kotlin
// More concise syntax
val groupDTO = group.toDTO()
val group = apiGroupDTO.toModel()

// Chat conversion with context
val chatDTO = latestChat.toDTO(userName, isOnline, context)
```

### In Repository/API Layer
```kotlin
class GroupRepository {
    suspend fun createGroup(request: CreateGroupRequest): Result<Group> {
        return try {
            val dto = request.toDTO() // Convert to DTO
            val response = apiService.createGroup(dto)
            Result.success(response.toModel()) // Convert back to model
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## File Structure

```
Models/
├── Group/
│   ├── Group.kt                    (Internal model)
│   ├── GroupDTO.kt                 (API DTO)
│   ├── GroupMember.kt              (Internal model)
│   ├── GroupMemberDTO.kt           (API DTO)
│   ├── GroupMessage.kt             (Internal model)
│   ├── GroupMessageDTO.kt          (API DTO)
│   ├── GroupMapper.kt              (Conversion logic)
│   └── ...
├── Chat/
│   ├── ChatMessage.kt              (Internal model)
│   ├── MessageDTO.kt               (API DTO)
│   ├── ChatMapper.kt               (Conversion logic)
│   └── ...
├── Friend/
│   ├── Friend.kt                   (Internal model)
│   ├── FriendDTO.kt                (API DTO)
│   ├── FriendMapper.kt             (Conversion logic)
│   └── ...
├── Utils/
│   └── DateMapper.kt               (Date conversion utilities)
└── Extensions/
    └── DTOExtensions.kt            (Extension functions)
```

## Best Practices

1. **Always use DTOs for API communication**
2. **Keep internal models for UI and business logic**
3. **Handle date conversions consistently using DateMapper**
4. **Use extension functions for cleaner syntax**
5. **Validate required fields during conversion**
6. **Handle nullable fields appropriately**

## Migration Strategy

When updating existing code:

1. **API Layer**: Replace direct model usage with DTOs
2. **Repository Layer**: Add conversion logic using mappers
3. **ViewModel Layer**: Continue using internal models
4. **UI Layer**: Use internal models for display

Example migration:
```kotlin
// Before
apiService.createGroup(CreateGroupRequest(...))

// After  
val dto = CreateGroupRequest(...).toDTO()
apiService.createGroup(dto)
```

## Error Handling

Mappers include error handling for:
- Date parsing failures (falls back to current date or original string)
- Missing optional fields (uses defaults)
- Type conversion issues (logs and uses safe defaults)

## Testing

Each mapper should be tested for:
- Successful conversions
- Date format handling
- Null value handling
- Error scenarios
- Bidirectional conversion consistency
