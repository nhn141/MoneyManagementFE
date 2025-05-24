package DI.Composables.ChatSection

import DI.API.TokenHandler.AuthStorage
import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.Models.Chat.ChatMessage
import DI.Navigation.Routes
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun ChatMessageScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    friendId: String,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)

    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        chatViewModel.getChatWithOtherUser(friendId)
        profileViewModel.getFriendAvatar(friendId)
        chatViewModel.markAllMessagesAsReadFromSingleChat(friendId)
    }

    val chatMessagesResult = chatViewModel.chatMessages.collectAsState()
    val chatMessages = chatMessagesResult.value?.getOrNull() ?: emptyList()

    val friendAvatar = profileViewModel.friendAvatar.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()

    val friendName = chatMessages.firstOrNull { it.senderId == friendId }?.senderName ?: ""

    Scaffold(
        topBar = {
            ChatTopBar(
                userName = friendName,
                isOnline = friends.firstOrNull { it.userId == friendId}?.isOnline ?: false,
                friendAvatarUrl = friendAvatar.avatarUrl,
                isLoadingAvatar = isLoadingAvatar.value,
                onBackClick = { navController.popBackStack() },
                onInfoClick = { navController.navigate("friend_profile/$friendId") }
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageContent,
                onMessageChange = { messageContent = it },
                onSendClick = {
                    if (messageContent.isNotBlank()) {
                        chatViewModel.sendMessage(friendId, messageContent)
                        messageContent = "" // Clear input after sending
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF53dba9))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages.reversed()) { message ->
                MessageBubble(
                    message,
                    isSentByCurrentUser = message.senderId == currentUserId,
                    friendAvatarUrl = friendAvatar.avatarUrl,
                    isLoadingAvatar = isLoadingAvatar.value
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isSentByCurrentUser: Boolean,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
) {
    val backgroundColor = if (isSentByCurrentUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Top
    ) {
        // Avatar on the left only for received messages
        if (!isSentByCurrentUser) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
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
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(12.dp)
                .widthIn( max =
                    if(!isSentByCurrentUser) {
                        LocalConfiguration.current.screenWidthDp.dp * 0.5f
                    } else {
                        LocalConfiguration.current.screenWidthDp.dp * 0.7f
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = message.content)
            Text(
                text = ChatTimeFormatter.formatTimestamp(message.sentAt),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End),
            )
        }


    }
}


@Composable
fun ChatTopBar(
    userName: String,
    isOnline: Boolean,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF46F2C9))
            .height(56.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Icon(
            imageVector = Icons.Default.ArrowCircleLeft,
            contentDescription = "Back",
            modifier = Modifier
                .size(28.dp)
                .clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

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
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                    .border(1.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and Online/Offline
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                if (isOnline) "Online" else "Offline",
                fontSize = 12.sp,
                color = if (isOnline) Color(0xFF4CAF50) else Color.Gray
            )
        }

        // Info icon
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            modifier = Modifier
                .size(24.dp)
                .clickable { onInfoClick() }
        )
    }
}

@Composable
fun MessageInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageChange,
            placeholder = { Text("Type a message") },
            modifier = Modifier.weight(1f).heightIn(max = 100.dp),
            maxLines = 4,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSendClick,
            shape = CircleShape,
        ) {
            Text("Send")
        }
    }
}
