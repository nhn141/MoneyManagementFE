package DI.Composables.GroupChat

import DI.API.TokenHandler.AuthStorage
import DI.Composables.ChatSection.MessageInputBar
import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Group.GroupMessage
import DI.Models.GroupTransactionComment.CreateGroupTransactionCommentDto
import DI.Models.GroupTransactionComment.GroupTransactionCommentDto
import DI.Models.GroupTransactionComment.UpdateGroupTransactionCommentDto
import DI.Models.UserInfo.Profile
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.GroupTransactionCommentViewModel
import DI.ViewModels.ProfileViewModel
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.moneymanagement_frontend.R

private val PrimaryColor = Color(0xFF00C853)
private val BackgroundColor = Color(0xFFF5F9F6)
private val SentMessageColor = Color(0xFF00E676)
private val ReceivedMessageColor = Color.White
private val SurfaceColor = Color(0xFFE8F5E9)

@Composable
fun GroupChatMessageScreen(
    navController: NavController,
    groupId: String,
    groupChatViewModel: GroupChatViewModel,
    groupTransactionCommentViewModel: GroupTransactionCommentViewModel,
    profileViewModel: ProfileViewModel
) {
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)
    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var activeTransactionId by remember { mutableStateOf<String?>(null) }
    val comments by groupTransactionCommentViewModel.comments.collectAsState()


    LaunchedEffect(Unit) {
        groupChatViewModel.joinGroup(groupId)
        groupChatViewModel.connectToSignalR()
        groupChatViewModel.loadGroupMessages(groupId)
        groupChatViewModel.markMessagesRead(groupId)
        groupChatViewModel.loadGroupById(groupId)
        groupChatViewModel.loadGroupMembers(groupId)
        profileViewModel.getProfile()
    }

    LaunchedEffect(activeTransactionId) {
        activeTransactionId?.let {
            groupTransactionCommentViewModel.fetchComments(it)
        }
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
                    isSentByCurrentUser = message.senderId == currentUserId,
                    onCommentClick = { transactionId ->
                        activeTransactionId = transactionId
                    }
                )
            }
        }

        activeTransactionId?.let { transactionId ->
            val filteredComments = comments?.getOrNull()?.filter {
                it.groupTransactionId == transactionId
            } ?: emptyList()
            CommentDialog(
                transactionId = transactionId,
                comments = filteredComments,
                profile = profile,
                onAdd = { newContent ->
                    groupTransactionCommentViewModel.addComment(
                        CreateGroupTransactionCommentDto(transactionId, newContent)
                    )
                },
                onEdit = { commentId, newContent ->
                    groupTransactionCommentViewModel.updateComment(
                        UpdateGroupTransactionCommentDto(commentId, newContent),
                        transactionId
                    )
                },
                onDelete = { commentId ->
                    groupTransactionCommentViewModel.deleteComment(commentId, transactionId)
                },
                onDismiss = {
                    activeTransactionId = null
                }
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: GroupMessage,
    isSentByCurrentUser: Boolean,
    onCommentClick: (transactionId: String) -> Unit
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

    val transactionId = remember(message) { extractTransactionId(message.content) }
    val isTransactionMessage = remember(message) {
        (message.content.contains("💰") || message.content.contains("💸")) &&
                transactionId != null
    }

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
                    .shadow(2.dp, bubbleShape),
                contentAlignment = Alignment.BottomStart
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
                        text = ChatTimeFormatter.formatTimestamp(message.sentAt),
                        fontSize = 11.sp,
                        color = if (isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                if (isTransactionMessage && transactionId != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 10.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment),
                            contentDescription = "Comment",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onCommentClick(transactionId) }
                        )
                    }
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

fun extractTransactionId(content: String): String? {
    val pattern = Regex("_Transaction ID: ([a-fA-F0-9\\-]+)_")
    return pattern.find(content)?.groupValues?.getOrNull(1)
}

@Composable
fun CommentDialog(
    transactionId: String,
    comments: List<GroupTransactionCommentDto>,
    profile: Result<Profile>?,
    onAdd: (String) -> Unit,
    onEdit: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newComment by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedContent by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transaction Comments",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Kiểm tra nếu không có bình luận
                if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No Comments",
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.no_comments),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(comments) { comment ->
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Avatar(comment.userAvatarUrl ?: "", 26)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            comment.userName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            ChatTimeFormatter.formatTimestamp(comment.createdAt),
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    val currentUserId = (profile?.getOrNull()?.id ?: "")
                                    if (comment.userId == currentUserId) {
                                        if (editingCommentId != comment.commentId) {
                                            IconButton(onClick = {
                                                editingCommentId = comment.commentId
                                                editedContent = comment.content
                                            }) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit"
                                                )
                                            }
                                            IconButton(onClick = { onDelete(comment.commentId) }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete"
                                                )
                                            }
                                        }
                                    }
                                }

                                if (editingCommentId == comment.commentId) {
                                    OutlinedTextField(
                                        value = editedContent,
                                        onValueChange = { editedContent = it },
                                        label = { Text("Edit comment") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        TextButton(onClick = {
                                            onEdit(comment.commentId, editedContent)
                                            editingCommentId = null
                                            editedContent = ""
                                        }) { Text("Save") }
                                        TextButton(onClick = {
                                            editingCommentId = null
                                            editedContent = ""
                                        }) { Text("Cancel") }
                                    }
                                } else {
                                    Text(
                                        text = comment.content,
                                        modifier = Modifier.padding(start = 44.dp),
                                        fontSize = 15.sp
                                    )
                                }

                                HorizontalDivider()
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("New comment") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Close") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                onAdd(newComment)
                                newComment = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}


@Composable
fun Avatar(url: String, size: Int) {
    Log.d("FriendAvatarCall", "URL: $url")
    val context = LocalContext.current
    AndroidView(
        factory = {
            ImageView(context).apply {
                Glide.with(context)
                    .load(url)
                    .into(this)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(context)
                .load(url)
                .into(imageView)
        },
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
    )
}