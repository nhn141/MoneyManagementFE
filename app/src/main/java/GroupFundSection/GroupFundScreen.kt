import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.GroupFundDto
import DI.Models.UiEvent.UiEvent
import DI.ViewModels.GroupFundViewModel
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Balance: ${fund.balance}", fontWeight = FontWeight.Bold)
            Text("Total In: ${fund.totalFundsIn}")
            Text("Total Out: ${fund.totalFundsOut}")
        }
    }
}

@Composable
fun GroupFundScreen(
    navController: NavController,
    groupFundViewModel: GroupFundViewModel,
    groupId: String
) {
    val context = LocalContext.current
    val groupFunds by groupFundViewModel.groupFunds.collectAsState()
    val addEvent = groupFundViewModel.addGroupFundEvent.collectAsState(initial = null)
    val updateEvent = groupFundViewModel.updateGroupFundEvent.collectAsState(initial = null)
    val deleteEvent = groupFundViewModel.deleteGroupFundEvent.collectAsState(initial = null)

    var selectedFund by remember { mutableStateOf<GroupFundDto?>(null) }

    LaunchedEffect(Unit) {
        groupFundViewModel.fetchGroupFunds(groupId)
    }

    LaunchedEffect(addEvent.value, updateEvent.value, deleteEvent.value) {
        val message = listOfNotNull(addEvent.value, updateEvent.value, deleteEvent.value)
            .firstOrNull()?.let { if (it is UiEvent.ShowMessage) it.message else null }
        message?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    val funds = groupFunds?.getOrNull() ?: emptyList()

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
                onClick = {
                    val newDto = CreateGroupFundDto(groupID = groupId, description = "New Fund")
                    groupFundViewModel.createGroupFund(newDto)
                },
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