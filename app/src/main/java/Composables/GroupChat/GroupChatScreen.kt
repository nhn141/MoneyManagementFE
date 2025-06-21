package DI.Composables.GroupChat

// GroupChatScreen.kt

import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Group.CreateGroupRequest
import DI.Models.Group.SimulatedLatestGroupChatDto
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.ProfileViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp


@Composable
fun GroupChatScreen(
    navController: NavController,
    groupChatViewModel: GroupChatViewModel,
    profileViewModel: ProfileViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val profile by profileViewModel.profile.collectAsState()
    val groupChats by groupChatViewModel.simulatedGroupChats.collectAsState()
    val error by groupChatViewModel.error.collectAsState()
    val groups by groupChatViewModel.groups.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("GroupChatScreen", "Starting simulateLatestGroupChats()")
        groupChatViewModel.simulateLatestGroupChats()
    }

    LaunchedEffect(Unit) {
        groupChatViewModel.createGroupEvent.collect { event ->
            Toast.makeText(context, event, Toast.LENGTH_SHORT).show()
            showCreateDialog = false
            groupName = ""
            groupDescription = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Group Messages", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Group", tint = Color(0xFF53dba9))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" }
        )
        Spacer(modifier = Modifier.height(16.dp))

        val filteredGroups = if (searchQuery.isEmpty()) {
            groupChats
        } else {
            groupChats.filter {
                it.groupName.contains(searchQuery, ignoreCase = true)
            }
        }

        if (filteredGroups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No groups found", fontSize = 16.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredGroups.size) { index ->
                    val group = filteredGroups[index]
                    GroupMessageItem(
                        title = group.groupName,
                        message = group.latestMessageContent ?: "",
                        count = group.unreadCount,
                        time = group.sendAt,
                        color = Color(0xFF5C6BC0),
                        onClick = {
                            navController.navigate("group_chat_message/${group.groupId}")
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Group") },
            text = {
                Column {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = groupDescription,
                        onValueChange = { groupDescription = it },
                        label = { Text("Description") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            groupChatViewModel.createGroup(
                                CreateGroupRequest(
                                    name = groupName,
                                    description = groupDescription.ifBlank { null }
                                )
                            )
                        } else {
                            Toast.makeText(context, "Group name is required", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x809AE7C5)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search groups",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = onClearQuery,
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
    }
}

@Composable
fun GroupMessageItem(
    title: String,
    message: String,
    count: Int,
    time: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            FriendAvatar("")
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(message, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(horizontalAlignment = Alignment.End) {
            if (!time.isNullOrBlank()) {
                Text(ChatTimeFormatter.formatTimestamp(time), fontSize = 12.sp)
            } else {
                Text("No Massage Found", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    //.background(if (isAlert) Color(0xFFD32F2F) else Color(0xFFB0BEC5)),
                    .background(if (count != 0) Color(0xFFD32F2F) else Color(0xFFB0BEC5)),
                contentAlignment = Alignment.Center
            ) {
                Text(count.toString(), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}


