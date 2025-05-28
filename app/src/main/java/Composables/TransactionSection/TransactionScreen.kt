package DI.Composables.TransactionSection

import DI.Models.Category.Transaction
import DI.Models.Transaction.TransactionSearchRequest
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TransactionScreen(
    navController: NavController,
    viewModel: TransactionScreenViewModel,
    categoryViewModel: CategoryViewModel
) {
    val listState = rememberLazyListState()
    val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    Column(
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
        TransactionHeaderSection(
            navController = navController,
            viewModel = viewModel,
            scrollOffset = scrollOffset.value
        )

        TransactionBodySection(
            navController = navController,
            viewModel = viewModel,
            categoryViewModel = categoryViewModel,
            listState = listState
        )
    }
}
data class GeneralTransactionItem(
    val transactionID: String,
    val categoryID: String,
    val walletID: String,
    val title: String,
    val timestamp: String?,
    val amount: String,
    val isIncome: Boolean
)


fun Transaction.toGeneralTransactionItem(): GeneralTransactionItem {
    val isIncome = type.lowercase() == "income"

    return GeneralTransactionItem(
        transactionID = transactionID,
        categoryID = categoryID,
        walletID = walletID,
        title = description,
        timestamp = transactionDate,
        amount = if (isIncome) "$$amount" else "-$$amount",
        isIncome = isIncome
    )
}

@Composable
fun TransactionHeaderSection(
    navController: NavController,
    viewModel: TransactionScreenViewModel,
    scrollOffset: Int
) {
    val selected = viewModel.selectedType.value
    val baseHeight = 320.dp
    val collapsedHeight = 100.dp
    val animatedHeight by animateDpAsState(
        targetValue = if (scrollOffset > 20) collapsedHeight else baseHeight,
        label = "HeaderHeightAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00D09E),
                            Color(0xFF00B888)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box {
                    Text(
                        text = "Transaction",
                        color = Color.Black.copy(alpha = 0.1f),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                    )
                    Text(
                        text = "Transaction",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Notification button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFF8F8F8)
                                )
                            ),
                            CircleShape
                        )
                        .clickable { /* Handle Notifications */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = "Notifications",
                        tint = Color(0xFF00D09E),
                        modifier = Modifier.size(22.dp)
                    )

                    // Notification badge
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF6B6B), CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Total Balance Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFF8FFFE)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Balance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$7,783.00",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D1F2D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income + Expense Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected == "Income")
                            Color(0xFF4CAF50) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (selected == "Income") 16.dp else 8.dp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable { viewModel.onTypeSelected("Income") }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (selected == "Income") {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF4CAF50),
                                            Color(0xFF388E3C)
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color(0xFFF8FFF8)
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (selected == "Income")
                                            Color.White.copy(alpha = 0.2f)
                                        else Color(0xFF4CAF50).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_income),
                                    contentDescription = "Income",
                                    tint = if (selected == "Income") Color.White else Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selected == "Income") Color.White else Color(0xFF666666),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$4,120.00",
                                fontWeight = FontWeight.Bold,
                                color = if (selected == "Income") Color.White else Color(0xFF4CAF50),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Expense Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected == "Expense")
                            Color(0xFFFF5722) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (selected == "Expense") 16.dp else 8.dp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable { viewModel.onTypeSelected("Expense") }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (selected == "Expense") {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFF5722),
                                            Color(0xFFD84315)
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color(0xFFFFF8F8)
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (selected == "Expense")
                                            Color.White.copy(alpha = 0.2f)
                                        else Color(0xFFFF5722).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_expense),
                                    contentDescription = "Expense",
                                    tint = if (selected == "Expense") Color.White else Color(0xFFFF5722),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Expense",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selected == "Expense") Color.White else Color(0xFF666666),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$1,187.40",
                                fontWeight = FontWeight.Bold,
                                color = if (selected == "Expense") Color.White else Color(0xFFFF5722),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionBodySection(
    navController: NavController,
    viewModel: TransactionScreenViewModel,
    categoryViewModel: CategoryViewModel,
    listState: LazyListState
) {
    val showSearchDialog = remember { mutableStateOf(false) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf<String>("") }
    val formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formatterDateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val transactions = viewModel.filteredTransactions.value

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        viewModel.fetchTransactions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDFF7E2),
                        Color(0xFFB5F2D0),
                        Color(0xFF00D09E).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
        ) {
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    iconRes = R.drawable.ic_search,
                    contentDescription = "Search",
                    onClick = { showSearchDialog.value = true }
                )

                Spacer(modifier = Modifier.width(12.dp))

                ActionButton(
                    iconRes = R.drawable.ic_calendar,
                    contentDescription = "Calendar",
                    onClick = { showDatePickerDialog.value = true }
                )

                if (selectedDate.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDate.value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                ActionButton(
                    iconRes = R.drawable.ic_more,
                    contentDescription = "Add Transaction",
                    onClick = { navController.navigate("add_transaction") },
                    isPrimary = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (transactions.isNotEmpty()) {
                    itemsIndexed(transactions) { index, transaction ->
                        TransactionRow(
                            navController = navController,
                            transaction = transaction
                        )
                    }
                } else {
                    item {
                        EmptyTransactionState()
                    }
                }
            }
        }

        // Dialogs
        if (showSearchDialog.value) {
            SearchDialog(
                viewModel = categoryViewModel,
                onDismiss = { showSearchDialog.value = false },
                onSearch = { params ->
                    showSearchDialog.value = false
                    val request = TransactionSearchRequest(
                        startDate = params.startDate,
                        endDate = params.endDate,
                        type = params.type?.ifBlank { null },
                        category = params.category?.ifBlank { null },
                        amountRange = params.amountRange?.ifBlank { null },
                        keywords = params.keywords?.ifBlank { null },
                        timeRange = params.timeRange?.ifBlank { null },
                        dayOfWeek = params.dayOfWeek?.ifBlank { null }
                    )
                    viewModel.searchTransactions(request)
                }
            )
        }

        if (showDatePickerDialog.value) {
            DatePickerModal(
                onDateSelected = { millis ->
                    showDatePickerDialog.value = false
                    millis?.let {
                        val localDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        selectedDate.value = localDate.format(formatterDateOnly)

                        val from = localDate.atStartOfDay().format(formatterDateTime)
                        val to = localDate.atTime(LocalTime.MAX).format(formatterDateTime)

                        viewModel.fetchTransactionsByDateRange(from, to)
                    }
                },
                onDismiss = { showDatePickerDialog.value = false }
            )
        }
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
private fun TransactionRow(
    navController: NavController,
    transaction: GeneralTransactionItem,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (transaction.isIncome)
                            Color(0xFF4CAF50).copy(alpha = 0.15f)
                        else Color(0xFFFF5722).copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                TransactionIconButton(
                    navController = navController,
                    transactionID = transaction.transactionID,
                    categoryID = transaction.categoryID,
                )
            }

            // Transaction details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0D1F2D),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.timestamp ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = transaction.amount,
                    color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFFF5722),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyTransactionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No transactions found",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@Composable
fun SearchDialog(
    viewModel: CategoryViewModel,
    onDismiss: () -> Unit,
    onSearch: (TransactionSearchRequest) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val formatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amountRange by remember { mutableStateOf("") }

    fun formatAmount(amount: String): String {
        return amount.toIntOrNull()?.let {
            "%,d".format(it).replace(",", ".")
        } ?: amount
    }

    val rawAmountRanges = listOf("0-50000", "50000-200000", "200000-500000", "500000-1000000", "1000000+")
    val amountRangeMap = rawAmountRanges.associate { range ->
        val display = if (range.contains("-")) {
            val (start, end) = range.split("-")
            "${formatAmount(start)} - ${formatAmount(end)}"
        } else {
            formatAmount(range) + "+"
        }
        display to range
    }

    val reverseAmountRangeMap = amountRangeMap.entries.associate { it.value to it.key }
    var keywords by remember { mutableStateOf("") }
    var timeRange by remember { mutableStateOf("") }
    val timeRangeMap = mapOf(
        "00:00-08:00" to "00:00:00-08:00:00",
        "08:00-16:00" to "08:00:00-16:00:00",
        "16:00-00:00" to "16:00:00-23:59:59"
    )

    val reverseTimeRangeMap = timeRangeMap.entries.associate { it.value to it.key }
    var dayOfWeek by remember { mutableStateOf("") }
    val dayOfWeekMap = mapOf(
        "Monday" to "Mon",
        "Tuesday" to "Tue",
        "Wednesday" to "Wed",
        "Thursday" to "Thu",
        "Friday" to "Fri",
        "Saturday" to "Sat",
        "Sunday" to "Sun"
    )

    val reverseDayOfWeekMap = dayOfWeekMap.entries.associate { it.value to it.key }
    val categoriesResult by viewModel.categories.collectAsState()
    val categoryNames = categoriesResult?.getOrNull()?.map { it.name } ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF85E0B3)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header with search design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Advanced Search",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF1A1A1A)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(22.dp)
                            .background(
                                color = Color(0xFFFFB7B7),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                }

                Spacer(modifier = Modifier.height(24.dp))

                // Content area with scroll
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    item {
                        // Date Range Section
                        SearchSection(title = "Date Range", icon = Icons.Default.DateRange) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SearchSearchField(
                                    label = "Start Date",
                                    value = startDate,
                                    isDateField = true,
                                    onChange = { startDate = it },
                                    onClick = { showStartDatePicker = true }
                                )

                                SearchSearchField(
                                    label = "End Date",
                                    value = endDate,
                                    isDateField = true,
                                    onChange = { endDate = it },
                                    onClick = { showEndDatePicker = true }
                                )
                            }
                        }
                    }

                    item {
                        // Transaction Details Section
                        SearchSection(title = "Transaction Details", icon = Icons.Default.AccountBalanceWallet) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    SearchDropdownField(
                                        label = "Type",
                                        options = listOf("Expense", "Income"),
                                        selectedOption = type,
                                        onOptionSelected = { type = it },
                                        modifier = Modifier.weight(1f)
                                    )

                                    SearchDropdownField(
                                        label = "Category",
                                        options = categoryNames,
                                        selectedOption = category,
                                        onOptionSelected = { category = it },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                SearchDropdownField(
                                    label = "Amount Range (VND)",
                                    options = amountRangeMap.keys.toList(),
                                    selectedOption = reverseAmountRangeMap[amountRange] ?: "",
                                    onOptionSelected = { selected ->
                                        amountRange = amountRangeMap[selected] ?: ""
                                    }
                                )

                                SearchSearchField(
                                    label = "Keywords",
                                    value = keywords,
                                    onChange = { keywords = it },
                                    placeholder = "Enter keywords to search..."
                                )
                            }
                        }
                    }

                    item {
                        // Time Filters Section
                        SearchSection(title = "Time Filters", icon = Icons.Default.AccessTime) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SearchDropdownField(
                                    label = "Day of Week",
                                    options = dayOfWeekMap.keys.toList(),
                                    selectedOption = reverseDayOfWeekMap[dayOfWeek] ?: "",
                                    onOptionSelected = { selected ->
                                        dayOfWeek = dayOfWeekMap[selected] ?: ""
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                SearchDropdownField(
                                    label = "Time Range",
                                    options = timeRangeMap.keys.toList(),
                                    selectedOption = reverseTimeRangeMap[timeRange] ?: "",
                                    onOptionSelected = { selected ->
                                        timeRange = timeRangeMap[selected] ?: ""
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Reset all fields
                            startDate = ""
                            endDate = ""
                            type = ""
                            category = ""
                            amountRange = ""
                            keywords = ""
                            timeRange = ""
                            dayOfWeek = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFBDBDBD)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF5F5F5),
                            contentColor = Color(0xFF424242)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(20.dp)
                        )
                    }


                    Button(
                        onClick = {
                            Log.d("SearchDialog", "Start Date: $startDate, End Date: $endDate, type: $type, category: $category, amountRange: $amountRange, keywords: $keywords")
                            onSearch(
                                TransactionSearchRequest(
                                    startDate, endDate, type, category,
                                    amountRange, keywords, timeRange, dayOfWeek
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF36C249)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        DatePickerModal(
            onDateSelected = {
                it?.let { millis ->
                    val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                    startDate = formatter.format(calendar.time)
                }
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    if (showEndDatePicker) {
        DatePickerModal(
            onDateSelected = {
                it?.let { millis ->
                    val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                    endDate = formatter.format(calendar.time)
                }
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@Composable
fun SearchSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FFF1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00D09E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A)
                )
            }
            content()
        }
    }
}

@Composable
fun SearchSearchField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDateField: Boolean = false,
    onClick: (() -> Unit)? = null,
    placeholder: String = ""
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = if (isDateField) { {} } else onChange,
            readOnly = isDateField,
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = Color(0xFFCCCCCC)) }
            } else null,
            trailingIcon = if (isDateField) {
                {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF00D09E),
                        modifier = Modifier.clickable { onClick?.invoke() }
                    )
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isDateField) Modifier.clickable { onClick?.invoke() } else Modifier),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00D09E),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF00D09E)
            ),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryEditable,
                        enabled = true
                    )
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00D09E),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = if (option == selectedOption) Color(0xFF00D09E) else Color(0xFF1A1A1A)
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (option == selectedOption) Color(0xFFF0FDF4) else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}


