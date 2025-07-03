package DI.Composables.GroupChat

import DI.API.TokenHandler.AuthStorage
import DI.Models.GroupModeration.BanKickUserRequest
import DI.Models.GroupModeration.GroupUserActionRequest
import DI.Models.GroupModeration.MuteUserRequest
import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.Composables.ProfileSection.uriToFile
import DI.Models.Group.MemberWithStatus
import DI.Models.Group.UpdateGroupRequest
import DI.Navigation.Routes
import DI.ViewModels.FriendViewModel
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.GroupModerationViewModel
import DI.ViewModels.ProfileViewModel
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun GroupProfileScreen(
    groupId: String,
    navController: NavController,
    groupChatViewModel: GroupChatViewModel,
    groupModerationViewModel: GroupModerationViewModel,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        groupChatViewModel.loadGroupById(groupId)
        groupChatViewModel.loadGroupMembers(groupId)
        groupChatViewModel.getGroupAvatar(groupId)
        groupModerationViewModel.getAllMemberStatuses(groupId)
        friendViewModel.getAllFriends()
    }

    LaunchedEffect(Unit) {
        groupChatViewModel.updateGroupEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val nonComposableStrings = remember {
        object {
            val avatarUploadSuccess = context.getString(R.string.avatar_upload_success)
            val avatarUploadFailed = context.getString(R.string.avatar_upload_failed)
        }
    }

    val group = groupChatViewModel.selectedGroup.collectAsState().value

    LaunchedEffect(Unit) {
        group?.groupId?.let { groupChatViewModel.loadGroupById(it) }
    }

    val members = groupChatViewModel.groupMembers.collectAsState().value

    val groupAvatarUrl by groupChatViewModel.groupAvatarUrl.collectAsState()
    val isAvatarLoading by groupChatViewModel.isAvatarLoading.collectAsState()

    var showMemberDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(group?.name ?: "") }
    var editedDes by remember { mutableStateOf(group?.description ?: "") }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserId by remember { mutableStateOf("") }

    var showRemoveUserDialog by remember { mutableStateOf(false) }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val userGroupStatus = groupModerationViewModel.userGroupStatus.collectAsState().value
    val allMemberStatus = groupModerationViewModel.allMemberStatuses.collectAsState().value

    var showMuteDialog by remember { mutableStateOf(false) }
    var showBanDialog by remember { mutableStateOf(false) }

    var selectedMemberForAction by remember { mutableStateOf<MemberWithStatus?>(null) }
    var reason by remember { mutableStateOf("") }
    var muteTime by remember { mutableIntStateOf(0) }

    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)

    val currentUser = members.find { it.userId == currentUserId }

    val currentUserRole = currentUser?.role ?: -1

    val friendsResult by friendViewModel.friends.collectAsState()

    val friends = friendsResult?.getOrNull().orEmpty()

    val membersIdSet = members.map { it.userId }.toSet()

    val friendsNotInGroup = friends.filter { friend ->
        friend.userId !in membersIdSet
    }

    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    LaunchedEffect(friendsNotInGroup) {
        val friendIds = friendsNotInGroup.map { it.userId }
        profileViewModel.getFriendAvatars(friendIds)
    }

    val membersWithStatus = members.mapNotNull { member ->
        val status = allMemberStatus.find { it.userId == member.userId }

        status?.let {
            MemberWithStatus(
                userId = member.userId,
                displayName = member.displayName,
                avatarUrl = member.avatarUrl,
                role = member.role,
                joinedAt = member.joinedAt,
                isMuted = it.isMuted,
                isBanned = it.isBanned,
                mutedAt = it.mutedAt,
                mutedUntil = it.mutedUntil,
                muteReason = it.muteReason,
                banReason = it.banReason,
                lastModerationUpdate = it.lastModerationUpdate
            )
        }
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                uri: Uri? ->
            uri?.let {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val file = uriToFile(it, context)
                        if (file != null) {
                            groupChatViewModel.uploadGroupAvatar(groupId, file)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    nonComposableStrings.avatarUploadSuccess,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                nonComposableStrings.avatarUploadFailed,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Listen for various UI events in parallel
        scope.launch {
            groupModerationViewModel.muteUserResult.collect { result ->
                result.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_mute_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_mute_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        scope.launch {
            groupModerationViewModel.unmuteUserResult.collect { result ->
                result.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_unmute_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_unmute_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        scope.launch {
            groupModerationViewModel.banUserResult.collect { result ->
                result.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_ban_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_ban_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        scope.launch {
            groupModerationViewModel.unbanUserResult.collect { result ->
                result.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_unban_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_unban_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        scope.launch {
            groupModerationViewModel.grantModeratorRoleResult.collect { result ->
                result.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_grant_mod_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_grant_mod_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        scope.launch {
            groupModerationViewModel.revokeModeratorRoleResult.collect { result ->
                result.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_revoke_mod_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_profile_revoke_mod_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        scope.launch {
            groupChatViewModel.groupMemberChangeEvent.collect { eventType ->
                val message = when (eventType) {
                    "add" -> context.getString(R.string.group_profile_group_member_add_success)
                    "remove" -> context.getString(R.string.group_profile_group_member_remove_success)
                    else -> context.getString(R.string.group_profile_group_member_error)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
    ) {
        // Back Button
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.group_profile_back),
                tint = Color.White,
                modifier = Modifier.size(30.dp).offset(x = 10.dp, y = 10.dp)
            )
        }

        if (currentUserRole != 0)
        {
            // Edit Button
            IconButton(
                onClick = { showEditDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.group_profile_edit_group),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Avatar Section
                Box(modifier = Modifier.size(120.dp)) {
                    if (isAvatarLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        if (groupAvatarUrl != null) {
                            val url = groupAvatarUrl ?: ""
                            FriendAvatar(url)
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.profile_image),
                                contentDescription = stringResource(R.string.default_avatar)
                            )
                        }
                    }

                    if (currentUserRole != 0)
                    {
                        Box(
                            modifier =
                            Modifier.size(32.dp)
                                .clip(CircleShape)
                                .background(MainColor)
                                .align(Alignment.BottomEnd)
                                .clickable { launcher.launch("image/*") }
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.group_profile_change_avatar),
                                tint = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.BottomStart)
                                .clickable {
                                    groupAvatarUrl?.let {
                                        groupChatViewModel.deleteGroupAvatar(groupId)
                                    }
                                }
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.group_profile_delete_avatar),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = group?.name ?: stringResource(R.string.group_profile_default_name),
                fontSize = 24.sp,
                color = Color.White
            )

            Text(
                text = stringResource(R.string.group_profile_member_count, members.size),
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
                    label = stringResource(R.string.group_profile_members_tab),
                    onClick = { showMemberDialog = true }
                )

                GroupActionButton(Icons.Default.Money, stringResource(R.string.group_profile_funds_tab)) {
                    navController.navigate("group_fund_screen/$groupId")
                }

                if (currentUserRole != 0)
                {
                    GroupActionButton(icon = Icons.Default.PersonAdd, label = stringResource(R.string.group_profile_add_user)) {
                        showAddUserDialog = true
                    }

                    GroupActionButton(icon = Icons.Default.PersonRemove, label = stringResource(R.string.group_profile_remove_user)) {
                        showRemoveUserDialog = true
                    }
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
                    Text(stringResource(R.string.group_profile_info), fontSize = 18.sp, color = Color(0xFF333333))

                    Spacer(modifier = Modifier.height(12.dp))

                    val txt2 = stringResource(R.string.group_profile_group_id_copied)

                    GroupInfoItem(Icons.Default.Badge, stringResource(R.string.group_profile_id), group?.groupId ?: "") {
                        clipboard.setText(AnnotatedString(group?.groupId ?: ""))
                        Toast.makeText(context, txt2, Toast.LENGTH_SHORT).show()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GroupDescriptionItem(
                        icon = Icons.Default.Info,
                        label = stringResource(R.string.group_profile_description),
                        description = group?.description ?: stringResource(R.string.group_profile_description_empty)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.group_profile_leave_notice),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Button(
                onClick = {
                    showConfirmDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.group_profile_leave_group),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirmDialog = false

                            if (currentUserRole == 2) { // is Admin
                                groupChatViewModel.adminLeaveGroup(groupId) { result ->
                                    result?.let {
                                        if (it.success) {
                                            when (it.action) {
                                                "leave" -> {
                                                    // Admin đã leave, group vẫn tồn tại
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.group_profile_admin_leave_success),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.navigateToGroupChatScreenPopUp()
                                                }
                                                "delete" -> {
                                                    // Group bị xóa
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.group_profile_group_deleted),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.navigateToGroupChatScreenPopUp()
                                                }
                                                else -> {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.group_profile_unknown_action),
                                                        Toast.LENGTH_SHORT
                                                    ).show()                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.group_profile_leave_group_error),
                                                Toast.LENGTH_SHORT
                                            ).show()                                        }
                                    } ?: run {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.group_profile_leave_group_error),
                                            Toast.LENGTH_SHORT
                                        ).show()                                    }
                                }
                            } else {
                                groupChatViewModel.leaveGroup(groupId)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.group_profile_leave_group_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigateToGroupChatScreenPopUp()
                            }
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    title = { Text(stringResource(R.string.group_profile_confirm_leave_title)) },
                    text = { Text(stringResource(R.string.group_profile_confirm_leave)) }
                )
            }
        }
    }

    fun showMuteDialog(member: MemberWithStatus) {
        selectedMemberForAction = member
        showMuteDialog = true
        reason = ""
        muteTime = 0
    }

    fun showBanDialog(member: MemberWithStatus) {
        selectedMemberForAction = member
        showBanDialog = true
        reason = ""
    }

    val txt = stringResource(R.string.group_profile_reason_time_required)

    // Handle mute or ban action
    fun handleMuteAction() {
        selectedMemberForAction?.let { member ->
            if (muteTime > 0 && reason.isNotBlank()) {
                Log.d("GroupProfile", "Calling API with groupId: $groupId")
                Log.d("GroupProfile", "Calling API with groupId: $member.userId")
                groupModerationViewModel.muteUser(MuteUserRequest(groupId, member.userId, reason, muteTime))
            } else {
                Toast.makeText(context, txt, Toast.LENGTH_SHORT).show()
            }
            showMuteDialog = false
        }
    }

    val txt1 = stringResource(R.string.group_profile_reason_required)
    // Handle ban action
    fun handleBanAction() {
        selectedMemberForAction?.let { member ->
            if (reason.isNotBlank()) {
                Log.d("GroupProfile", "Calling API with groupId: $groupId")
                Log.d("GroupProfile", "Calling API with groupId: $member.userId")
                groupModerationViewModel.banUser(BanKickUserRequest(groupId, member.userId, reason))
            } else {
                Toast.makeText(context, txt1, Toast.LENGTH_SHORT).show()
            }
            showBanDialog = false
        }
    }

    // Member Dialog
    if (showMemberDialog) {
        AlertDialog(
            onDismissRequest = { showMemberDialog = false },
            title = { Text(stringResource(R.string.group_profile_members)) },
            text = {
                Column {
                    membersWithStatus.forEach { member ->
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Avatar(url = member.avatarUrl ?: "", 26)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                if (member.userId != currentUserId)
                                {
                                    Text(text = member.displayName, fontWeight = FontWeight.Bold)
                                }
                                else
                                {
                                    Text(
                                        text = stringResource(
                                            R.string.group_profile_member_me,
                                            member.displayName
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(text = getRoleLabel(member.role))
                                Text(text = formatDate(member.joinedAt))
                            }

                            // Action buttons for admin/collaborator
                            if (currentUserRole != 0) {
                                if (member.role != 2 && member.userId != currentUserId)
                                {
                                   //Column () {
                                       Row(horizontalArrangement = Arrangement.End, modifier = Modifier.weight(1f).padding(8.dp)) {
                                           // Mute/Unmute Button
                                           if (member.isMuted)
                                           {
                                               IconButton(onClick = {
                                                   Log.d("GroupProfile", "Calling API with groupId: $groupId")
                                                   Log.d("GroupProfile", "Calling API with groupId: $member.userId")
                                                   groupModerationViewModel.unmuteUser(
                                                       GroupUserActionRequest(groupId, member.userId)
                                                   )
                                               }) {
                                                   Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = stringResource(R.string.group_profile_unmute))
                                               }
                                           }
                                           else
                                           {
                                               IconButton(onClick = {
                                                   showMuteDialog(member)
                                               }) {
                                                   Icon(imageVector = Icons.AutoMirrored.Filled.VolumeOff, contentDescription = stringResource(R.string.group_profile_mute))
                                               }
                                           }

                                           // Ban/Unban Button
                                           if (member.isBanned)
                                           {
                                               IconButton(onClick = {
                                                   Log.d("GroupProfile", "Calling API with groupId: $groupId")
                                                   Log.d("GroupProfile", "Calling API with groupId: $member.userId")
                                                   groupModerationViewModel.unbanUser(
                                                       GroupUserActionRequest(groupId, member.userId)
                                                   )
                                               }) {
                                                   Icon(imageVector = Icons.Default.Accessibility , contentDescription = stringResource(R.string.group_profile_unban))
                                               }
                                           }
                                           else
                                           {
                                               IconButton(onClick = {
                                                   showBanDialog(member)
                                               }) {
                                                   Icon(imageVector = Icons.Default.Block, contentDescription = stringResource(R.string.group_profile_ban))
                                               }
                                           }

                                           // Grant/Revoke Mod Role Button
                                           IconButton(onClick = {
                                               if (member.role == 0) {
                                                   Log.d("GroupProfile", "Calling API with groupId: $groupId")
                                                   Log.d("GroupProfile", "Calling API with groupId: $member.userId")
                                                   groupChatViewModel.assignCollaborator(groupId, member.userId)
                                               } else if (member.role == 1) {
                                                   Log.d("GroupProfile", "Calling API with groupId: $groupId")
                                                   Log.d("GroupProfile", "Calling API with groupId: $member.userId")
                                                   groupChatViewModel.loadGroupMembers(groupId)
                                                   groupModerationViewModel.revokeModeratorRole(
                                                       GroupUserActionRequest(groupId, member.userId)
                                                   )
                                               }
                                           }) {
                                               Icon(imageVector = if (member.role == 0) Icons.Default.Shield else Icons.Default.BrowserNotSupported, contentDescription = if (member.role == 0) stringResource(R.string.group_profile_grant_mod) else stringResource(R.string.group_profile_revoke_mod))
                                           }
                                       }
                                   //}
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMemberDialog = false }) { Text(stringResource(R.string.group_profile_close)) } },
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.group_profile_edit_group)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text(stringResource(R.string.group_profile_default_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedDes,
                        onValueChange = { editedDes = it },
                        label = { Text(stringResource(R.string.group_profile_description_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        group?.let {
                            groupChatViewModel.updateGroup(it.groupId, UpdateGroupRequest(editedName, editedDes))
                        }
                        showEditDialog = false
                    }
                ) {
                    Text(stringResource(R.string.group_profile_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(stringResource(R.string.group_profile_members))
                }
            }
        )
    }

    if (showAddUserDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddUserDialog = false
                newUserId = ""
            },
            title = { Text(stringResource(R.string.group_profile_add_user_title)) },
            text = {
                when {
                    friendsResult == null -> {
                        Text(stringResource(R.string.group_profile_loading_friends))
                    }
                    friendsResult?.isFailure == true -> {
                        Text(stringResource(R.string.group_profile_load_friends_error))
                    }
                    friendsResult?.isSuccess == true -> {
                        if (friendsNotInGroup.isEmpty()) {
                            Text(stringResource(R.string.group_profile_all_friends_in_group))
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .heightIn(max = 400.dp)
                            ) {
                                items(friendsNotInGroup) { friend ->
                                    val avatarUrl = friendAvatars
                                        .find { it.userId == friend.userId }
                                        ?.avatarUrl ?: ""

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                groupChatViewModel.addUserToGroup(
                                                    groupId,
                                                    friend.userId
                                                )
                                                showAddUserDialog = false
                                                newUserId = ""
                                            }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isLoadingAvatar.value) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color(0xFF53dba9),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            FriendAvatar(avatarUrl)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(friend.displayName, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showAddUserDialog = false
                    newUserId = ""
                }) {
                    Text(stringResource(R.string.group_profile_cancel))
                }
            }
        )
    }


    if (showRemoveUserDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveUserDialog = false },
            title = { Text(stringResource(R.string.group_profile_choose_user_remove)) },
            text = {
                Column {
                    members.forEach { member ->
                        if (currentUserId != member.userId) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        groupChatViewModel.removeUserFromGroup(
                                            groupId,
                                            member.userId
                                        )
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
                }
            },
            confirmButton = {
                TextButton(onClick = { showRemoveUserDialog = false }) {
                    Text(stringResource(R.string.group_profile_cancel))
                }
            }
        )
    }

    // Show Mute Dialog
    if (showMuteDialog) {
        AlertDialog(
            onDismissRequest = { showMuteDialog = false },
            title = { Text(stringResource(R.string.group_profile_mute_member)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(stringResource(R.string.group_profile_reason)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = muteTime.toString(),
                        onValueChange = { muteTime = it.toIntOrNull() ?: 0 },
                        label = { Text(stringResource(R.string.group_profile_mute_time)) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { handleMuteAction() }) {
                    Text(stringResource(R.string.group_profile_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showMuteDialog = false }) {
                    Text(stringResource(R.string.group_profile_cancel))
                }
            }
        )
    }

    // Show Ban Dialog
    if (showBanDialog) {
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            title = { Text(stringResource(R.string.group_profile_ban_member)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(stringResource(R.string.group_profile_reason)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { handleBanAction() }) {
                    Text(stringResource(R.string.group_profile_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) {
                    Text(stringResource(R.string.group_profile_cancel))
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
            Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.group_profile_copy), tint = MainColor, modifier = Modifier.size(16.dp))
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

@Composable
fun getRoleLabel(role: Int): String {
    return when (role) {
        0 -> stringResource(R.string.group_profile_role_member)
        1 -> stringResource(R.string.group_profile_role_collaborator)
        2 -> stringResource(R.string.group_profile_role_admin)
        else -> stringResource(R.string.group_profile_role_unknown)
    }
}

fun NavController.navigateToGroupChatScreenPopUp() {
    this.navigate(Routes.GroupChat) {
        popUpTo(Routes.GroupChat) { inclusive = true }
    }
}
