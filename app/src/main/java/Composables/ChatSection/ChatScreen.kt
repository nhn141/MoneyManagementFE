package DI.Composables.ChatSection

import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import ViewModels.AuthViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.SolidColor
import androidx.navigation.NavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import com.example.moneymanagement_frontend.R

@Composable
fun ChatScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            chatViewModel.connectToSignalR()
            chatViewModel.getLatestChats()
            chatViewModel.getAllGroups()
        }
    }

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        chatViewModel.getLatestChats()
        chatViewModel.getAllGroups()
    }

    // Use unified chats from chatViewModel
    val unifiedChatsResult = chatViewModel.unifiedChats.collectAsState()
    val unifiedChats = unifiedChatsResult.value?.getOrNull() ?: emptyList()
    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()
    val profile = profileViewModel.profile.collectAsState().value?.getOrNull()    // Get friend IDs from unified chats for avatar loading
    LaunchedEffect(unifiedChats.toList()) {
        if(unifiedChats.isNotEmpty()) {
            val friendIds = unifiedChats.mapNotNull { chat ->
                when(chat.type) {
                    "direct" -> chat.id
                    "group" -> null // For groups, we'll use group avatars
                    else -> null
                }
            }
            if(friendIds.isNotEmpty()) {
                profileViewModel.getFriendAvatars(friendIds)
            }
        }
    }

    // Update unified chats with avatars
    val chatsWithAvatars = remember(unifiedChats, friendAvatars) {
        unifiedChats.map { chat ->
            when(chat.type) {
                "direct" -> {
                    val avatarUrl = friendAvatars.find { it.userId == chat.id }?.avatarUrl ?: ""
                    chat.copy(avatarUrl = avatarUrl)
                }
                "group" -> chat // Group avatars are already handled in the unified data
                else -> chat
            }
        }
    }    // Filter chats based on search query
    val filteredChats = remember(chatsWithAvatars, searchQuery) {
        if(searchQuery.isEmpty()) {
            chatsWithAvatars
        } else {
            chatsWithAvatars.filter { chat ->
                chat.title.contains(searchQuery, ignoreCase = true) ||
                chat.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
            .padding(16.dp),
    ) {
        Text(stringResource(R.string.messages), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if(chatsWithAvatars.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_conversations), fontSize = 16.sp)
            }
        } else if(filteredChats.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.no_conversations_search),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.try_different_search),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            }
        } else {            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(filteredChats.size) { index ->
                    val chat = filteredChats[index]
                    
                    MessageItem(
                        navController = navController,
                        title = chat.title,
                        message = chat.lastMessage,
                        time = chat.timestamp,
                        count = if(chat.unreadCount > 0) chat.unreadCount else null,
                        friendId = if(chat.type == "direct") chat.id else null,
                        groupId = if(chat.type == "group") chat.id else null,
                        friendAvatarUrl = chat.avatarUrl,
                        isLoadingAvatar = isLoadingAvatar.value,
                        isOnline = if(chat.type == "direct") {
                            friends.firstOrNull { it.userId == chat.id }?.isOnline ?: false
                        } else false,
                        color = Color(0xFF5C6BC0)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x809AE7C5)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) { innerTextField ->  
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_conversations),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    navController: NavController,
    title: String,
    message: String,
    time: String,
    count: Int?,
    color: Color,
    friendId: String?=null,
    groupId: String? =null,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    isOnline: Boolean,
    isAlert: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)            .clickable {
                if (friendId != null) {
                    navController.navigate("chat_message/$friendId")
                } else if (groupId != null) {
                    navController.navigate("group_chat/$groupId")
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online dot
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.BottomEnd
        ) {            if(isLoadingAvatar) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MainColor,
                    strokeWidth = 2.dp
                )
            } else {
                FriendAvatar(friendAvatarUrl)
            }

            // Only show online indicator for direct messages (when friendId is not null)
            if (friendId != null) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(message, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(ChatTimeFormatter.formatTimestamp(if (time.isBlank()) java.time.Instant.now().toString() else time), fontSize = 12.sp)
            if (count != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isAlert) Color(0xFFD32F2F) else Color(0xFFB0BEC5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(count.toString(), color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

