package DI.Composables.TransactionSection

import DI.Composables.CategorySection.getCategoryIcon
import DI.Models.Friend.Friend
import DI.Models.Group.Group
import DI.Models.UiEvent.UiEvent
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.TransactionAction
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch

@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyViewModel: CurrencyConverterViewModel,
    chatViewModel: ChatViewModel,
    groupChatViewModel: GroupChatViewModel,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    val selectedTransaction by viewModel.selectedTransaction
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    val context = LocalContext.current
    var isLoaded by remember { mutableStateOf(false) }
    val isVND by currencyViewModel.isVND.collectAsState() // Lấy trạng thái isVND
    val exchangeRate by currencyViewModel.exchangeRate.collectAsState() // Lấy tỷ giá

    LaunchedEffect(transactionId) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
        viewModel.loadTransactionById(transactionId) {
            isLoaded = it
        }
    }

    LaunchedEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA5DABC),
                        Color(0xFF87EAAF),
                        Color(0xFF00BE7C).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        TransactionDetailHeader(
            navController = navController,
            onEditClick = {
                if (isLoaded && selectedTransaction != null) {
                    navController.navigate("transaction_edit/${selectedTransaction!!.transactionID}")
                }
            }
        )

        if (isLoaded && selectedTransaction != null && categories != null && wallets != null) {
            val generalTransaction = selectedTransaction!!.toGeneralTransactionItem()

            val categoryList = categories?.getOrNull() ?: emptyList()
            val walletList = wallets?.getOrNull() ?: emptyList()

            val category = categoryList.find { it.categoryID == generalTransaction.categoryID }
            val wallet = walletList.find { it.walletID == generalTransaction.walletID }
            TransactionDetailBody(
                navController = navController,
                title = generalTransaction.title,
                categoryName = category?.name ?: stringResource(R.string.unknown),
                amount = generalTransaction.amount,
                walletName = wallet?.walletName ?: stringResource(R.string.unknown),
                date = generalTransaction.timestamp ?: stringResource(R.string.unknown),
                type = if (generalTransaction.isIncome) stringResource(R.string.income) else stringResource(
                    R.string.expense
                ),
                transactionId = transactionId,
                viewModel = viewModel,
                context = context,
                isVND = isVND, // Truyền isVND
                exchangeRate = exchangeRate,
                chatViewModel = chatViewModel, // Truyền chatViewModel
                groupChatViewModel = groupChatViewModel, // Truyền groupChatViewModel
                profileViewModel = profileViewModel, // Truyền profileViewModel,
                friends = friendsWithAvatars // Truyền danh sách bạn bè đã có avatar
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun TransactionDetailHeader(
    navController: NavController,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color(0xFF667eea).copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.transaction_detail),
                    color = Color(0xFF2D3748),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color(0xFF48BB78).copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = Color(0xFF48BB78),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionDetailBody(
    navController: NavController,
    title: String,
    categoryName: String,
    amount: String,
    walletName: String,
    date: String,
    type: String,
    transactionId: String,
    viewModel: TransactionViewModel,
    chatViewModel: ChatViewModel,
    groupChatViewModel: GroupChatViewModel,
    profileViewModel: ProfileViewModel,
    context: Context,
    isVND: Boolean,
    exchangeRate: Double?,
    friends: List<Friend>,
) {
    val typeColor = if (type.equals("Income", ignoreCase = true))
        Color(0xFF48BB78) else Color(0xFFE53E3E)
    val typeIcon = if (type.equals("Income", ignoreCase = true))
        Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

    val coroutineScope = rememberCoroutineScope()

    val currentUserId = profileViewModel.profile.value?.getOrNull()?.id ?: ""

    val formattedAmount =
        CurrencyUtils.formatAmount(amount.toDoubleOrNull() ?: 0.0, isVND, exchangeRate)

    val shareMessage = """
        ${stringResource(R.string.shared_transaction)}
        ${stringResource(R.string.title_label)} $title
        ${stringResource(R.string.type_label)} $type
        ${stringResource(R.string.amount_label1)} $formattedAmount
        ${stringResource(R.string.date_label)} $date
        ${stringResource(R.string.category_label)} $categoryName
        ${stringResource(R.string.wallet_label)} $walletName
        [transaction:$transactionId]
    """.trimIndent()

    var showShareDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val latestChats by chatViewModel.latestChats.collectAsState(initial = null)
    val groups by groupChatViewModel.groups.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        groupChatViewModel.loadUserGroups()
        chatViewModel.getLatestChats()
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel._transactionEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Transaction Type Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    typeColor.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = typeIcon,
                                contentDescription = type,
                                tint = typeColor,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = type.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = typeColor,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = CurrencyUtils.formatAmount(
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                isVND = isVND,
                                exchangeRate = exchangeRate
                            ),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2D3748),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            item {
                // Transaction Details Card with Share Button
                Box {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.transaction_details),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            DetailItem(
                                icon = Icons.Default.Title,
                                label = stringResource(R.string.title),
                                value = title,
                                iconTint = Color(0xFF667EEA)
                            )

                            DetailItem(
                                icon = getCategoryIcon(categoryName),
                                label = stringResource(R.string.category),
                                value = categoryName,
                                iconTint = Color(0xFF9F7AEA)
                            )

                            DetailItem(
                                icon = Icons.Default.AccountBalanceWallet,
                                label = stringResource(R.string.wallet),
                                value = walletName,
                                iconTint = Color(0xFF38B2AC)
                            )

                            DetailItem(
                                icon = Icons.Default.DateRange,
                                label = stringResource(R.string.date),
                                value = date,
                                iconTint = Color(0xFFED8936)
                            )
                        }
                    }

                    // Share Button
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(17.dp)
                            .background(Color(0xFF667EEA), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Delete FAB
        FloatingActionButton(
            onClick = { showDeleteDialog = true },
            containerColor = Color(0xFFE53E3E),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(64.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 12.dp,
                pressedElevation = 16.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.size(28.dp)
            )
        }

        // Delete Dialog
        if (showDeleteDialog) {
            DeleteDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    coroutineScope.launch {
                        try {
                            viewModel.deleteTransaction(transactionId) { }
                            viewModel.notifyTransactionAction(
                                TransactionAction.DELETE,
                                success = true
                            )
                            navController.popBackStack()
                        } catch (e: Exception) {
                            viewModel.notifyTransactionAction(
                                TransactionAction.DELETE,
                                success = false
                            )
                        }
                        showDeleteDialog = false
                    }
                }
            )
        }

        // Share Dialog
        if (showShareDialog) {
            ShareDialog(
                onDismiss = { showShareDialog = false },
                friends = friends,
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
                },
                transactionId = transactionId
            )
        }
    }
}

@Composable
fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    iconTint.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF718096),
                letterSpacing = 0.25.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748),
                lineHeight = 20.sp
            )
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
    transactionId: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.share_dialog_title)) },
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
                        Log.d("ShareDialog", "GroupAva: ${group.imageUrl}")
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

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFFE53E3E).copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE53E3E),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.delete_transaction_confirm_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2D3748),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = stringResource(R.string.delete_transaction_confirm_message),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color(0xFF4A5568),
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53E3E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.delete),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4A5568)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        dismissButton = null,
        modifier = Modifier.padding(16.dp)
    )
}

