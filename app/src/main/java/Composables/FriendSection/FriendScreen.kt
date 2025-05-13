package com.example.friendsapp

import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.ViewModels.FriendViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch

// Define main color as provided
val MainColor = Color(0xFF53dba9)

@Composable
fun FriendsAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MainColor,
            secondary = MainColor.copy(alpha = 0.7f),
            tertiary = MainColor.copy(alpha = 0.5f),
            surface = Color.White,
            background = Color(0xFFF5F5F5)
        ),
        content = content
    )
}

@Composable
fun FriendsApp(
    friendViewModel: FriendViewModel = hiltViewModel(),
    navController: NavController
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var friendIdInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Collect UI events once
    LaunchedEffect(Unit) {
        launch {
            friendViewModel.addFriendEvent.collect { event ->
                when (event) {
                    is FriendViewModel.UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    } else -> {}
                }
            }
        }

        launch {
            friendViewModel.acceptFriendRequestEvent.collect { event ->
                when (event) {
                    is FriendViewModel.UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    } else -> {}
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

    Scaffold(
        topBar = { CustomTopBar(
            title = "My Friends",
            onBackClick = { navController.popBackStack() },
            onFriendRequestsClick = { showPendingRequestsDialog = true },
            onSearchClick = { /* Search action */ }
        ) },
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
        FriendsList(Modifier.padding(innerPadding), friendViewModel)

        // Add Friend Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    friendIdInput = ""
                },
                title = { Text("Add Friend") },
                text = {
                    Column {
                        Text(
                            "Enter Friend ID to send request:",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = friendIdInput,
                            onValueChange = { friendIdInput = it },
                            label = { Text("Friend ID") },
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
                        Text("Send Request")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            friendIdInput = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Pending Requests Dialog
        if(showPendingRequestsDialog) {
            AlertDialog(
                onDismissRequest = { showPendingRequestsDialog = false },
                title = {
                    Text("Pending Requests") },
                text = {
                    if(friendRequests.isEmpty()) {
                        Text("No pending requests.")
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "You have ${friendRequests.size} pending requests.",
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
                        Text("Close")
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
                text = "@${request.username}",
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
                    Icon(Icons.Default.Close, contentDescription = "Reject")
                    Spacer(Modifier.width(4.dp))
                    Text("Reject")
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                    Spacer(Modifier.width(4.dp))
                    Text("Accept")
                }
            }
        }
    }
}




@Composable
fun FriendsList(
    modifier: Modifier = Modifier,
    friendViewModel: FriendViewModel
) {

    LaunchedEffect(Unit) {
        friendViewModel.getAllFriends()
    }

    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()

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
                Text("No friends found.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {

            val onlineFriendCount = friends.count { it.isOnline }
            FriendsStatsSummary(friends.size, onlineFriendCount)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(friends) { friend ->
                    FriendCard(friend, friendViewModel)
                }
            }
        }
    }
}

@Composable
fun FriendsStatsSummary(friendCount: Int, onlineFriendCount: Int = 0, ) {
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
            StatItem(count = friendCount, label = "Friends")
            StatItem(count = onlineFriendCount, label = "Online")
            StatItem(count = 5, label = "Pending")
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
fun FriendCard(friend: Friend, friendViewModel: FriendViewModel) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch {
            friendViewModel.deleteFriendEvent.collect { event ->
                when (event) {
                    is FriendViewModel.UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
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
            // Avatar with status indicator
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp)
            ) {
                // Avatar
                Icon(
                    painter = painterResource(R.drawable.profile_image),
                    contentDescription = "Avatar for ${friend.username}",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MainColor.copy(alpha = 0.2f))
                        .padding(8.dp),
                    tint = MainColor
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if(friend.isOnline) Color.Green
                            else Color.Gray
                        )
                        .border(2.dp, Color.White, CircleShape)
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
                    text = friend.lastActive ?: "Last active unknown",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { /* Message action */ }) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "Message",
                    tint = MainColor,
                )
            }
            IconButton(onClick = { friendViewModel.deleteFriend(friend.userId) }) {
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
    onBackClick: () -> Unit = {},
    onFriendRequestsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainColor)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Icon
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.SubdirectoryArrowLeft,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Title
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
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
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}
