package DI.Composables.FriendSection

import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.Models.Friend.AddFriendRequest
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendProfileScreen(
    friendId: String,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel,
    navController: NavController
) {

    LaunchedEffect(Unit) {
        profileViewModel.getFriendProfile(friendId)
    }

    val friendProfile = profileViewModel.friendProfile.collectAsState().value?.getOrNull()
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    val friends = friendViewModel.friends.collectAsState().value?.getOrNull()
    val isOnline = friends?.find { it.userId == friendId }?.isOnline == true

    val isFriend = friends?.find { it.userId == friendId } != null

    Log.d("FriendProfileScreen", "Friend ID: $friendId, isFriend: $isFriend")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = 10.dp, y = 10.dp)
            )
        }

        // Main content
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Profile header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile image with online indicator
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        // Avatar with online dot
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (isLoadingAvatar.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MainColor,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                FriendAvatar(friendProfile?.avatarUrl ?: "")
                            }

                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display name
                    Text(
                        text = friendProfile?.displayName ?: stringResource(R.string.no_full_name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Username
                    Text(
                        text = friendProfile?.userName ?: stringResource(R.string.no_email),
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Online status text
                    Text(
                        text = if (isOnline) stringResource(R.string.online) else stringResource(R.string.offline),
                        fontSize = 14.sp,
                        color = if (isOnline) Color(0xFF4CAF50) else Color.Gray,
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Default.ChatBubble,
                    label = stringResource(R.string.message),
                    onClick = { navController.navigate("chat_message/$friendId") }
                )

                ActionButton(
                    icon = if (isFriend) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
                    label = if (isFriend) stringResource(R.string.unfriend) else stringResource(R.string.add_friend),
                    onClick = {
                        if (isFriend)
                            friendViewModel.deleteFriend(friendId)
                        else
                            friendViewModel.addFriend(AddFriendRequest(friendId))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile details card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_info),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoItem(
                        icon = Icons.Default.Person,
                        label = stringResource(R.string.full_name),
                        value = "${friendProfile?.firstName ?: ""} ${friendProfile?.lastName ?: ""}"
                    )

                    ProfileInfoItem(
                        icon = Icons.Default.Email,
                        label = stringResource(R.string.email),
                        value = friendProfile?.email ?: ""
                    )

                    ProfileInfoItem(
                        icon = Icons.Default.Tag,
                        label = stringResource(R.string.user_id),
                        value = friendProfile?.id ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF53dba9),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedText = stringResource(R.string.id_copied)

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
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = value,
                fontSize = 16.sp,
                color = Color(0xFF333333),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (label == stringResource(R.string.user_id)) {
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(value))
                    Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy ID",
                    tint = MainColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
