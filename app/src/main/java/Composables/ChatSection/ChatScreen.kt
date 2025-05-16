package DI.Composables.ChatSection

import DI.API.TokenHandler.AuthStorage
import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Friend.Friend
import DI.Navigation.Routes
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import okhttp3.Route

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        chatViewModel.getLatestChats()
    }

    val latestChatsResult = chatViewModel.latestChats.collectAsState()
    val latestChats = latestChatsResult.value?.getOrNull() ?: emptyList()
    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    LaunchedEffect(latestChats.toList()) { // Convert to list to trigger on changes
        if(latestChats.isNotEmpty()) {
            val friendIds = latestChats.map { it.latestMessage.receiverId }
            profileViewModel.getFriendAvatars(friendIds)
        }
    }

    // When both chats and avatars are ready, update chats with avatarUrls
    val chatsWithAvatars = remember(latestChats, friendAvatars) {
        latestChats.map { chat ->
            val avatarUrl = friendAvatars.find { it.userId == chat.latestMessage.receiverId }?.avatarUrl ?: ""
            chat.copy(avatarUrl = avatarUrl)
        }
    }

    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()
    Log.d("Friends", friends.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
            .padding(16.dp),
    ) {
        Text("Messages", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))
        if(chatsWithAvatars.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = Color.White,
                    strokeWidth = 5.dp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(chatsWithAvatars.size) { index ->
                    MessageItem(
                        navController = navController,
                        title = chatsWithAvatars[index].latestMessage.receiverName,
                        message = chatsWithAvatars[index].latestMessage.content,
                        time = chatsWithAvatars[index].latestMessage.sentAt,
                        count = chatsWithAvatars[index].unreadCount,
                        friendId = chatsWithAvatars[index].latestMessage.receiverId,
                        friendAvatarUrl = chatsWithAvatars[index].avatarUrl ?: "",
                        isLoadingAvatar = isLoadingAvatar.value,
                        isOnline = friends.firstOrNull {
                            it.userId == chatsWithAvatars[index].latestMessage.receiverId
                        }?.isOnline ?: false,
                        color = Color(0xFF5C6BC0)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x809AE7C5)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "\uD83D\uDD0D Search conversations...",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
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
    friendId: String,
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
            .padding(12.dp)
            .clickable {
                navController.navigate("chat_message/$friendId")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online dot
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            if(isLoadingAvatar) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MainColor,
                    strokeWidth = 2.dp
                )
            } else {
                FriendAvatar(friendAvatarUrl)
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                    .border(1.dp, Color.White, CircleShape)
            )
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
            Text(ChatTimeFormatter.formatTimestamp(time), fontSize = 12.sp)
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

