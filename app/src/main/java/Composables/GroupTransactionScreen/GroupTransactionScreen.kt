package DI.Composables.GroupTransactionScreen

import DI.Composables.TransactionSection.TransactionIconButton
import DI.Models.Category.Category
import DI.Models.GroupTransaction.CreateGroupTransactionDto
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.GroupTransaction.UpdateGroupTransactionDto
import DI.Models.UiEvent.UiEvent
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.GroupTransactionViewModel
import DI.ViewModels.WalletViewModel
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch

@Composable
fun GroupTransactionScreen(
    navController: NavController,
    viewModel: GroupTransactionViewModel,
    walletViewModel: WalletViewModel,
    categoryViewModel: CategoryViewModel,
    currencyViewModel: CurrencyConverterViewModel,
    groupFundId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val groupTransactions by viewModel.groupTransactions.collectAsState()
    val transactions = groupTransactions?.getOrNull() ?: emptyList()

    var category by remember { mutableStateOf<Category?>(null) }
    val isVND by currencyViewModel.isVND.collectAsState() // Lấy trạng thái isVND
    val exchangeRate by currencyViewModel.exchangeRate.collectAsState() // Lấy tỷ giá

    var selectedTransaction by remember { mutableStateOf<GroupTransactionDto?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Fetch when screen opens
    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        viewModel.fetchGroupTransactions(groupFundId)
    }

    // UI feedback handlers
    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.addGroupTransactionEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        scope.launch {
            viewModel.updateGroupTransactionEvent.collect { event ->
                if (event is UiEvent.ShowMessage) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        scope.launch {
            viewModel.deleteGroupTransactionEvent.collect { event ->
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
                        text = stringResource(R.string.group_transaction),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    ActionButton(
                        iconRes = R.drawable.ic_more,
                        contentDescription = stringResource(R.string.add_transaction),
                        onClick = { showAddDialog = true },
                        isPrimary = true
                    )
                }
            }


            // Transactions List Header
            item {
                Text(
                    text = stringResource(R.string.recent_transactions),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0D1F2D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            // Transactions List
            if (transactions.isNotEmpty()) {
                itemsIndexed(transactions) { _, transaction ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .clickable {
                                selectedTransaction = transaction
                                Log.d(
                                    "GroupTransactionRow",
                                    "ID: ${transaction.groupTransactionID}"
                                )
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
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(
                                            if (transaction.toGeneralGroupTransactionItem().isIncome)
                                                Color(0xFF4CAF50).copy(alpha = 0.15f)
                                            else Color(0xFFFF5722).copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TransactionIconButton(
                                        categoryName = category?.name
                                            ?: stringResource(R.string.unknown_category),
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = transaction.description,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF0D1F2D),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = transaction.transactionDate,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }

                            Text(
                                text = CurrencyUtils.formatAmount(
                                    amount = transaction.amount,
                                    isVND = isVND,
                                    exchangeRate = exchangeRate
                                ),
                                color = if (transaction.toGeneralGroupTransactionItem().isIncome) Color(
                                    0xFF4CAF50
                                ) else Color(0xFFFF5722),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.End)
                            )
                        }
                    }
                }
            } else {
                item {
                    EmptyGroupTransactionState()
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddGroupTransactionDialog(
            walletList = walletViewModel.wallets.value?.getOrNull() ?: emptyList(),
            categoryList = categoryViewModel.categories.value?.getOrNull() ?: emptyList(),
            onDismiss = { showAddDialog = false },
            onSave = { wallet, userCategory, amount, desc, date, type ->
                viewModel.createGroupTransaction(
                    CreateGroupTransactionDto(
                        groupFundId,
                        wallet,
                        userCategory,
                        amount,
                        desc,
                        date,
                        type
                    )
                )
                showAddDialog = false
            },
            isVND = isVND,
            exchangeRate = exchangeRate
        )
    }

    // Edit Dialog
    selectedTransaction?.let { transaction ->
        EditGroupTransactionDialog(
            walletList = walletViewModel.wallets.value?.getOrNull() ?: emptyList(),
            categoryList = categoryViewModel.categories.value?.getOrNull() ?: emptyList(),
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onUpdate = { wallet, category, amount, desc, date, type ->
                viewModel.updateGroupTransaction(
                    transaction.groupTransactionID,
                    UpdateGroupTransactionDto(
                        transaction.groupTransactionID,
                        wallet,
                        category,
                        amount,
                        desc,
                        date,
                        type
                    ),
                    groupFundId
                )
                selectedTransaction = null
            },
            onDelete = {
                viewModel.deleteGroupTransaction(
                    transaction.groupTransactionID,
                    transaction.groupFundID
                )
                selectedTransaction = null
            },
            isVND = isVND,
            exchangeRate = exchangeRate
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
private fun EmptyGroupTransactionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_transactions_found),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

data class GeneralGroupTransactionItem(
    val groupTransactionID: String,
    val groupFundID: String,
    val userWalletID: String,
    val userCategoryID: String,
    val amount: String,
    val description: String,
    val transactionDate: String?,
    val isIncome: Boolean
)

fun GroupTransactionDto.toGeneralGroupTransactionItem(): GeneralGroupTransactionItem {
    val isIncome = type.lowercase() == "income"

    return GeneralGroupTransactionItem(
        groupTransactionID = groupTransactionID,
        groupFundID = groupFundID,
        userWalletID = userWalletID,
        userCategoryID = userCategoryID,
        amount = if (isIncome) "$amount" else "-$amount",
        description = description,
        transactionDate = transactionDate,
        isIncome = isIncome
    )
}
