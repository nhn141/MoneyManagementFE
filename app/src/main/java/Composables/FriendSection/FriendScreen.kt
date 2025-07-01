package com.example.friendsapp

import DI.Composables.CategorySection.ModernColors
import DI.Composables.HomeSection.MoneyAppColors
import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.Models.UiEvent.UiEvent
import DI.Utils.DateTimeUtils
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch

// Define main color as provided
val MainColor = Color(0xFF53dba9)

@Composable
fun FriendsScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MainColor,
            secondary = MainColor.copy(alpha = 0.7f),
            tertiary = MainColor.copy(alpha = 0.5f),
            surface = Color.White,
            background = MoneyAppColors.Background
        ),
        content = content
    )
}

@Composable
fun FriendsScreen(
    friendViewModel: FriendViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        friendViewModel.getAllFriends()
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var friendIdInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Collect UI events once
    LaunchedEffect(Unit) {
        launch {
            friendViewModel.addFriendEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        launch {
            friendViewModel.acceptFriendRequestEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    var showPendingRequestsDialog by remember { mutableStateOf(false) }
    val friendRequestsResult = friendViewModel.friendRequests.collectAsState()
    val friendRequests = friendRequestsResult.value?.getOrNull() ?: emptyList()

    LaunchedEffect(showPendingRequestsDialog) {
        launch {
            friendViewModel.getFriendRequests()
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.my_friends),
                onFriendRequestsClick = { showPendingRequestsDialog = true },
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClearQuery = { searchQuery = "" },
                onNavBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MainColor
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Friend",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        FriendsList(
            modifier = Modifier.padding(innerPadding),
            friendViewModel = friendViewModel,
            pendingRequests = friendRequests.size,
            profileViewModel = profileViewModel,
            navController = navController,
            searchQuery = searchQuery,
        )

        // Add Friend Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    friendIdInput = ""
                },
                title = { Text(stringResource(R.string.add_friend)) },
                text = {
                    Column {
                        Text(
                            stringResource(R.string.enter_friend_id),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = friendIdInput,
                            onValueChange = { friendIdInput = it },
                            label = { Text(stringResource(R.string.friend_id)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainColor,
                                focusedLabelColor = MainColor,
                                cursorColor = MainColor
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Handle adding friend with ID: friendIdInput
                            val addFriendRequest = AddFriendRequest(friendIdInput)
                            friendViewModel.addFriend(addFriendRequest)
                            showAddDialog = false
                            friendIdInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor
                        )
                    ) {
                        Text(stringResource(R.string.send_request))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            friendIdInput = ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Pending Requests Dialog
        if (showPendingRequestsDialog) {
            AlertDialog(
                onDismissRequest = { showPendingRequestsDialog = false },
                title = {
                    Text(stringResource(R.string.pending_requests))
                },
                text = {
                    if (friendRequests.isEmpty()) {
                        Text(stringResource(R.string.no_pending_requests))
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                stringResource(
                                    R.string.pending_requests_count,
                                    friendRequests.size
                                ),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyColumn {
                                items(friendRequests) { request ->
                                    PendingRequestItem(
                                        request = request,
                                        onAccept = {
                                            friendViewModel.acceptFriendRequest(request.userId)
                                            showPendingRequestsDialog = false
                                        },
                                        onReject = {
                                            friendViewModel.rejectFriendRequest(request.userId)
                                            showPendingRequestsDialog = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPendingRequestsDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
    }
}

@Composable
fun PendingRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val formattedDateTime = DateTimeUtils.formatDateTime(request.requestedAt, LocalContext.current)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 18.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User Info
            Text(
                text = request.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    R.string.sent_request_at,
                    formattedDateTime.formattedTime + ", " + formattedDateTime.formattedDayMonth
                ),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.reject))
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.accept))
                }
            }
        }
    }
}

@Composable
fun FriendsList(
    modifier: Modifier = Modifier,
    friendViewModel: FriendViewModel,
    pendingRequests: Int,
    profileViewModel: ProfileViewModel,
    navController: NavController,
    searchQuery: String
) {
    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()

    val filteredFriends = remember(friends, searchQuery) {
        if (searchQuery.isEmpty()) {
            friends
        } else {
            friends.filter { friend ->
                friend.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(filteredFriends) {
        val friendIds = filteredFriends.map { it.userId }
        profileViewModel.getFriendAvatars(friendIds)
    }

    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    // State to track the friendId for deletion
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var friendIdToDelete by remember { mutableStateOf<String?>(null) }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && friendIdToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                friendIdToDelete = null
            },
            title = {
                Text(
                    stringResource(R.string.delete_friend),
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.OnSurface
                )
            },
            text = {
                Text(
                    stringResource(R.string.delete_friend_confirm),
                    color = ModernColors.OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        friendIdToDelete?.let { friendId ->
                            friendViewModel.deleteFriend(friendId) // Call deleteFriend with friendId
                        }
                        showDeleteConfirmation = false
                        friendIdToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ModernColors.Error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.delete), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        friendIdToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        color = ModernColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (friends.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_friends_found),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (filteredFriends.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.no_friends_search),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.try_different_search),
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            val onlineFriendCount = friends.count { it.isOnline }
            FriendsStatsSummary(friends.size, onlineFriendCount, pendingRequests)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(filteredFriends) { friend ->
                    FriendCard(
                        friend = friend,
                        friendViewModel = friendViewModel,
                        friendAvatarUrl = friendAvatars.find { it.userId == friend.userId }?.avatarUrl
                            ?: "",
                        isLoadingAvatar = isLoadingAvatar.value,
                        navController = navController,
                        onShowDeleteConfirmation = {
                            friendIdToDelete = friend.userId // Set the friendId to delete
                            showDeleteConfirmation = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendsStatsSummary(friendCount: Int, onlineFriendCount: Int, pendingRequest: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MainColor.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = friendCount, label = stringResource(R.string.friends))
            StatItem(count = onlineFriendCount, label = stringResource(R.string.online))
            StatItem(count = pendingRequest, label = stringResource(R.string.pending))
        }
    }
}

@Composable
fun StatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MainColor
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun FriendCard(
    friend: Friend,
    friendViewModel: FriendViewModel,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    navController: NavController,
    onShowDeleteConfirmation: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch {
            friendViewModel.deleteFriendEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online dot
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (isLoadingAvatar) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DI.Composables.ProfileSection.MainColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    FriendAvatar(friendAvatarUrl)
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (friend.isOnline) Color(0xFF4CAF50) else Color.Gray)
                        .border(1.dp, Color.White, CircleShape)
                )
            }

            // Friend info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = friend.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = friend.username,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { navController.navigate("chat_message/${friend.userId}") }) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "Message",
                    tint = MainColor,
                )
            }
            IconButton(onClick = { onShowDeleteConfirmation() }) {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = "Delete",
                    tint = Color.Red,
                )
            }
        }
    }
}

@Composable
fun CustomTopBar(
    title: String,
    onFriendRequestsClick: () -> Unit = {},
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onNavBack: () -> Unit,
) {

    var onSearchMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainColor)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (onSearchMode) {
                CustomSearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onClearQuery = {
                        onClearQuery()
                        onSearchMode = false
                    }
                )
            } else {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .clickable(onClick = { onNavBack() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                // Friend Requests Icon
                IconButton(onClick = onFriendRequestsClick) {
                    Icon(
                        imageVector = Icons.Filled.Contacts,
                        contentDescription = "Friends",
                        tint = Color.White
                    )
                }

                // Search Icon
                IconButton(onClick = {
                    onSearchMode = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) { innerTextField ->
            Box(
                modifier = Modifier.weight(1f),
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

        IconButton(
            onClick = { onClearQuery() },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = Color.White
            )
        }
    }
}