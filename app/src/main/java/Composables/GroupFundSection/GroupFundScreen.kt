import DI.Composables.GroupFundSection.AddGroupFundDialog
import DI.Composables.GroupFundSection.EditGroupFundDialog
import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.GroupFundDto
import DI.Models.GroupFund.UpdateGroupFundDto
import DI.Models.UiEvent.UiEvent
import DI.Utils.CurrencyUtils
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.GroupFundViewModel
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun GroupFundScreen(
    navController: NavController,
    groupFundViewModel: GroupFundViewModel,
    groupId: String,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    Log.d("GroupFundScreen", "Composable started")

    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRate by currencyConverterViewModel.exchangeRate.collectAsState()

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
                }
            }
        }
        scope.launch {
            groupFundViewModel.updateGroupFundEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        scope.launch {
            groupFundViewModel.deleteGroupFundEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
                    ) {
                        Icon(
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
                                        text = "${stringResource(id = R.string.description_label)}: ${fund.description}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF0D1F2D),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val savingGoalAmount = fund.savingGoal
                                    Text(
                                        text = "${stringResource(id = R.string.saving_goal_label)}: ${
                                            CurrencyUtils.formatAmount(
                                                amount = savingGoalAmount,
                                                isVND = isVND,
                                                exchangeRate = exchangeRate
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${stringResource(id = R.string.balance_label)}: ${
                                            CurrencyUtils.formatAmount(
                                                amount = fund.balance,
                                                isVND = isVND,
                                                exchangeRate = exchangeRate
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${stringResource(id = R.string.created_at_label)}: ${
                                            formatDateTime(
                                                fund.createdAt
                                            )
                                        }",
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
            },
            isVND = isVND
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
                        text = stringResource(R.string.group_fund_details),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    val savingGoal = selectedFund?.savingGoal ?: 0.0

                    // Detailed Info
                    InfoText(
                        label = stringResource(id = R.string.description_label),
                        value = selectedFund!!.description
                    )
                    InfoText(
                        label = stringResource(id = R.string.saving_goal_label),
                        value = CurrencyUtils.formatAmount(
                            amount = savingGoal,
                            isVND = isVND,
                            exchangeRate = exchangeRate
                        )
                    )
                    InfoText(
                        label = stringResource(id = R.string.income_label),
                        value = CurrencyUtils.formatAmount(
                            amount = selectedFund!!.totalFundsIn,
                            isVND = isVND,
                            exchangeRate = exchangeRate
                        )
                    )
                    InfoText(
                        label = stringResource(id = R.string.expense_label),
                        value = CurrencyUtils.formatAmount(
                            amount = selectedFund!!.totalFundsOut,
                            isVND = isVND,
                            exchangeRate = exchangeRate
                        )
                    )
                    InfoText(
                        label = stringResource(id = R.string.balance_label),
                        value = CurrencyUtils.formatAmount(
                            amount = selectedFund!!.balance,
                            isVND = isVND,
                            exchangeRate = exchangeRate
                        )
                    )
                    InfoText(
                        label = stringResource(id = R.string.created_at_label),
                        value = formatDateTime(selectedFund!!.createdAt),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button
                    Column {
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
                                Text(stringResource(R.string.edit), color = Color.White)
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
                                Text(stringResource(R.string.delete), color = Color.White)
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
                                Text(
                                    stringResource(id = R.string.view_group_transaction),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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
            onDelete = {},
            isVND = isVND,
            exchangeRate = exchangeRate
        )
    }

    if (showDeleteDialog && selectedFund != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.delete_fund)) },
            text = { Text(stringResource(id = R.string.are_you_sure_you_want_delete_this_group_fund)) },
            confirmButton = {
                TextButton(onClick = {
                    groupFundViewModel.deleteGroupFund(
                        selectedFund!!.groupFundID,
                        selectedFund!!.groupID
                    )
                    showDeleteDialog = false
                    selectedFund = null
                }) {
                    Text(stringResource(R.string.delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
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

@Composable
fun formatDateTime(input: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(input, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        return stringResource(R.string.invalid_date)
    }
}