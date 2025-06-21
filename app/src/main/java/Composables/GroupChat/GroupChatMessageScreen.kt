package DI.Composables.GroupChat

import DI.API.TokenHandler.AuthStorage
import DI.Composables.ChatSection.MessageInputBar
import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Group.GroupMessage
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.ProfileViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

private val PrimaryColor = Color(0xFF00C853)
private val SecondaryColor = Color(0xFF69F0AE)
private val BackgroundColor = Color(0xFFF5F9F6)
private val SentMessageColor = Color(0xFF00E676)
private val ReceivedMessageColor = Color.White
private val OnlineColor = Color(0xFF00E676)
private val OfflineColor = Color(0xFF9E9E9E)
private val SurfaceColor = Color(0xFFE8F5E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatMessageScreen(
    navController: NavController,
    groupId: String,
    groupChatViewModel: GroupChatViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)
    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        groupChatViewModel.joinGroup(groupId)
        groupChatViewModel.connectToSignalR()
        groupChatViewModel.loadGroupMessages(groupId)
        groupChatViewModel.markMessagesRead(groupId)
        groupChatViewModel.loadGroupById(groupId)
        groupChatViewModel.loadGroupMembers(groupId)
    }

    val messages by groupChatViewModel.groupMessages.collectAsState()
    val profile by profileViewModel.profile.collectAsState()

    val group by groupChatViewModel.selectedGroup.collectAsState()
    val members by groupChatViewModel.groupMembers.collectAsState()

    Scaffold(
        topBar = {
            GroupChatTopBar(
                groupName = group?.name ?: "Loading...",
                memberCount = members.size,
                groupAvatarUrl = group?.imageUrl,
                isLoadingAvatar = group == null,
                onBackClick = { navController.popBackStack() },
                onInfoClick = { navController.navigate("group_profile_screen/${groupId}") }
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageContent,
                onMessageChange = { messageContent = it },
                onSendClick = {
                    if (messageContent.isNotBlank()) {
                        groupChatViewModel.sendGroupMessage(groupId, messageContent)
                        messageContent = ""
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isSentByCurrentUser = message.senderId == currentUserId
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: GroupMessage,
    isSentByCurrentUser: Boolean
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isSentByCurrentUser) 20.dp else 4.dp,
        bottomEnd = if (isSentByCurrentUser) 4.dp else 20.dp
    )

    val backgroundColor = if (isSentByCurrentUser) SentMessageColor else ReceivedMessageColor
    val textColor = if (isSentByCurrentUser) Color.White else Color.Black
    val alignment = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isSentByCurrentUser) {
            AvatarView(
                imageUrl = message.senderAvatarUrl,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(36.dp)
            )
        }

        Column(
            modifier = Modifier
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.7f)
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(backgroundColor)
                    .shadow(2.dp, bubbleShape)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.content,
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Text(
                        text = ChatTimeFormatter.formatTimestamp(message.sentAt ?: ""),
                        fontSize = 11.sp,
                        color = if (isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarView(imageUrl: String?, modifier: Modifier = Modifier) {
    if (imageUrl.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(CircleShape)
                .shadow(4.dp, CircleShape)
        ) {
            FriendAvatar(imageUrl)
        }
    }
}

@Composable
fun GroupChatTopBar(
    groupName: String,
    memberCount: Int,
    groupAvatarUrl: String?,
    isLoadingAvatar: Boolean,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shadowElevation = 4.dp,
        color = SurfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = PrimaryColor
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoadingAvatar) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        FriendAvatar(groupAvatarUrl ?: "")
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = groupName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "$memberCount ${stringResource(R.string.members)}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.info),
                    tint = PrimaryColor
                )
            }
        }
    }
}
