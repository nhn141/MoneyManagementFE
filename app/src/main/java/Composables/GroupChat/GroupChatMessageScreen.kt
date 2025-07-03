package DI.Composables.GroupChat

import ChatTimeFormatter
import DI.API.TokenHandler.AuthStorage
import DI.Composables.ChatSection.MessageInputBar
import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.CreateMessageReactionDTO
import DI.Models.Group.GroupMember
import DI.Models.Group.GroupMessage
import DI.Models.GroupTransactionComment.CreateGroupTransactionCommentDto
import DI.Models.GroupTransactionComment.GroupTransactionCommentDto
import DI.Models.GroupTransactionComment.UpdateGroupTransactionCommentDto
import DI.Models.MessageReactionSummaryDTO
import DI.Models.UserInfo.Profile
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.GroupModerationViewModel
import DI.ViewModels.GroupTransactionCommentViewModel
import DI.ViewModels.MessageEnhancementViewModel
import DI.ViewModels.ProfileViewModel
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.moneymanagement_frontend.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
    profileViewModel: ProfileViewModel,
    groupModerationViewModel: GroupModerationViewModel,
    messageEnhancementViewModel: MessageEnhancementViewModel
) {
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)
    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var activeTransactionId by remember { mutableStateOf<String?>(null) }
    val comments by groupTransactionCommentViewModel.comments.collectAsState()

    val messages by groupChatViewModel.groupMessages.collectAsState()
    val profile by profileViewModel.profile.collectAsState()

    val group by groupChatViewModel.selectedGroup.collectAsState()
    val members by groupChatViewModel.groupMembers.collectAsState()

    val userGroupStatus = groupModerationViewModel.userGroupStatus.collectAsState().value

    var activeReactionMessageId by remember { mutableStateOf<String?>(null) }

    val reactionSummary by messageEnhancementViewModel.reactionSummary.collectAsState()

    currentUserId?.let {
        groupModerationViewModel.getUserGroupStatus(groupId, it)
    } ?: run {
        // Handle the case when currentUserId is null, e.g., show an error or handle gracefully
        Log.e("GroupProfile", "User ID is null")
    }


    // Only join group and load messages when groupId changes
    LaunchedEffect(groupId) {
        groupChatViewModel.joinGroup(groupId)
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


    Scaffold(
        topBar = {
            GroupChatTopBar(
                groupName = group?.name ?: stringResource(R.string.loading),
                memberCount = members.size,
                groupAvatarUrl = group?.imageUrl,
                isLoadingAvatar = group == null,
                onBackClick = { navController.popBackStack() },
                onInfoClick = { navController.navigate("group_profile_screen/${groupId}") }
            )
        },
        bottomBar = {
            if (userGroupStatus?.isMuted == true) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.you_are_muted),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    userGroupStatus.muteReason?.let {
                        Text(
                            stringResource(R.string.mute_reason, it),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    userGroupStatus.mutedUntil?.let {
                        val mutedUntil = formatDateTime(it) // You can format the date as needed
                        //val mutedUntil = userGroupStatus.mutedUntil
                        Log.d("GroupProfile", "Muted Until: $mutedUntil")
                        Text(
                            stringResource(R.string.muted_until, mutedUntil),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
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
            }
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
                    },
                    navController = navController, // Truyền navController
                    onReactionClick = { messageId ->
                        activeReactionMessageId = messageId
                        messageEnhancementViewModel.getMessageReactions(messageId, "group")
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
                members = members,
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

        activeReactionMessageId?.let { messageId ->
            ReactionDialog(
                messageId = messageId,
                summaryResult = reactionSummary,
                onAddReaction = { reactionType ->
                    messageEnhancementViewModel.addReaction(
                        CreateMessageReactionDTO(
                            messageId = messageId,
                            reactionType = reactionType,
                            messageType = "group"
                        )
                    )
                },
                onDismiss = {
                    activeReactionMessageId = null
                }
            )
        }

    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun MessageBubble(
    message: GroupMessage,
    isSentByCurrentUser: Boolean,
    onCommentClick: (transactionId: String) -> Unit,
    navController: NavController,
    onReactionClick: (messageId: String) -> Unit
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

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val annotatedString = buildAnnotatedString {
        val content = message.content
        val postMatch = Regex("\\[post:([a-zA-Z0-9-]+)\\]").find(content)
        val transactionMatch = Regex("\\[transaction:([a-zA-Z0-9-]+)\\]").find(content)

        if (postMatch != null) {
            val postId = postMatch.groupValues[1]
            val startIndex = postMatch.range.first
            val endIndex = postMatch.range.last + 1
            append(content.substring(0, startIndex))
            withAnnotation("postId", postId) {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF667EEA),
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("\n[Xem bài viết]")
                }
            }
            append(content.substring(endIndex))
        } else if (transactionMatch != null) {
            val transactionId = transactionMatch.groupValues[1]
            val startIndex = transactionMatch.range.first
            val endIndex = transactionMatch.range.last + 1
            val transactionContent =
                content.substring(0, startIndex).trim() // Lấy nội dung trước [transaction]
            append(transactionContent)
            withAnnotation("transactionId", transactionId) {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF667EEA),
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("\n[Xem giao dịch]")
                }
            }
        } else {
            append(content)
        }
    }

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
                    val hasAnnotation =
                        annotatedString.getStringAnnotations("postId", 0, annotatedString.length)
                            .isNotEmpty() ||
                                annotatedString.getStringAnnotations(
                                    "transactionId",
                                    0,
                                    annotatedString.length
                                ).isNotEmpty()

                    if (hasAnnotation) {
                        BasicText(
                            text = annotatedString,
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        textLayoutResult?.let { layout ->
                                            val position = layout.getOffsetForPosition(offset)
                                            annotatedString.getStringAnnotations(
                                                "postId",
                                                position,
                                                position
                                            )
                                                .firstOrNull()?.let { annotation ->
                                                    navController.navigate("newsfeed?postIdToFocus=${annotation.item}")
                                                } ?: annotatedString.getStringAnnotations(
                                                "transactionId",
                                                position,
                                                position
                                            )
                                                .firstOrNull()?.let { annotation ->
                                                    val encodedContent = Uri.encode(message.content)
                                                    navController.navigate("temporary_transaction?content=$encodedContent")
                                                }
                                        }
                                    }
                                }
                                .onGloballyPositioned { coordinates ->
                                    // Đảm bảo text layout có sẵn khi click
                                },
                            style = TextStyle(color = textColor),
                            onTextLayout = { layoutResult ->
                                textLayoutResult = layoutResult
                            }
                        )
                    } else {
                        Text(
                            text = message.content,
                            color = textColor,
                            fontSize = 16.sp
                        )
                    }

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
                            contentDescription = stringResource(R.string.comment),
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onCommentClick(transactionId) }
                        )
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_reaction),
                    contentDescription = stringResource(R.string.reactions),
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onReactionClick(message.messageId) }
                )
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
    members: List<GroupMember>,
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
                    text = stringResource(R.string.transaction_comments),
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
                                contentDescription = stringResource(R.string.no_comments),
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

                                    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)

                                    val currentUser = members.find { it.userId == currentUserId }

                                    val currentUserRole = currentUser?.role ?: -1

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
                                        label = { Text(stringResource(R.string.edit_comment)) },
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
                                        }) { Text(stringResource(R.string.save)) }
                                        TextButton(onClick = {
                                            editingCommentId = null
                                            editedContent = ""
                                        }) { Text(stringResource(R.string.cancel)) }
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
                    label = { Text(stringResource(R.string.new_comment)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                onAdd(newComment)
                                newComment = ""
                            }
                        }
                    ) {
                        Text(stringResource(R.string.add))
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

@Composable
fun formatDateTime(input: String): String {
    return try {
        // Parse the input string into a ZonedDateTime object
        val dateTime = ZonedDateTime.parse(input)

        // Format the ZonedDateTime into a desired string format (e.g., "dd/MM/yyyy hh:mm a")
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
        dateTime.format(formatter)
    } catch (e: Exception) {
        stringResource(R.string.invalid_date) // Return a fallback string if parsing fails
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReactionDialog(
    messageId: String,
    summaryResult: Result<MessageReactionSummaryDTO>?,
    onAddReaction: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val reactionMap = mapOf(
        "👍" to "like",
        "❤️" to "love",
        "😂" to "laugh",
        "😢" to "cry",
        "😡" to "angry",
        "🤔" to "thinking",
        "😍" to "loveeyes",
        "🙌" to "clap"
    )

    val reverseReactionMap = reactionMap.entries.associate { (emoji, type) ->
        type to emoji
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reactions_for_message),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // --- Emoji Grid for choosing ---
                Text(
                    text = stringResource(R.string.add_reaction),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    reactionMap.forEach { (emoji, reactionType) ->
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    onAddReaction(reactionType)
                                }
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = CircleShape
                                )
                                .padding(8.dp),
                        )
                    }
                }

                HorizontalDivider()

                // --- Existing Reactions summary ---
                Text(
                    text = stringResource(R.string.current_reactions),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                summaryResult?.onSuccess { summary ->
                    if (summary.reactionCounts.isEmpty()) {
                        Text(stringResource(R.string.no_reactions_yet), color = Color.Gray)
                    } else {
                        summary.reactionCounts.forEach { (type, count) ->
                            val emoji = reverseReactionMap[type] ?: type
                            val users = summary.reactionDetails[type] ?: emptyList()

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(emoji, fontSize = 20.sp)
                                    if (users.isNotEmpty()) {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            users.forEach { user ->
                                                Avatar(user.userAvatarUrl ?: "", 26)
                                            }
                                        }
                                    }
                                    Text("$count", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}