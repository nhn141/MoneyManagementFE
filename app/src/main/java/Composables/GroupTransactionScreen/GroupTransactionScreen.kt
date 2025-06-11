package DI.Composables.GroupTransactionScreen

import DI.Models.GroupTransaction.CreateGroupTransactionDto
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.GroupTransaction.UpdateGroupTransactionDto
import DI.Models.UiEvent.UiEvent
import DI.ViewModels.GroupTransactionViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun GroupTransactionScreen(
    navController: NavController,
    viewModel: GroupTransactionViewModel,
    groupFundId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val groupTransactions by viewModel.groupTransactions.collectAsState()
    val transactions = groupTransactions?.getOrNull() ?: emptyList()

    var selectedTransaction by remember { mutableStateOf<GroupTransactionDto?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Fetch when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchGroupTransactions(groupFundId)
    }

    // UI feedback handlers
    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.addGroupTransactionEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    viewModel.fetchGroupTransactions(groupFundId)
                }
            }
        }
        scope.launch {
            viewModel.updateGroupTransactionEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    viewModel.fetchGroupTransactions(groupFundId)
                }
            }
        }
        scope.launch {
            viewModel.deleteGroupTransactionEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    viewModel.fetchGroupTransactions(groupFundId)
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
                                    text = "Total: ${transactions.size}",
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
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
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
                items(transactions) { transaction ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTransaction = transaction },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Wallet: ${transaction.userWalletID}")
                            Text("Category: ${transaction.userCategoryID}")
                            Text("Amount: ${transaction.amount}")
                            Text("Description: ${transaction.description}")
                            Text("Created: ${transaction.transactionDate}")
                            Text("Type: ${transaction.type}")
                        }
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddGroupTransactionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { wallet, category, amount, desc, type ->
                viewModel.createGroupTransaction(
                    CreateGroupTransactionDto(groupFundId, wallet, category, amount, desc, LocalTime.now().toString(), type)
                )
                showAddDialog = false
            }
        )
    }

    // Edit Dialog
    selectedTransaction?.let { transaction ->
        EditGroupTransactionDialog(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onUpdate = { wallet, category, amount, desc, type ->
                viewModel.updateGroupTransaction(
                    transaction.groupTransactionID,
                    UpdateGroupTransactionDto(transaction.groupTransactionID, wallet, category, amount, desc, transaction.transactionDate, type),
                    groupFundId
                )
                selectedTransaction = null
            },
            onDelete = {
                viewModel.deleteGroupTransaction(transaction.groupTransactionID, transaction.groupFundID)
                selectedTransaction = null
            }
        )
    }
}
