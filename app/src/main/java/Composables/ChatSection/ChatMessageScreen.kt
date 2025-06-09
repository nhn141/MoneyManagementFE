package DI.Composables.ChatSection

import DI.API.TokenHandler.AuthStorage
import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Chat.ChatMessage
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
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

// Define modern color scheme
private val PrimaryColor = Color(0xFF00C853)  // Bright green
private val SecondaryColor = Color(0xFF69F0AE)  // Light green
private val BackgroundColor = Color(0xFFF5F9F6)  // Very light mint
private val SentMessageColor = Color(0xFF00E676)  // Vibrant green
private val ReceivedMessageColor = Color.White
private val OnlineColor = Color(0xFF00E676)  // Matching sent message color
private val OfflineColor = Color(0xFF9E9E9E)
private val SurfaceColor = Color(0xFFE8F5E9)  // Light mint surface

@Composable
fun ChatMessageScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    friendId: String? = null,
    groupId: String? = null,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)
    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Determine if this is a group chat or direct chat
    val isGroupChat = groupId != null
    val chatId = groupId ?: friendId ?: ""

    LaunchedEffect(Unit) {
        if (isGroupChat) {
            chatViewModel.getGroupMessages(chatId)
            chatViewModel.markGroupMessagesAsRead(chatId)
        } else {
            chatViewModel.getChatWithOtherUser(chatId)
            profileViewModel.getFriendAvatar(chatId)
            chatViewModel.markAllMessagesAsReadFromSingleChat(chatId)
        }
    }    // Get messages based on chat type
    val chatMessages = if (isGroupChat) {
        val groupMessagesResult = chatViewModel.groupMessages.collectAsState()
        val groupMessages = groupMessagesResult.value?.getOrNull() ?: emptyList()
        // Convert GroupMessage to a common format for display
        groupMessages.map { groupMsg ->
            ChatMessage(
                messageID = groupMsg.messageId,
                senderId = groupMsg.senderId,
                receiverId = "", // Not applicable for group
                content = groupMsg.content,
                sentAt = groupMsg.timestamp,
                senderName = groupMsg.senderName,
                receiverName = "" // Not applicable for group
            )
        }
    } else {
        val chatMessagesResult = chatViewModel.chatMessages.collectAsState()
        chatMessagesResult.value?.getOrNull() ?: emptyList()
    }

    // Get chat info
    val friendAvatar = if (!isGroupChat) profileViewModel.friendAvatar.collectAsState().value else null
    val isLoadingAvatar = if (!isGroupChat) profileViewModel.isLoadingAvatar.collectAsState() else remember { mutableStateOf(false) }
    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()
    
    // Get chat title and info
    val (chatTitle, isOnline, avatarUrl) = if (isGroupChat) {
        // For group chats, get info from unified chats
        val unifiedChatsResult = chatViewModel.unifiedChats.collectAsState()
        val unifiedChats = unifiedChatsResult.value?.getOrNull() ?: emptyList()
        val groupInfo = unifiedChats.find { it.type == "group" && it.id == chatId }
        Triple(groupInfo?.title ?: "Group Chat", false, "")    } else {
        // For direct chats
        val friendName = chatMessages.firstOrNull { it.senderId == chatId }?.senderName ?: ""
        val friend = friends.firstOrNull { it.userId == chatId }
        Triple(friendName, friend?.isOnline ?: false, friendAvatar?.avatarUrl ?: "")
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                userName = chatTitle,
                isOnline = isOnline,
                friendAvatarUrl = avatarUrl,
                isLoadingAvatar = isLoadingAvatar.value,
                onBackClick = { navController.popBackStack() },
                onInfoClick = { 
                    if (isGroupChat) {
                        // Navigate to group info when implemented
                        // navController.navigate("group_info/$chatId")
                    } else {
                        navController.navigate("friend_profile/$chatId")
                    }
                },
                isGroupChat = isGroupChat
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageContent,
                onMessageChange = { messageContent = it },
                onSendClick = {
                    if (messageContent.isNotBlank()) {
                        if (isGroupChat) {
                            chatViewModel.sendGroupMessage(chatId, messageContent)
                        } else {
                            chatViewModel.sendMessage(chatId, messageContent)
                        }
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
            items(chatMessages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isSentByCurrentUser = message.senderId == currentUserId,
                    friendAvatarUrl = avatarUrl,
                    isLoadingAvatar = isLoadingAvatar.value,
                    isGroupChat = isGroupChat
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
    isGroupChat: Boolean = false
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isSentByCurrentUser) 20.dp else 4.dp,
        bottomEnd = if (isSentByCurrentUser) 4.dp else 20.dp
    )

    val backgroundColor = if (isSentByCurrentUser) {
        SentMessageColor
    } else {
        ReceivedMessageColor
    }

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
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isGroupChat) {
                    // For group chats, show first letter of sender name as avatar placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.senderName.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // For direct chats, show friend avatar
                    if(isLoadingAvatar) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = PrimaryColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        FriendAvatar(friendAvatarUrl)
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                    // Show sender name for group messages (except for current user)
                    if (isGroupChat && !isSentByCurrentUser && message.senderName.isNotBlank()) {
                        Text(
                            text = message.senderName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }
                    Text(
                        text = message.content,
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Text(
                        text = ChatTimeFormatter.formatTimestamp(message.sentAt),
                        fontSize = 11.sp,
                        color = if (isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    userName: String,
    isOnline: Boolean,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    isGroupChat: Boolean = false
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
                if (isGroupChat) {
                    // For group chats, show first letter as avatar placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "G",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // For direct chats, show friend avatar with loading state
                    if(isLoadingAvatar) {
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
                            FriendAvatar(friendAvatarUrl)
                        }
                    }
                }

                // Only show online indicator for direct chats
                if (!isGroupChat) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) OnlineColor else OfflineColor)
                            .border(2.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                if (isGroupChat) {
                    Text(
                        text = stringResource(R.string.group_chat),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = if (isOnline) stringResource(R.string.online) else stringResource(R.string.offline),
                        fontSize = 13.sp,
                        color = if (isOnline) OnlineColor else OfflineColor
                    )
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = SurfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp, max = 100.dp)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text(stringResource(R.string.type_message)) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundColor,
                    focusedContainerColor = BackgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = onSendClick,
                modifier = Modifier.size(46.dp),
                containerColor = PrimaryColor,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
