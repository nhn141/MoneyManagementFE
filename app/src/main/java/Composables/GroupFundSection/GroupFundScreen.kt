import DI.Composables.CategorySection.ModernColors
import DI.Composables.GroupFundSection.AddGroupFundDialog
import DI.Composables.GroupFundSection.EditGroupFundDialog
import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.GroupFundDto
import DI.Models.GroupFund.UpdateGroupFundDto
import DI.Models.UiEvent.UiEvent
import DI.ViewModels.GroupFundViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch

@Composable
fun GroupFundCard(
    fund: GroupFundDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(ModernColors.cardGradient)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Description: ${fund.description}", fontWeight = FontWeight.Bold, color = ModernColors.OnSurface)
            Text("Saving Goal: ${fund.savingGoal}", color = ModernColors.OnSurfaceVariant)
            Text("Balance: ${fund.balance}", color = ModernColors.OnSurfaceVariant)
        }
    }
}

@Composable
fun GroupFundScreen(
    navController: NavController,
    groupFundViewModel: GroupFundViewModel,
    groupId: String
) {
    Log.d("GroupFundScreen", "Composable started")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val groupFunds by groupFundViewModel.groupFunds.collectAsState()
    val funds = groupFunds?.getOrNull() ?: emptyList()

    var selectedFund by remember { mutableStateOf<GroupFundDto?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Log.d("GroupID", "Fetching group funds for: $groupId")
    // Fetch data once when screen opens
    LaunchedEffect(Unit) {
        Log.d("GroupFundScreen", "Calling fetchGroupFunds")
        groupFundViewModel.fetchGroupFunds(groupId)
        Log.d("GroupFundScreen", "Fetching funds for: $groupId")
        Log.d("GroupFundScreen", "Fetched funds: ${funds.size}")
    }

    // Collect and react to add/update/delete events
    LaunchedEffect(Unit) {
        scope.launch {
            groupFundViewModel.addGroupFundEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    groupFundViewModel.fetchGroupFunds(groupId)
                }
            }
        }
        scope.launch {
            groupFundViewModel.updateGroupFundEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    groupFundViewModel.fetchGroupFunds(groupId)
                }
            }
        }
        scope.launch {
            groupFundViewModel.deleteGroupFundEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    groupFundViewModel.fetchGroupFunds(groupId)
                }
            }
        }
    }


//    Scaffold(
//        containerColor = Color.Transparent,
//        topBar = {
//            Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFF00D09E))
//                ) {
//                    Column(
//                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Box(
//                                modifier = Modifier
//                                    .size(48.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.White.copy(alpha = 0.2f)),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Money,
//                                    contentDescription = "Fund",
//                                    tint = Color.White,
//                                    modifier = Modifier.size(28.dp)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(16.dp))
//
//                            Column {
//                                Text(
//                                    text = "Group Funds",
//                                    fontSize = 24.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.White
//                                )
//                                Text(
//                                    text = "Total: ${funds.size}",
//                                    fontSize = 16.sp,
//                                    color = Color.White.copy(alpha = 0.9f),
//                                    fontWeight = FontWeight.Medium
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { showAddDialog = true },
//                containerColor = Color(0xFF00D09E),
//                contentColor = Color.White,
//                shape = CircleShape,
//                elevation = FloatingActionButtonDefaults.elevation(12.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add Fund",
//                    modifier = Modifier.size(28.dp)
//                )
//            }
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Brush.verticalGradient(colors = listOf(Color(0xFF00D09E), Color(0xFFF8FFFE))))
//        ) {
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                modifier = Modifier.fillMaxSize(),
//                contentPadding = PaddingValues(
//                    start = 20.dp,
//                    end = 20.dp,
//                    top = paddingValues.calculateTopPadding() + 16.dp,
//                    bottom = paddingValues.calculateBottomPadding() + 16.dp
//                ),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(funds) { fund ->
//                    GroupFundCard(
//                        fund = fund,
//                        onClick = { selectedFund = fund }
//                    )
//                }
//            }
//        }
//
//        selectedFund?.let { fund ->
//            EditGroupFundDialog(
//                fund = fund,
//                onDismiss = { selectedFund = null },
//                onUpdate = { newDescription, newSavingGoal ->
//                    groupFundViewModel.updateGroupFund(
//                        fund.groupFundID, UpdateGroupFundDto(
//                            groupFundID = fund.groupFundID,
//                            description = newDescription,
//                            savingGoal = newSavingGoal
//                        ), groupId
//                    )
//                    selectedFund = null
//                },
//                onDelete = {
//                    groupFundViewModel.deleteGroupFund(
//                        fund.groupFundID,
//                        fund.groupID
//                    )
//                    selectedFund = null
//                }
//            )
//        }
//
//        if (showAddDialog) {
//            AddGroupFundDialog(
//                onDismiss = { showAddDialog = false },
//                onSave = { description, savingGoal ->
//                    groupFundViewModel.createGroupFund(
//                        CreateGroupFundDto(
//                            groupID = groupId,
//                            description = description,
//                            //savingGoal = savingGoal
//                        )
//                    )
//                    showAddDialog = false
//                }
//            )
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00D09E),
                        Color(0xFFF8FFFE)
                    )
                )
            )
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top Bar
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {                        Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    }

                    Text(
                        text = stringResource(R.string.group_fund),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Color.White,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_notifications),
                            contentDescription = stringResource(R.string.notifications),
                            tint = Color(0xFF00D09E),
                            modifier = Modifier.size(22.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFFF6B6B), CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    ActionButton(
                        iconRes = R.drawable.ic_more,
                        contentDescription = stringResource(R.string.add_group_fund),
                        onClick = { showAddDialog = true },
                        isPrimary = true
                    )
                }
            }

            // Transactions List Header
            item {
                Text(
                    text = stringResource(R.string.recent_group_funds),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0D1F2D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            // Group Fund List
            if (funds.isNotEmpty()) {
                itemsIndexed(funds) { _, fund ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .clickable {
                                selectedFund = fund
                                showDetailDialog = true
                                Log.d("GroupFundRow", "ID: ${fund.groupFundID}")
                            }
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = "Description: ${fund.description}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF0D1F2D),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Saving Goal: ${fund.savingGoal}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Balance: ${fund.balance.toString()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Create At: ${fund.createdAt}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    EmptyGroupFundState()
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddGroupFundDialog(
                onDismiss = { showAddDialog = false },
                onSave = { description, savingGoal ->
                    groupFundViewModel.createGroupFund(
                        CreateGroupFundDto(
                            groupID = groupId,
                            description = description,
                            savingGoal = savingGoal
                        )
                    )
            showAddDialog = false
            }
        )
    }

    if (showDetailDialog && selectedFund != null) {
        Dialog(onDismissRequest = { showDetailDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)) // nền dịu nhẹ
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tittle
                    Text(
                        text = "Group Fund Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Detailed Info
                    InfoText(label = "Description", value = selectedFund!!.description)
                    InfoText(label = "Saving Goal", value = "${selectedFund!!.savingGoal}")
                    InfoText(label = "Income", value = "${selectedFund!!.totalFundsIn}")
                    InfoText(label = "Expense", value = "${selectedFund!!.totalFundsOut}")
                    InfoText(label = "Balance", value = "${selectedFund!!.balance}")
                    InfoText(
                        label = "Created At",
                        value = selectedFund!!.createdAt,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button
                    Column (

                    )
                    {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    showEditDialog = true
                                    showDetailDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4CAF50
                                    )
                                )
                            ) {
                                Text("Edit", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    showDeleteDialog = true
                                    showDetailDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFE53935
                                    )
                                )
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    showDetailDialog = false
                                    navController.navigate("group_transaction/${selectedFund!!.groupFundID}")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4CAF50
                                    )
                                )
                            ) {
                                Text("View Group Transaction", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

//    // Edit Dialog
//    selectedFund?.let { fund ->
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp),
//            shape = RoundedCornerShape(16.dp),
//            elevation = CardDefaults.cardElevation(8.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text("Description: ${fund.description}", fontWeight = FontWeight.Bold)
//                Text("Saving Goal: ${fund.savingGoal}")
//                Text("Balance: ${fund.balance}")
//                Text("Created At: ${fund.createdAt}")
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                    Button(
//                        onClick = { showEditDialog = true }
//                    ) {
//                        Text("Edit")
//                    }
//
//                    Button(
//                        onClick = {
//                            showDeleteDialog = true
//                        },
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                    ) {
//                        Text("Delete", color = Color.White)
//                    }
//
//                    Spacer(modifier = Modifier.weight(1f))
//
//                    TextButton(onClick = { selectedFund = null }) {
//                        Text("Close")
//                    }
//                }
//            }
//        }
//    }

    if (showEditDialog && selectedFund != null) {
        EditGroupFundDialog(
            fund = selectedFund!!,
            onDismiss = {
                showEditDialog = false
                selectedFund = null
            },
            onUpdate = { newDescription, newGoal ->
                groupFundViewModel.updateGroupFund(
                    selectedFund!!.groupFundID,
                    UpdateGroupFundDto(
                        groupFundID = selectedFund!!.groupFundID,
                        description = newDescription,
                        savingGoal = newGoal
                    ),
                    selectedFund!!.groupID
                )
                showEditDialog = false
                selectedFund = null
            },
            onDelete = {} // không xài ở đây
        )
    }

    if (showDeleteDialog && selectedFund != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Fund") },
            text = { Text("Are you sure you want to delete this group fund?") },
            confirmButton = {
                TextButton(onClick = {
                    groupFundViewModel.deleteGroupFund(
                        selectedFund!!.groupFundID,
                        selectedFund!!.groupID
                    )
                    showDeleteDialog = false
                    selectedFund = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ActionButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                if (isPrimary) {
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D09E),
                            Color(0xFF00B888)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8F8F8)
                        )
                    )
                },
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = if (isPrimary) Color.White else Color(0xFF666666),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmptyGroupFundState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_group_funds_found),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoText(label: String, value: String, maxLines: Int = Int.MAX_VALUE) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(120.dp),
            color = Color(0xFF424242)
        )
        Text(
            text = value,
            color = Color(0xFF212121),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}