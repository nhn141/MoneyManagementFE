package DI.Composables.ChatSection

import DI.API.DateTimeHandler.ChatTimeFormatter
import DI.API.TokenHandler.AuthStorage
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Navigation.Routes
import DI.ViewModels.ChatViewModel
import android.util.Log
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
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        chatViewModel.getAllChats()
        Log.d("Getting Chat", "")
    }
    val chatsResult = chatViewModel.chats.collectAsState()
    val chats = remember { mutableStateListOf<Chat?>(null) }
    LaunchedEffect(chatsResult.value) {
        chatsResult.value?.let { result ->
            result.onSuccess { data ->
                chats.clear()
                chats.addAll(data)
            }.onFailure {
                Log.d("Chats", "Error fetching chats data")
            }
        }
    }

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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(chats.size) { index ->
                MessageItem(
                    navController = navController,
                    title = chats[index]?.otherUserName ?: "NoName",
                    message = "Hello World" + "!",
                    time = "No time",
                    count =1,
                    otherUserId = chats[index]?.otherUserId ?: "",
                    color = Color(0xFF5C6BC0)
                )
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
    otherUserId: String,
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
                navController.navigate("chat_message/$otherUserId")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(message, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(time, fontSize = 12.sp)
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

@Composable
fun ChatMessageScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel(),
    otherUserId: String
) {
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)

    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        chatViewModel.getChatWithOtherUser(otherUserId)
    }

    val chatMessagesResult = chatViewModel.chatMessages.collectAsState()
    val chatMessages = chatMessagesResult.value?.getOrNull() ?: emptyList()

    Scaffold(
        topBar = {
            ChatTopBar(
                userName = "Test User",
                isOnline = true,
                onBackClick = { navController.popBackStack() },
                onInfoClick = { /* Show chat info/details */ }
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageContent,
                onMessageChange = { messageContent = it },
                onSendClick = {
                    if (messageContent.isNotBlank()) {
                        chatViewModel.sendMessage(otherUserId, messageContent)
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
                    otherUserAvatarRes = R.drawable.profile_image
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isSentByCurrentUser: Boolean,
    otherUserAvatarRes: Int // e.g., R.drawable.avatar_other_user
) {
    val backgroundColor = if (isSentByCurrentUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Top
    ) {
        // Avatar on the left only for received messages
        if (!isSentByCurrentUser) {
            Image(
                painter = painterResource(id = otherUserAvatarRes),
                contentDescription = "Other User Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(12.dp)
                .widthIn(max = 280.dp),
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
            Image(
                painter = painterResource(id = R.drawable.profile_image), // Replace with actual image
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
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
