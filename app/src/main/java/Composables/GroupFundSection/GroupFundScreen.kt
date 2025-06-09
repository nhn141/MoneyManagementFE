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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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

    val groupFunds by groupFundViewModel.groupFunds.collectAsState()
    val funds = groupFunds?.getOrNull() ?: emptyList()

    val addEvent = groupFundViewModel.addGroupFundEvent.collectAsState(initial = null)
    val updateEvent = groupFundViewModel.updateGroupFundEvent.collectAsState(initial = null)
    val deleteEvent = groupFundViewModel.deleteGroupFundEvent.collectAsState(initial = null)

    var selectedFund by remember { mutableStateOf<GroupFundDto?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

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


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF00D09E))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Money,
                                    contentDescription = "Fund",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Group Funds",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Total: ${funds.size}",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF00D09E),
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Fund",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF00D09E), Color(0xFFF8FFFE))))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(funds) { fund ->
                    GroupFundCard(
                        fund = fund,
                        onClick = { selectedFund = fund }
                    )
                }
            }
        }

        selectedFund?.let { fund ->
            EditGroupFundDialog(
                fund = fund,
                onDismiss = { selectedFund = null },
                onUpdate = { newDescription, newSavingGoal ->
                    groupFundViewModel.updateGroupFund(
                        fund.groupFundID, UpdateGroupFundDto(
                            groupFundID = fund.groupFundID,
                            description = newDescription,
                            savingGoal = newSavingGoal
                        )
                    )
                    selectedFund = null
                },
                onDelete = {
                    groupFundViewModel.deleteGroupFund(
                        fund.groupFundID,
                        fund.groupID
                    )
                    selectedFund = null
                }
            )
        }

        if (showAddDialog) {
            AddGroupFundDialog(
                onDismiss = { showAddDialog = false },
                onSave = { description, savingGoal ->
                    groupFundViewModel.createGroupFund(
                        CreateGroupFundDto(
                            groupID = groupId,
                            description = description,
                            //savingGoal = savingGoal
                        )
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewGroupFundScreen() {
//    GroupFundScreen(
//        navController = rememberNavController(),
//        groupFundViewModel = FakeGroupFundViewModel(),
//        groupId = "12345678-1234-1234-1234-123456789012"
//    )
//}