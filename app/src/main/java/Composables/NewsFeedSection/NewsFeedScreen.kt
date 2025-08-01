package DI.Composables.NewsFeedSection

import DI.Models.Friend.Friend
import DI.Models.Group.Group
import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.Post
import DI.Models.NewsFeed.ReplyCommentRequest
import DI.Models.NewsFeed.ReplyCommentResponse
import DI.Models.NewsFeed.ResultState
import DI.Models.UiEvent.UiEvent
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.NewsFeedViewModel
import DI.ViewModels.ProfileViewModel
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    navController: NavController,
    viewModel: NewsFeedViewModel,
    profileViewModel: ProfileViewModel,
    chatViewModel: ChatViewModel,
    groupChatViewModel: GroupChatViewModel,
    newsFeedViewModel: NewsFeedViewModel,
    postIdToFocus: String? = null,
    friendViewModel: FriendViewModel
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMore by viewModel.hasMorePosts.collectAsState()
    val postCreationState by viewModel.postCreationState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showCommentSheet by remember { mutableStateOf(false) }
    var selectedPostForComment by remember { mutableStateOf<Post?>(null) }
    val commentState by viewModel.commentState.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()
    var showNotificationPopup by remember { mutableStateOf(false) }
    val notificationSheetState = rememberModalBottomSheetState()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { posts.size }
    )

    var showPullGuide by remember { mutableStateOf(true) }

    LaunchedEffect(sheetState.targetValue) {
        if (sheetState.targetValue == SheetValue.Expanded && showPullGuide) {
            showPullGuide = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val arrowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val arrowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Handle post events
    LaunchedEffect(Unit) {
        viewModel.postEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= posts.size - 2 && hasMore && !isLoading) {
            viewModel.loadNextPost()
        }
    }

    LaunchedEffect(postIdToFocus) {
        if (postIdToFocus != null) {
            val postIndex = posts.indexOfFirst { it.postId == postIdToFocus }
            if (postIndex >= 0) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(postIndex)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF001A17),
                        Color(0xFF000808),
                        Color(0xFF000000)
                    ),
                    radius = 1200f
                )
            )
    ) {
        if (posts.isEmpty() && isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF00D09E).copy(alpha = 0.3f),
                            spotColor = Color(0xFF00D09E).copy(alpha = 0.5f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
                    ),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            color = Color(0xFF00D09E),
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        } else if (posts.isEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0xFF00D09E).copy(alpha = 0.2f),
                        spotColor = Color(0xFF00D09E).copy(alpha = 0.4f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00D09E).copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Article,
                            contentDescription = null,
                            tint = Color(0xFF00D09E),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.no_posts_found),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.create_first_post_prompt),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            VerticalPager(state = pagerState) { page ->
                val post = posts[page]
                PostItem(
                    post = post,
                    viewModel = viewModel,
                    onCommentClick = { clickedPost ->
                        selectedPostForComment = clickedPost
                        viewModel.fetchComments(clickedPost.postId)
                        showCommentSheet = true
                    },
                    profileViewModel = profileViewModel,
                    chatViewModel = chatViewModel,
                    groupChatViewModel = groupChatViewModel,
                    newsFeedViewModel = newsFeedViewModel,
                    friendViewModel = friendViewModel
                )
            }
        }

        if (error != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Red.copy(alpha = 0.3f),
                        spotColor = Color.Red.copy(alpha = 0.5f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D1B1B)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = error ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (isLoading && posts.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0xFF00D09E).copy(alpha = 0.3f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A).copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF00D09E),
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.loading_more),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (showDialog) {
            CreatePostDialog(
                onDismiss = { showDialog = false },
                viewModel = viewModel,
                groupChatViewModel = groupChatViewModel
            )
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(60.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF00D09E).copy(alpha = 0.4f),
                    spotColor = Color(0xFF00D09E).copy(alpha = 0.6f)
                ),
            containerColor = Color.Transparent,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00F5D4),
                                Color(0xFF00D09E),
                                Color(0xFF00A085)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_post),
                    modifier = Modifier.size(26.dp),
                    tint = Color.White
                )
            }
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(40.dp)
                .border(width = 1.dp, color = Color(0xFF009460), shape = CircleShape)
                .background(
                    color = Color(0xFF1A1A1A).copy(alpha = 0.7f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.White
            )
        }

        BadgedBox(
            badge = {
                if (unreadNotificationCount > 0) {
                    Badge(
                        modifier = Modifier.offset(x = (-8).dp, y = 8.dp),
                        containerColor = Color(0xFFFF6B6B),
                        contentColor = Color.White
                    ) {
                        Text(
                            text = unreadNotificationCount.toString(),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(40.dp)
                .border(width = 1.dp, color = Color(0xFF009460), shape = CircleShape)
                .background(
                    color = Color(0xFF1A1A1A).copy(alpha = 0.7f),
                    shape = CircleShape
                )
        ) {
            IconButton(onClick = { showNotificationPopup = true }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications),
                    tint = Color.White
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0xFF00D09E).copy(alpha = 0.3f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = snackbarData.visuals.message,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (showNotificationPopup) {
            ModalBottomSheet(
                onDismissRequest = {
                    showNotificationPopup = false
                },
                sheetState = notificationSheetState,
                containerColor = Color(0xFF1A1A1A),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(4.dp)
                            .background(
                                Color(0xFF00D09E).copy(alpha = 0.6f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.notifications),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (notifications.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2D1B1B)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_new_notifications),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn {
                            items(notifications) { notification ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            showNotificationPopup = false
                                            viewModel.markNotificationAsRead(notification.id)
                                            navController.navigate("newsfeed?postIdToFocus=${notification.postId}")
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2D1B1B)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = notification.userAvatarUrl,
                                            contentDescription = stringResource(R.string.avatar),
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color.Gray),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = when (notification.type) {
                                                    "like" -> stringResource(
                                                        R.string.notification_like_content,
                                                        notification.userName
                                                    )

                                                    "comment" -> stringResource(
                                                        R.string.notification_comment_content,
                                                        notification.userName
                                                    )

                                                    "reply" -> stringResource(
                                                        R.string.notification_reply_content,
                                                        notification.userName
                                                    )

                                                    else -> notification.userName
                                                },
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = notification.createdAt,
                                                color = Color.White.copy(alpha = 0.6f),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCommentSheet && selectedPostForComment != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showCommentSheet = false
                    showPullGuide = true
                },
                sheetState = sheetState,
                containerColor = Color(0xFF1A1A1A),
                dragHandle = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        AnimatedVisibility(
                            visible = showPullGuide,
                            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                                animationSpec = tween(600),
                                initialOffsetY = { it / 2 }
                            ),
                            exit = fadeOut(animationSpec = tween(400)) + slideOutVertically(
                                animationSpec = tween(400),
                                targetOffsetY = { -it / 2 }
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = Color(0xFF00D09E).copy(alpha = arrowAlpha),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(y = arrowOffset.dp)
                                        .scale(arrowScale)
                                        .graphicsLayer {
                                            shadowElevation = 4.dp.toPx()
                                        }
                                )
                                Text(
                                    text = stringResource(R.string.pull_to_comment),
                                    color = Color(0xFF00D09E).copy(alpha = 0.8f * arrowAlpha),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .alpha(arrowAlpha)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(4.dp)
                                .background(
                                    Color(0xFF00D09E).copy(alpha = 0.6f),
                                    RoundedCornerShape(2.dp)
                                )
                                .animateContentSize()
                        )
                    }
                }
            ) {
                CommentSection(
                    post = selectedPostForComment!!,
                    viewModel = viewModel,
                    profileViewModel = profileViewModel,
                    commentState = commentState
                )
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    onCommentClick: (Post) -> Unit,
    viewModel: NewsFeedViewModel,
    profileViewModel: ProfileViewModel,
    chatViewModel: ChatViewModel,
    groupChatViewModel: GroupChatViewModel,
    newsFeedViewModel: NewsFeedViewModel,
    friendViewModel: FriendViewModel
) {
    val postDetailState by viewModel.postDetail.collectAsState()

    LaunchedEffect(post.postId) {
        viewModel.loadPostDetail(post.postId)
    }

    val mediaUrl = when (postDetailState) {
        is ResultState.Success -> (postDetailState as ResultState.Success).data.mediaUrl
        else -> null
    }

    val imageUri = post.authorAvatarUrl?.toUri()

    if (!mediaUrl.isNullOrEmpty()) {
        PostItemWithImage(
            post = post,
            mediaUrl = mediaUrl,
            imageUri = imageUri,
            onCommentClick = onCommentClick,
            viewModel = viewModel,
            profileViewModel = profileViewModel,
            chatViewModel = chatViewModel,
            groupChatViewModel = groupChatViewModel,
            newsFeedViewModel = newsFeedViewModel,
            friendViewModel = friendViewModel
        )
    } else {
        PostItemTextOnly(
            post = post,
            imageUri = imageUri,
            onCommentClick = onCommentClick,
            viewModel = viewModel,
            profileViewModel = profileViewModel,
            chatViewModel = chatViewModel,
            groupChatViewModel = groupChatViewModel,
            friendViewModel = friendViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDropdown(
    selectedPrivacy: Int,
    onPrivacyChanged: (Int) -> Unit,
    groups: List<Group>,
    selectedGroupIds: SnapshotStateList<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val privacyOptions = mapOf(
        stringResource(R.string.privacy_friends) to 0,
        stringResource(R.string.privacy_private) to 1,
        stringResource(R.string.privacy_public) to 2,
        stringResource(R.string.privacy_group) to 3
    )

    val privacyIcons = mapOf(
        0 to Icons.Default.People,
        1 to Icons.Default.Lock,
        2 to Icons.Default.Public,
        3 to Icons.Default.Group
    )

    Column(modifier = modifier) {
        // Button Dropdown Header
        Card(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .defaultMinSize(minWidth = 100.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = privacyIcons[selectedPrivacy] ?: Icons.Default.People,
                    contentDescription = null,
                    tint = Color(0xFF00D09E),
                    modifier = Modifier.size(12.dp)
                )

                Text(
                    text = privacyOptions.entries.find { it.value == selectedPrivacy }?.key
                        ?: stringResource(R.string.privacy_friends),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 6.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF00D09E),
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Dropdown nội dung
        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 4.dp)
                    .shadow(12.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    privacyOptions.forEach { (label, value) ->
                        val isSelected = value == selectedPrivacy

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp)
                                .clickable {
                                    onPrivacyChanged(value)
                                    expanded = true // vẫn mở nếu là nhóm
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    Color(0xFF00D09E).copy(alpha = 0.15f)
                                else
                                    Color.Transparent
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = privacyIcons[value] ?: Icons.Default.People,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF00D09E) else Color.White.copy(
                                        alpha = 0.7f
                                    ),
                                    modifier = Modifier.size(12.dp)
                                )

                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF00D09E) else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    modifier = Modifier.padding(start = 6.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF00D09E),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Hiển thị danh sách group
                    if (selectedPrivacy == 3 && groups.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                        Text(
                            text = stringResource(R.string.select_group),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )

                        groups.forEach { group ->
                            val isChecked = selectedGroupIds.contains(group.groupId)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedGroupIds.remove(group.groupId)
                                        else selectedGroupIds.add(group.groupId)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (isChecked) selectedGroupIds.remove(group.groupId)
                                        else selectedGroupIds.add(group.groupId)
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = group.name,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItemWithImage(
    post: Post,
    mediaUrl: String,
    imageUri: Uri?,
    onCommentClick: (Post) -> Unit,
    viewModel: NewsFeedViewModel,
    profileViewModel: ProfileViewModel,
    chatViewModel: ChatViewModel,
    groupChatViewModel: GroupChatViewModel,
    newsFeedViewModel: NewsFeedViewModel,
    friendViewModel: FriendViewModel
) {
    var isImagePreviewOpen by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var selectedPrivacy by remember { mutableStateOf(post.targetType) }
    val selectedGroupIds = remember {
        mutableStateListOf<String>().apply {
            if (post.targetType == 3 && !post.targetGroupIds.isNullOrEmpty()) {
                addAll(post.targetGroupIds)
            }
        }
    }

    val context = LocalContext.current

    val groups by groupChatViewModel.groups.collectAsState(initial = emptyList())
    val latestChats by chatViewModel.latestChats.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        groupChatViewModel.loadUserGroups()
        chatViewModel.getLatestChats()
    }

    val friends = friendViewModel.friends.collectAsState().value?.getOrNull() ?: emptyList()
    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value

    LaunchedEffect(friends.toList()) {
        if (friends.isNotEmpty()) {
            profileViewModel.getFriendAvatars(friends.map { it.userId })
        }
    }

    // When both chats and avatars are ready, update chats with avatarUrls
    val friendsWithAvatars = remember(friends, friendAvatars) {
        friends.map { friend ->
            val avatarUrl = friendAvatars.find { it.userId == friend.userId }?.avatarUrl ?: ""
            friend.copy(avatarUrl = avatarUrl)
        }
    }

    val currentUserId = profileViewModel.profile.value?.getOrNull()?.id ?: ""
    val isAuthor = post.authorId == currentUserId

    val truncatedContent = post.content?.let {
        if (it.length > 15) it.take(15) + "..." else it
    } ?: ""

    val shareMessage = """
        ${stringResource(R.string.shared_post)}
        ${stringResource(R.string.from_label)} ${post.authorName ?: "Unknown Author"}
        $truncatedContent
        [post:${post.postId}]
    """.trimIndent()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = !isImagePreviewOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { isImagePreviewOpen = true }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Phần thông tin tác giả
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = stringResource(R.string.avatar),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = post.authorName ?: "Unknown",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = post.createdAt ?: "",
                                        color = Color(0xFF00D09E).copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // PrivacyDropdown
                            if (isAuthor) {
                                Spacer(modifier = Modifier.height(12.dp))
                                PrivacyDropdown(
                                    selectedPrivacy = selectedPrivacy,
                                    onPrivacyChanged = { newPrivacy ->
                                        selectedPrivacy = newPrivacy
                                        if (newPrivacy == 3 && selectedGroupIds.isEmpty()) {
                                            viewModel.notifyGroupSelectionError()
                                        } else {
                                            viewModel.updatePostTarget(
                                                postId = post.postId,
                                                targetType = newPrivacy,
                                                targetGroupIds = if (newPrivacy == 3 && selectedGroupIds.isNotEmpty()) {
                                                    selectedGroupIds.toList()
                                                } else {
                                                    null
                                                }
                                            )
                                        }
                                    },
                                    groups = groups,
                                    selectedGroupIds = selectedGroupIds,
                                    modifier = Modifier.width(IntrinsicSize.Min)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val content = post.content ?: ""
                            val shouldShowExpand = content.length > 100
                            Column {
                                Text(
                                    text = content,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 24.sp,
                                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (shouldShowExpand) {
                                    Text(
                                        text = if (isExpanded) stringResource(R.string.collapse) else stringResource(
                                            R.string.___see_more
                                        ),
                                        color = Color(0xFF00D09E),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .clickable { isExpanded = !isExpanded }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            ActionButtons(
                                post = post,
                                viewModel = viewModel,
                                onCommentClick = onCommentClick,
                                onShareClick = { showShareDialog = true }
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isImagePreviewOpen,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 5f)
                offset += panChange
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (dragAmount.y > 100f || dragAmount.y < -100f) {
                                isImagePreviewOpen = false
                                scale = 1f
                                offset = Offset.Zero
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { showSaveDialog = true },
                            onDoubleTap = {
                                scale = 1f
                                offset = Offset.Zero
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = stringResource(R.string.image),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(transformableState)
                )

                IconButton(
                    onClick = {
                        isImagePreviewOpen = false
                        scale = 1f
                        offset = Offset.Zero
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White
                    )
                }
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text(stringResource(R.string.save_image)) },
                text = { Text(stringResource(R.string.save_image_prompt)) },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            val saved = saveImageToGallery(context, mediaUrl, newsFeedViewModel)
                            viewModel.notifySaveImage(success = saved)
                            showSaveDialog = false
                        }
                    }) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (showShareDialog) {
            ShareDialog(
                onDismiss = { showShareDialog = false },
                friends = friendsWithAvatars,
                groups = groups,
                currentUserId = currentUserId,
                onShareToFriend = { friendId ->
                    coroutineScope.launch {
                        try {
                            chatViewModel.sendMessage(friendId, shareMessage)
                            viewModel.notifyShareAction(success = true, isGroup = false)
                        } catch (e: Exception) {
                            viewModel.notifyShareAction(success = false, isGroup = false)
                        }
                        showShareDialog = false
                    }
                },
                onShareToGroup = { groupId ->
                    coroutineScope.launch {
                        try {
                            groupChatViewModel.sendGroupMessage(groupId, shareMessage)
                            viewModel.notifyShareAction(success = true, isGroup = true)
                        } catch (e: Exception) {
                            viewModel.notifyShareAction(success = false, isGroup = true)
                        }
                        showShareDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun PostItemTextOnly(
    post: Post,
    imageUri: Uri?,
    onCommentClick: (Post) -> Unit,
    viewModel: NewsFeedViewModel,
    profileViewModel: ProfileViewModel,
    groupChatViewModel: GroupChatViewModel,
    chatViewModel: ChatViewModel,
    friendViewModel: FriendViewModel
) {
    var selectedPrivacy by remember { mutableStateOf(post.targetType) }
    var isExpanded by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val selectedGroupIds = remember {
        mutableStateListOf<String>().apply {
            if (post.targetType == 3 && !post.targetGroupIds.isNullOrEmpty()) {
                addAll(post.targetGroupIds)
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val groups by groupChatViewModel.groups.collectAsState(initial = emptyList())
    val latestChats by chatViewModel.latestChats.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        groupChatViewModel.loadUserGroups()
        chatViewModel.getLatestChats()
        friendViewModel.getAllFriends()
    }

    val friends = friendViewModel.friends.collectAsState().value?.getOrNull() ?: emptyList()
    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value

    LaunchedEffect(friends.toList()) {
        if (friends.isNotEmpty()) {
            profileViewModel.getFriendAvatars(friends.map { it.userId })
        }
    }

    // When both chats and avatars are ready, update chats with avatarUrls
    val friendsWithAvatars = remember(friends, friendAvatars) {
        friends.map { friend ->
            val avatarUrl = friendAvatars.find { it.userId == friend.userId }?.avatarUrl ?: ""
            friend.copy(avatarUrl = avatarUrl)
        }
    }

    val currentUserId = profileViewModel.profile.value?.getOrNull()?.id ?: ""
    val isAuthor = post.authorId == currentUserId

    val truncatedContent = post.content?.let {
        if (it.length > 15) it.take(15) + "..." else it
    } ?: ""

    val shareMessage = """
        ${stringResource(R.string.shared_post)}
        ${stringResource(R.string.from_label)} ${post.authorName ?: "Unknown Author"}
        $truncatedContent
        [post:${post.postId}]
    """.trimIndent()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF001A17),
                        Color(0xFF002B24),
                        Color(0xFF000808),
                        Color(0xFF000000)
                    ),
                    radius = 1500f,
                    center = Offset(0.5f, 0.3f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = Color(0xFF00D09E).copy(alpha = 0.3f),
                        spotColor = Color(0xFF00D09E).copy(alpha = 0.5f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF00D09E).copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        radius = 80f
                                    ),
                                    shape = CircleShape
                                )
                                .padding(2.dp)
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = stringResource(R.string.avatar),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF404040),
                                                Color(0xFF2A2A2A)
                                            )
                                        )
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = post.authorName ?: "Unknown",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = post.createdAt ?: "",
                                color = Color(0xFF00D09E).copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (isAuthor) {
                        Spacer(modifier = Modifier.height(12.dp))
                        PrivacyDropdown(
                            selectedPrivacy = selectedPrivacy,
                            onPrivacyChanged = { newPrivacy ->
                                selectedPrivacy = newPrivacy
                                if (newPrivacy == 3 && selectedGroupIds.isEmpty()) {
                                    viewModel.notifyGroupSelectionError()
                                } else {
                                    viewModel.updatePostTarget(
                                        postId = post.postId,
                                        targetType = newPrivacy,
                                        targetGroupIds = if (newPrivacy == 3 && selectedGroupIds.isNotEmpty()) {
                                            selectedGroupIds.toList()
                                        } else {
                                            null
                                        }
                                    )
                                }
                            },
                            groups = groups,
                            selectedGroupIds = selectedGroupIds,
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .align(Alignment.Start)
                                .padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF00D09E).copy(alpha = 0.05f),
                                        Color.Transparent,
                                        Color(0xFF00D09E).copy(alpha = 0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val content = post.content ?: ""
                            val shouldShowExpand = content.length > 100
                            Text(
                                text = content,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 32.sp,
                                textAlign = TextAlign.Center,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (shouldShowExpand) {
                                Text(
                                    text = if (isExpanded) stringResource(R.string.collapse) else stringResource(
                                        R.string.___see_more
                                    ),
                                    color = Color(0xFF00D09E),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .clickable { isExpanded = !isExpanded }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ActionButtons(
                        post = post,
                        viewModel = viewModel,
                        onCommentClick = onCommentClick,
                        onShareClick = { showShareDialog = true }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D09E).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.TopStart)
                .offset((-50).dp, (-50).dp)
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00B4D8).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.BottomEnd)
                .offset(40.dp, 40.dp)
        )

        if (showShareDialog) {
            ShareDialog(
                onDismiss = { showShareDialog = false },
                friends = friendsWithAvatars,
                groups = groups,
                currentUserId = currentUserId,
                onShareToFriend = { friendId ->
                    coroutineScope.launch {
                        try {
                            chatViewModel.sendMessage(friendId, shareMessage)
                            viewModel.notifyShareAction(success = true, isGroup = false)
                        } catch (e: Exception) {
                            viewModel.notifyShareAction(success = false, isGroup = false)
                        }
                        showShareDialog = false
                    }
                },
                onShareToGroup = { groupId ->
                    coroutineScope.launch {
                        try {
                            groupChatViewModel.sendGroupMessage(groupId, shareMessage)
                            viewModel.notifyShareAction(success = true, isGroup = true)
                        } catch (e: Exception) {
                            viewModel.notifyShareAction(success = false, isGroup = true)
                        }
                        showShareDialog = false
                    }
                }
            )
        }
    }
}

suspend fun saveImageToGallery(
    context: Context,
    imageUrl: String,
    viewModel: NewsFeedViewModel
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // Tải ảnh từ URL bằng OkHttp
            val client = OkHttpClient()
            val request = Request.Builder().url(imageUrl).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Không thể tải ảnh: ${response.code}")
            }

            val inputStream = response.body?.byteStream() ?: throw IOException("Dữ liệu ảnh rỗng")

            // Lưu vào MediaStore
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "saved_image_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Không thể tạo URI cho ảnh")

            resolver.openOutputStream(uri)?.use { outputStream ->
                inputStream.use { it.copyTo(outputStream) }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            true // Thành công
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                viewModel.notifySaveImage(success = false)
            }
            false // Thất bại
        }
    }
}

@Composable
fun ActionButtons(
    post: Post,
    viewModel: NewsFeedViewModel,
    onCommentClick: (Post) -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Like Button
        Card(
            modifier = Modifier
                .shadow(
                    elevation = if (post.isLikedByCurrentUser) 8.dp else 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = if (post.isLikedByCurrentUser)
                        Color.Red.copy(alpha = 0.3f) else Color.Transparent,
                    spotColor = if (post.isLikedByCurrentUser)
                        Color.Red.copy(alpha = 0.5f) else Color.Transparent
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (post.isLikedByCurrentUser)
                    Color(0xFF2D1B1B) else Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        if (post.isLikedByCurrentUser) {
                            viewModel.unlikePost(post.postId)
                        } else {
                            viewModel.likePost(post.postId)
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            if (post.isLikedByCurrentUser) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B).copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (post.isLikedByCurrentUser)
                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.like_action),
                        tint = if (post.isLikedByCurrentUser)
                            Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${post.likesCount}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Comment Button
        Card(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFF00D09E).copy(alpha = 0.2f)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onCommentClick(post) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00D09E).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = stringResource(R.string.comments_action),
                        tint = Color(0xFF00D09E),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${post.commentsCount}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Share Button
        Card(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFF00B4D8).copy(alpha = 0.2f)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onShareClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00B4D8).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_action),
                        tint = Color(0xFF00B4D8),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.share_label),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    friends: List<Friend>,
    groups: List<Group>,
    currentUserId: String,
    onShareToFriend: (String) -> Unit,
    onShareToGroup: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.share_post_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.share_with_friends),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 150.dp)
                        .fillMaxWidth()
                ) {
                    items(friends) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onShareToFriend(friend.userId) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = stringResource(R.string.friend_avatar),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = friend.displayName,
                                color = Color(0xFF00D09E),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.share_with_groups),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 150.dp)
                        .fillMaxWidth()
                ) {
                    items(groups) { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onShareToGroup(group.groupId) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = group.imageUrl,
                                contentDescription = stringResource(R.string.group_avatar),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = group.name,
                                color = Color(0xFF00D09E),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        dismissButton = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSection(
    post: Post,
    viewModel: NewsFeedViewModel,
    profileViewModel: ProfileViewModel,
    commentState: ResultState<Any>,
) {
    var commentText by remember { mutableStateOf("") }
    val comments = when (commentState) {
        is ResultState.Success -> commentState.data as? List<Comment> ?: emptyList()
        else -> emptyList()
    }
    val replyState by viewModel.replyState.collectAsState() // Lắng nghe replyState
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val currentUserId = profileViewModel.profile.value?.getOrNull()?.id ?: ""
    var replyingTo by remember { mutableStateOf<ReplyCommentResponse?>(null) }

    LaunchedEffect(replyState) {
        when (replyState) {
            is ResultState.Success -> {
            }

            is ResultState.Error -> {
            }

            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF1A1A1A),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Fixed Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(24.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF00F5D4),
                                        Color(0xFF00D09E)
                                    )
                                ),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.post_comments_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }

                // Scrollable comment list
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f) // chỉ phần này cuộn
                        .fillMaxWidth()
                ) {
                    items(comments) { comment ->
                        var expanded by remember { mutableStateOf(false) }
                        var showReplyInput by remember { mutableStateOf(false) } // Trạng thái hiển thị input reply
                        var replyText by remember { mutableStateOf("") } // Nội dung reply

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Phần hiển thị thông tin comment
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = comment.authorAvatarUrl,
                                        contentDescription = stringResource(R.string.avatar),
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = comment.authorName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = comment.createdAt,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    if (comment.authorId == currentUserId) {
                                        Box {
                                            IconButton(
                                                onClick = { expanded = true },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = stringResource(R.string.menu_action),
                                                    tint = Color.White.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                            ) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = stringResource(R.string.delete_action),
                                                            color = Color(0xFFFF6B6B),
                                                            fontSize = 14.sp
                                                        )
                                                    },
                                                    onClick = {
                                                        expanded = false
                                                        viewModel.deleteComment(
                                                            post.postId,
                                                            comment.commentId
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = comment.content,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Nút "Trả lời"
                                TextButton(
                                    onClick = {
                                        showReplyInput = !showReplyInput
                                        replyingTo = null // Reset khi trả lời trực tiếp comment
                                    },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = if (showReplyInput && replyingTo == null) stringResource(
                                            R.string.cancel
                                        ) else stringResource(R.string.reply_action),
                                        color = Color(0xFF00D09E),
                                        fontSize = 12.sp
                                    )
                                }

                                // Input field cho reply
                                if (showReplyInput) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = replyText,
                                        onValueChange = { replyText = it },
                                        label = {
                                            Text(
                                                text = if (replyingTo != null) "${stringResource(R.string.reply_to_label)} ${replyingTo!!.authorName}..." else stringResource(
                                                    R.string.enter_reply
                                                ),
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            cursorColor = Color(0xFF00D09E),
                                            focusedBorderColor = Color(0xFF00D09E),
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                            focusedLabelColor = Color(0xFF00D09E),
                                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            if (replyText.isNotBlank()) {
                                                viewModel.replyToComment(
                                                    ReplyCommentRequest(
                                                        commentId = comment.commentId,
                                                        content = replyText,
                                                        parentReplyId = replyingTo?.replyId
                                                    ),
                                                    post.postId
                                                )
                                                replyText = ""
                                                showReplyInput = false
                                                replyingTo = null
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFF00F5D4),
                                                            Color(0xFF00D09E)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .padding(horizontal = 24.dp, vertical = 12.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.send_reply),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                // Hiển thị danh sách reply
                                if (comment.replies.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Column(
                                        modifier = Modifier.padding(start = 0.dp)
                                    ) {
                                        val flattenedReplies = flattenReplies(comment.replies)
                                        flattenedReplies.forEach { reply ->
                                            ReplyItem(
                                                reply = reply,
                                                currentUserId = currentUserId,
                                                onDelete = { replyId ->
                                                    viewModel.deleteReply(replyId, post.postId)
                                                },
                                                onReply = { replyToReply ->
                                                    showReplyInput = true
                                                    replyingTo =
                                                        replyToReply // Lưu reply đang trả lời
                                                    replyText = ""
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Fixed Comment Input
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A1A))
                        .imePadding() // tránh che bởi bàn phím
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = {
                            Text(
                                text = stringResource(R.string.enter_comment),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00D09E),
                            focusedBorderColor = Color(0xFF00D09E),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFF00D09E),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.createComment(post.postId, commentText)
                                commentText = ""
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(comments.size)
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color(0xFF00D09E).copy(alpha = 0.3f)
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF00F5D4), Color(0xFF00D09E))
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.send_comment),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ReplyItem(
    reply: ReplyCommentResponse,
    currentUserId: String,
    onDelete: (String) -> Unit,
    onReply: (ReplyCommentResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (reply.parentReplyName.isNotBlank()) {
            Text(
                text = "${stringResource(R.string.reply_to_comment_label)} ${reply.parentReplyName}",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row {
            AsyncImage(
                model = reply.authorAvatarUrl,
                contentDescription = stringResource(R.string.avatar),
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reply.authorName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reply.createdAt,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (reply.authorId == currentUserId) {
                        Box {
                            IconButton(
                                onClick = { expanded = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.menu_action),
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.delete_action),
                                            color = Color(0xFFFF6B6B),
                                            fontSize = 12.sp
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        onDelete(reply.replyId)
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = reply.content,
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                // "Trả lời" button for the reply
                TextButton(
                    onClick = { onReply(reply) },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = stringResource(R.string.reply_action),
                        color = Color(0xFF00D09E),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

fun flattenReplies(replies: List<ReplyCommentResponse>): List<ReplyCommentResponse> {
    val result = mutableListOf<ReplyCommentResponse>()
    replies.forEach { reply ->
        result.add(reply)
        result.addAll(flattenReplies(reply.replies)) // Thêm các reply lồng nhau
    }
    return result
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    viewModel: NewsFeedViewModel,
    groupChatViewModel: GroupChatViewModel
) {
    var content by remember { mutableStateOf("") }
    var selectedPrivacy by remember { mutableStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val selectedGroupIds = remember { mutableStateListOf<String>() }
    val groups by groupChatViewModel.groups.collectAsState(initial = emptyList())
    val postCreationState by viewModel.postCreationState.collectAsState()
    val isPosting = postCreationState is ResultState.Loading

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    LaunchedEffect(Unit) {
        groupChatViewModel.loadUserGroups()
    }

    LaunchedEffect(postCreationState) {
        if (postCreationState is ResultState.Success) {
            viewModel.clearPostCreationState()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = { if (!isPosting) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isPosting,
            dismissOnClickOutside = !isPosting
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .shadow(24.dp, RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Enable scrolling
                    .padding(bottom = 16.dp) // Ensure bottom padding for safe area
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00D09E),
                                    Color(0xFF00B4D8)
                                )
                            ),
                            shape = RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 28.dp
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.create_post_title),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            onClick = onDismiss,
                            enabled = !isPosting,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // Add spacing above PrivacyDropdown
                    Spacer(modifier = Modifier.height(16.dp)) // Adjust this value as needed

                    // Privacy Dropdown
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        PrivacyDropdown(
                            selectedPrivacy = selectedPrivacy,
                            onPrivacyChanged = { selectedPrivacy = it },
                            groups = groups,
                            selectedGroupIds = selectedGroupIds,
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Text Input
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00D09E).copy(alpha = 0.3f),
                                        Color(0xFF00B4D8).copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8FFFE)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.post_content_placeholder),
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 200.dp) // Flexible height
                                .padding(16.dp),
                            enabled = !isPosting,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color(0xFF1A1A2E),
                                unfocusedTextColor = Color(0xFF1A1A2E),
                                cursorColor = Color(0xFF00D09E)
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Image Picker Button
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        enabled = !isPosting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF00D09E),
                                            Color(0xFF00B4D8)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFFBABEBE),
                                                    Color(0xFF00B4D8)
                                                )
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = stringResource(R.string.select_image_action),
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.add_image_label),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFFFFFF)
                                )
                            }
                        }
                    }

                    // Selected Image
                    selectedImageUri?.let { uri ->
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = stringResource(R.string.image),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(4 / 3f) // Maintain aspect ratio
                                        .heightIn(max = 300.dp) // Limit max height
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = { selectedImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .size(32.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.6f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.remove_image_action),
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Post Button
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                viewModel.createPost(
                                    content = content,
                                    category = "general",
                                    fileUri = selectedImageUri,
                                    targetType = selectedPrivacy,
                                    targetGroupIds = if (selectedPrivacy == 3 && selectedGroupIds.isNotEmpty()) {
                                        selectedGroupIds.joinToString(",")
                                    } else {
                                        null
                                    }
                                )
                            }
                        },
                        enabled = content.isNotBlank() && !isPosting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(12.dp, RoundedCornerShape(22.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (content.isNotBlank() && !isPosting) {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF00D09E),
                                                Color(0xFF00B4D8)
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Gray,
                                                Color.Gray
                                            )
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPosting) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(R.string.posting_label),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.post_action),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


