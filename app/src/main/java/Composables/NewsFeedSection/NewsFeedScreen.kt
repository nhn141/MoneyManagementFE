package DI.Composables.NewsFeedSection

import DI.Composables.ProfileSection.uriToFile
import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.ResultState
import DI.Models.NewsFeed.Post
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import DI.ViewModels.NewsFeedViewModel
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.res.painterResource
import com.example.moneymanagement_frontend.R
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.isNotEmpty
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import kotlin.io.encoding.ExperimentalEncodingApi
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(viewModel: NewsFeedViewModel) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMore by viewModel.hasMorePosts.collectAsState()
    val postCreationState by viewModel.postCreationState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showCommentSheet by remember { mutableStateOf(false) }
    var selectedPostForComment by remember { mutableStateOf<Post?>(null) }
    val commentState by viewModel.commentState.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { posts.size }
    )

    // Xử lý trạng thái tạo bài đăng
    LaunchedEffect(postCreationState) {
        when (postCreationState) {
            is ResultState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Đăng bài thành công!")
                }
            }
            is ResultState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        (postCreationState as ResultState.Error).message
                    )
                }
            }
            else -> { /* Không làm gì khi đang tải hoặc null */ }
        }
    }

    // Tải thêm bài đăng khi cuộn đến gần cuối
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage >= posts.size - 2 && hasMore && !isLoading) {
            viewModel.loadNextPost()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF001A17), // Dark green base
                        Color(0xFF000808), // Almost black
                        Color(0xFF000000)  // Pure black
                    ),
                    radius = 1200f
                )
            )
    ) {
        // Hiển thị danh sách bài đăng
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
                        text = "Không có bài đăng nào",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hãy tạo bài đăng đầu tiên của bạn!",
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
                    }
                )
            }
        }

        // Hiển thị lỗi chung
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

        // Hiển thị loading khi tải thêm bài đăng
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
                        text = "Đang tải thêm...",
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
                viewModel = viewModel
            )
        }

        // Modern FAB with gradient and glow effect
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(72.dp)
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
                    contentDescription = "Add Post",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }

        // Custom Snackbar
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
    }

    if (showCommentSheet && selectedPostForComment != null) {
        ModalBottomSheet(
            onDismissRequest = { showCommentSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1A1A1A),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(48.dp)
                        .height(4.dp)
                        .background(
                            Color(0xFF00D09E).copy(alpha = 0.6f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            CommentSection(
                post = selectedPostForComment!!,
                viewModel = viewModel,
                commentState = commentState
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    onCommentClick: (Post) -> Unit,
    viewModel: NewsFeedViewModel
) {
    val imageUri = post.authorAvatarUrl?.toUri()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (post.mediaUrl != null) {
            AsyncImage(
                model = post.mediaUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF001A17),
                                Color(0xFF002B24),
                                Color(0xFF000000)
                            )
                        )
                    )
            )
        }

        // Enhanced gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0xFF00D09E).copy(alpha = 0.2f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A).copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Avatar
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Author Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentScale = ContentScale.Crop
                        )

                        Column {
                            // Username
                            Text(
                                text = post.authorName ?: "Unknown",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // Created At
                            Text(
                                text = post.createdAt ?: "",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = post.content ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Enhanced Like button
                        Card(
                            modifier = Modifier
                                .shadow(
                                    elevation = if (post.isLikedByCurrentUser) 8.dp else 4.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    ambientColor = if (post.isLikedByCurrentUser)
                                        Color.Red.copy(alpha = 0.3f) else Color.Transparent
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (post.isLikedByCurrentUser)
                                    Color(0xFF2D1B1B) else Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(24.dp)
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
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = if (post.isLikedByCurrentUser)
                                        Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (post.isLikedByCurrentUser)
                                        Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${post.likesCount}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Enhanced Comment button
                        Card(
                            modifier = Modifier
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onCommentClick(post) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = "Comments",
                                    tint = Color(0xFF00D09E),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${post.commentsCount}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSection(
    post: Post,
    viewModel: NewsFeedViewModel,
    commentState: ResultState<Any>,
) {
    var commentText by remember { mutableStateOf("") }
    val comments = when (commentState) {
        is ResultState.Success -> commentState.data as? List<Comment> ?: emptyList()
        else -> emptyList()
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header with gradient accent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
                "Bình luận bài viết",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display comments
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comments) { comment ->
                var expanded by remember { mutableStateOf(false) }

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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = comment.content,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        Box {
                            IconButton(
                                onClick = { expanded = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(
                                    Color(0xFF2A2A2A),
                                    RoundedCornerShape(12.dp)
                                )
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Xóa",
                                            color = Color(0xFFFF6B6B),
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        viewModel.deleteComment(post.postId, comment.commentId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Modern text input
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = {
                Text(
                    "Nhập bình luận...",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Modern send button
        Button(
            onClick = {
                if (commentText.isNotBlank()) {
                    viewModel.createComment(post.postId, commentText)
                    commentText = ""
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFF00D09E).copy(alpha = 0.3f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
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
                    "Gửi",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalEncodingApi::class)
fun encodeImageToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let {
            Base64.encodeToString(it, Base64.DEFAULT)
        }
    } catch (e: Exception) {
        null
    }
}


@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    viewModel: NewsFeedViewModel
) {
    var content by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val postCreationState by viewModel.postCreationState.collectAsState()
    val isPosting = postCreationState is ResultState.Loading

    // Đóng dialog khi đăng thành công
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
                modifier = Modifier.fillMaxSize()
            ) {
                // Header với gradient background
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
                            text = "Tạo bài viết",
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
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Content input với gradient border
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
                                    "Bạn đang nghĩ gì?",
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
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

                    // Image selection button với gradient
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
                                        contentDescription = "Select Image",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    "Thêm ảnh",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFFFFFF)
                                )
                            }
                        }
                    }

                    // Image preview với animation
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
                                    contentDescription = "Image Preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                // Remove image button
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
                                        contentDescription = "Remove Image",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Post button với gradient đẹp
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                val base64Image = selectedImageUri?.let {
                                    encodeImageToBase64(context, it)
                                }
                                viewModel.createPost(content = content, mediaFile = base64Image)
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
                                        "Đang đăng...",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    "Đăng",
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
