package DI.Composables.GroupChat

import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.Models.Group.UpdateGroupRequest
import DI.ViewModels.GroupChatViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupProfileScreen(
    groupId: String,
    navController: NavController,
    groupChatViewModel: GroupChatViewModel
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        groupChatViewModel.loadGroupById(groupId)
        groupChatViewModel.loadGroupMembers(groupId)
    }

    LaunchedEffect(Unit) {
        groupChatViewModel.updateGroupEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val group = groupChatViewModel.selectedGroup.collectAsState().value
    val members = groupChatViewModel.groupMembers.collectAsState().value


    var showMemberDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(group?.name ?: "") }
    var editedDes by remember { mutableStateOf(group?.description ?: "") }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserId by remember { mutableStateOf("") }

    var showRemoveUserDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
    ) {
        // Back Button
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(30.dp).offset(x = 10.dp, y = 10.dp)
            )
        }

        // Edit Button
        IconButton(
            onClick = { showEditDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Group",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(120.dp)) {
                FriendAvatar(group?.imageUrl ?: "")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = group?.name ?: "Group Name",
                fontSize = 24.sp,
                color = Color.White
            )

            Text(
                text = "${members.size} members",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                GroupActionButton(
                    icon = Icons.Default.Group,
                    label = "Members",
                    onClick = { showMemberDialog = true }
                )

                GroupActionButton(Icons.Default.Money, "Funds") {
                    navController.navigate("group_fund_screen/$groupId")
                }

                GroupActionButton(icon = Icons.Default.PersonAdd, label = "Add User") {
                    showAddUserDialog = true
                }

                GroupActionButton(icon = Icons.Default.PersonRemove, label = "Remove User") {
                    showRemoveUserDialog = true
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Group Info", fontSize = 18.sp, color = Color(0xFF333333))

                    Spacer(modifier = Modifier.height(12.dp))

                    GroupInfoItem(Icons.Default.Badge, "Group ID", group?.groupId ?: "") {
                        clipboard.setText(AnnotatedString(group?.groupId ?: ""))
                        Toast.makeText(context, "Group ID copied", Toast.LENGTH_SHORT).show()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GroupDescriptionItem(
                        icon = Icons.Default.Info,
                        label = "Description",
                        description = group?.description ?: "No description provided"
                    )
                }
            }
        }
    }

    if (showMemberDialog) {
        AlertDialog(
            onDismissRequest = { showMemberDialog = false },
            title = { Text(stringResource(R.string.group_member))},
            text = {
                Column {
                    members.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(url = member.avatarUrl ?: "", 26)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(text = member.displayName, fontWeight = FontWeight.Bold)
                                val roleLabel = when (member.role) {
                                    0 -> stringResource(R.string.Member)
                                    1 -> stringResource(R.string.Collaborator)
                                    2 -> stringResource(R.string.Admin)
                                    else -> "Không xác định"
                                }
                                Text(text = stringResource(R.string.role, roleLabel), fontSize = 12.sp)
                                Text(text = stringResource(R.string.joined_at, formatDate(member.joinedAt)), fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMemberDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Group") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Group Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedDes,
                        onValueChange = { editedDes = it },
                        label = { Text("Group Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Gọi hàm ViewModel để cập nhật
                        group?.let {
                            groupChatViewModel.updateGroup(it.groupId, UpdateGroupRequest(editedName, editedDes))
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddUserDialog) {
        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("Enter User ID") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newUserId,
                        onValueChange = { newUserId = it },
                        label = { Text("User ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newUserId.isNotBlank()) {
                        groupChatViewModel.addUserToGroup(groupId, newUserId.trim())
                        showAddUserDialog = false
                        newUserId = ""
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddUserDialog = false
                    newUserId = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRemoveUserDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveUserDialog = false },
            title = { Text("Choose an user to remove") },
            text = {
                Column {
                    members.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    groupChatViewModel.removeUserFromGroup(groupId, member.userId)
                                    showRemoveUserDialog = false
                                }
                                .padding(8.dp)
                        ) {
                            FriendAvatar(member.avatarUrl ?: "")
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = member.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRemoveUserDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun GroupActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = MainColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun GroupInfoItem(icon: ImageVector, label: String, value: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MainColor, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, color = Color.Black)
        }
        IconButton(onClick = onCopy) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MainColor, modifier = Modifier.size(16.dp))
        }
    }
}

fun formatDate(input: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(input, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        input
    }
}

@Composable
fun GroupDescriptionItem(
    icon: ImageVector,
    label: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF53dba9),
            modifier = Modifier
                .size(26.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = description,
                fontSize = 16.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
    }
}
