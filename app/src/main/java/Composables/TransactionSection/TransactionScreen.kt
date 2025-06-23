package DI.Composables.TransactionSection

import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Transaction.TransactionSearchRequest
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.TransactionViewModel
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun TransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    currencyViewModel: CurrencyConverterViewModel,
) {
    val scrollState = rememberLazyListState()
    val selected = transactionViewModel.selectedType.value
    val transactions = transactionViewModel.filteredTransactions.value
    val cashFlow = transactionViewModel.cashFlow.value
    val showSearchDialog = remember { mutableStateOf(false) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf<String>("") }
    val searchParams = transactionViewModel.searchParams.value
    val context = LocalContext.current
    val isVND by currencyViewModel.isVND.collectAsState() // Lấy trạng thái isVND
    val exchangeRate by currencyViewModel.exchangeRate.collectAsState() // Lấy tỷ giá

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        transactionViewModel.fetchTransactions()
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
                        text = stringResource(R.string.transaction),
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

            // Balance Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.total_balance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = CurrencyUtils.formatAmount(
                                amount = cashFlow?.netCashFlow ?: 0.0,
                                isVND = isVND,
                                exchangeRate = exchangeRate
                            ),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D1F2D)
                        )
                    }
                    Log.d("TransactionScreen", "isVND: $isVND, exchangeRate: $exchangeRate")
                }
            }

            // Income + Expense Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
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
                            .clickable { transactionViewModel.onTypeSelected("Income") }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (selected == "Income") {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF4CAF50),
                                                Color(0xFF388E3C)
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
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
                                        contentDescription = stringResource(R.string.income),
                                        tint = if (selected == "Income") Color.White else Color(
                                            0xFF4CAF50
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.income),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selected == "Income") Color.White else Color(
                                        0xFF666666
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = CurrencyUtils.formatAmount(
                                        cashFlow?.totalIncome ?: 0.0,
                                        isVND,
                                        exchangeRate
                                    ), // Cập nhật số tiền
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected == "Income") Color.White else Color(
                                        0xFF4CAF50
                                    ),
                                    fontSize = 16.sp
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
                            .clickable { transactionViewModel.onTypeSelected("Expense") }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (selected == "Expense") {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFFF5722),
                                                Color(0xFFD84315)
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
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
                                        contentDescription = stringResource(R.string.expense),
                                        tint = if (selected == "Expense") Color.White else Color(
                                            0xFFFF5722
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.expense),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selected == "Expense") Color.White else Color(
                                        0xFF666666
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = CurrencyUtils.formatAmount(
                                        cashFlow?.totalExpense ?: 0.0,
                                        isVND,
                                        exchangeRate
                                    ), // Cập nhật số tiền
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected == "Expense") Color.White else Color(
                                        0xFFFF5722
                                    ),
                                    fontSize = 16.sp
                                )
                            }
                        }
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
                    if (searchParams != null || selectedDate.value.isNotEmpty()) {
                        ActionButton(
                            iconRes = R.drawable.ic_refresh,
                            contentDescription = stringResource(R.string.reset_filters),
                            onClick = {
                                transactionViewModel.resetSearch()
                                selectedDate.value = ""
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.filters_reset),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    ActionButton(
                        iconRes = R.drawable.ic_search,
                        contentDescription = stringResource(R.string.search),
                        onClick = { showSearchDialog.value = true }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    ActionButton(
                        iconRes = R.drawable.ic_calendar,
                        contentDescription = stringResource(R.string.calendar),
                        onClick = { showDatePickerDialog.value = true }
                    )

                    if (selectedDate.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedDate.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    ActionButton(
                        iconRes = R.drawable.ic_more,
                        contentDescription = stringResource(R.string.add_transaction),
                        onClick = { navController.navigate("add_transaction") },
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
                    TransactionRow(
                        navController = navController,
                        transaction = transaction,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        categoryViewModel = categoryViewModel,
                        currencyViewModel = currencyViewModel
                    )
                }
            } else {
                item {
                    EmptyTransactionState()
                }
            }
        }

        // Dialogs
        if (showSearchDialog.value) {
            SearchDialog(
                viewModel = categoryViewModel,
                onDismiss = { showSearchDialog.value = false },
                initialSearchParams = searchParams,
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
                    transactionViewModel.searchTransactions(request)
                },
                currencyViewModel = currencyViewModel
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

                        selectedDate.value =
                            localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        val from = localDate.atStartOfDay()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val to = localDate.atTime(LocalTime.MAX)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                        transactionViewModel.fetchTransactionsByDateRange(from, to)
                    }
                },
                onDismiss = { showDatePickerDialog.value = false }
            )
        }
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
        amount = if (isIncome) "$amount" else "-$amount",
        isIncome = isIncome
    )
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
fun TransactionRow(
    navController: NavController,
    transaction: GeneralTransactionItem,
    modifier: Modifier = Modifier,
    categoryViewModel: CategoryViewModel,
    currencyViewModel: CurrencyConverterViewModel
) {
    var category by remember { mutableStateOf<Category?>(null) }
    val isVND by currencyViewModel.isVND.collectAsState()
    val exchangeRate by currencyViewModel.exchangeRate.collectAsState()

    LaunchedEffect(transaction.categoryID) {
        categoryViewModel.getCategoryById(transaction.categoryID)
    }

    val selectedCategoryResult = categoryViewModel.selectedCategory.collectAsState()

    LaunchedEffect(selectedCategoryResult.value) {
        selectedCategoryResult.value?.getOrNull()?.let {
            category = it
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .clickable {
                navController.navigate("transaction_detail/${transaction.transactionID}")
                Log.d("TransactionRow", "ID: ${transaction.transactionID}")
            }
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
                            if (transaction.isIncome)
                                Color(0xFF4CAF50).copy(alpha = 0.15f)
                            else Color(0xFFFF5722).copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TransactionIconButton(
                        categoryName = category?.name ?: "Unknown Category",
                    )
                }

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
            }

            Text(
                text = CurrencyUtils.formatAmount(
                    amount = transaction.amount.toDoubleOrNull() ?: 0.0,
                    isVND = isVND,
                    exchangeRate = exchangeRate
                ),
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFFF5722),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
            )
        }
    }
}


@Composable
fun formatAmount(amount: Double, isVND: Boolean, exchangeRate: Double?): String {
    val displayAmount = if (isVND || exchangeRate == null) {
        amount
    } else {
        amount / exchangeRate
    }

    val symbols = if (isVND) {
        DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
    } else {
        DecimalFormatSymbols().apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
    }

    val formatter = if (isVND) {
        DecimalFormat("#,##0.##", symbols)
    } else {
        DecimalFormat("#,##0.00", symbols)
    }

    val formatted = formatter.format(displayAmount)
    val currencySymbol = if (isVND) "VND" else "USD"

    Log.d("formatAmount", "Exchange Rate: $exchangeRate")
    return "$formatted $currencySymbol"
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
            text = stringResource(R.string.no_transactions_found),
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
        onDismissRequest = onDismiss, confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
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
    initialSearchParams: TransactionSearchRequest? = null,
    onSearch: (TransactionSearchRequest) -> Unit,
    currencyViewModel: CurrencyConverterViewModel
) {
    val context = LocalContext.current
    val isVND by currencyViewModel.isVND.collectAsState() // Lấy trạng thái isVND
    val exchangeRate by currencyViewModel.exchangeRate.collectAsState() // Lấy tỷ giá

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val formatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
    var startDate by remember { mutableStateOf(initialSearchParams?.startDate ?: "") }
    var endDate by remember { mutableStateOf(initialSearchParams?.endDate ?: "") }
    var type by remember { mutableStateOf(initialSearchParams?.type ?: "") }
    var category by remember { mutableStateOf(initialSearchParams?.category ?: "") }
    var amountRange by remember { mutableStateOf(initialSearchParams?.amountRange ?: "") }
    var keywords by remember { mutableStateOf(initialSearchParams?.keywords ?: "") }
    var timeRange by remember { mutableStateOf(initialSearchParams?.timeRange ?: "") }
    var dayOfWeek by remember { mutableStateOf(initialSearchParams?.dayOfWeek ?: "") }

    fun formatAmount(amount: String, isVND: Boolean): String {
        return amount.toIntOrNull()?.let {
            val formatted = "%,d".format(it).replace(",", ".")
            if (isVND) "$formatted VND" else "$formatted USD"
        } ?: amount
    }

    fun unformatAmount(formatted: String): String {
        return formatted.replace(".", "").replace(",", "").replace(" VND", "").replace(" USD", "")
    }

    // Tạo danh sách rawAmountRanges dựa trên đơn vị tiền tệ
    val rawAmountRanges = if (isVND) {
        listOf("0-50000", "50000-200000", "200000-500000", "500000-1000000", "1000000+")
    } else {
        exchangeRate?.let { rate ->
            listOf(
                "0-${(50000 / rate).toInt()}",
                "${(50000 / rate).toInt()}-${(200000 / rate).toInt()}",
                "${(200000 / rate).toInt()}-${(500000 / rate).toInt()}",
                "${(500000 / rate).toInt()}-${(1000000 / rate).toInt()}",
                "${(1000000 / rate).toInt()}+"
            )
        } ?: listOf(
            "0-50",
            "50-200",
            "200-500",
            "500-1000",
            "1000+"
        ) // Giá trị mặc định nếu exchangeRate null
    }

    // Define a local object to hold non-composable string constants
    val nonComposableStrings = object {
        val allOption = "All"
        val time0008 = "00:00 - 08:00"
        val time0816 = "08:00 - 16:00"
        val time1600 = "16:00 - 24:00"
        val monday = "Monday"
        val tuesday = "Tuesday"
        val wednesday = "Wednesday"
        val thursday = "Thursday"
        val friday = "Friday"
        val saturday = "Saturday"
        val sunday = "Sunday"
        val pleaseSelectDates = "Please select both start and end dates"
        val startDateLaterError = "Start date cannot be after end date"
    }

    val amountRangeMap = buildMap {
        put(nonComposableStrings.allOption, "")
        putAll(rawAmountRanges.associateBy { range ->
            val display = if (range.contains("-")) {
                val (start, end) = range.split("-")
                "${formatAmount(start, isVND)} - ${formatAmount(end, isVND)}"
            } else {
                formatAmount(range, isVND) + "+"
            }
            display
        })
    }

    val reverseAmountRangeMap = amountRangeMap.entries.associate { it.value to it.key }

    val timeRangeMap = buildMap {
        put(nonComposableStrings.allOption, "")
        putAll(
            mapOf(
                nonComposableStrings.time0008 to "00:00:00-08:00:00",
                nonComposableStrings.time0816 to "08:00:00-16:00:00",
                nonComposableStrings.time1600 to "16:00:00-23:59:59"
            )
        )
    }

    val reverseTimeRangeMap = timeRangeMap.entries.associate { it.value to it.key }

    val dayOfWeekMap = buildMap {
        put(nonComposableStrings.allOption, "")
        putAll(
            mapOf(
                nonComposableStrings.monday to "Mon",
                nonComposableStrings.tuesday to "Tue",
                nonComposableStrings.wednesday to "Wed",
                nonComposableStrings.thursday to "Thu",
                nonComposableStrings.friday to "Fri",
                nonComposableStrings.saturday to "Sat",
                nonComposableStrings.sunday to "Sun"
            )
        )
    }
    val reverseDayOfWeekMap = dayOfWeekMap.entries.associate { it.value to it.key }
    val categoriesResult by viewModel.categories.collectAsState()
    val categoryNames = buildList {
        add(nonComposableStrings.allOption)
        addAll(categoriesResult?.getOrNull()?.map { it.name } ?: emptyList())
    }

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
                            text = stringResource(R.string.advanced_search),
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
                            contentDescription = stringResource(R.string.close),
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
                        SearchSection(
                            title = stringResource(R.string.date_range),
                            icon = Icons.Default.DateRange
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SearchSearchField(
                                    label = stringResource(R.string.start_date),
                                    value = startDate,
                                    isDateField = true,
                                    onChange = { startDate = it },
                                    onClick = { showStartDatePicker = true }
                                )

                                SearchSearchField(
                                    label = stringResource(R.string.end_date),
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
                        SearchSection(
                            title = stringResource(R.string.transaction_details_section),
                            icon = Icons.Default.AccountBalanceWallet
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    SearchDropdownField(
                                        label = stringResource(R.string.type),
                                        options = listOf(
                                            stringResource(R.string.all),
                                            stringResource(R.string.expense),
                                            stringResource(R.string.income)
                                        ),
                                        selectedOption = type.ifEmpty { stringResource(R.string.all) },
                                        onOptionSelected = { selected ->
                                            type =
                                                if (selected == nonComposableStrings.allOption) ""
                                                else selected
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    SearchDropdownField(
                                        label = stringResource(R.string.category),
                                        options = categoryNames,
                                        selectedOption = category.ifEmpty { stringResource(R.string.all) },
                                        onOptionSelected = { selected ->
                                            category =
                                                if (selected == nonComposableStrings.allOption) "" else selected
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                SearchDropdownField(
                                    label = "Amount Range (${if (isVND) "VND" else "USD"})",
                                    options = amountRangeMap.keys.toList(),
                                    selectedOption = reverseAmountRangeMap[amountRange]
                                        ?: stringResource(R.string.all),
                                    onOptionSelected = { selected ->
                                        amountRange = amountRangeMap[selected] ?: ""
                                    }
                                )

                                SearchSearchField(
                                    label = stringResource(R.string.keywords),
                                    value = keywords,
                                    onChange = { keywords = it },
                                    placeholder = stringResource(R.string.enter_keywords_search)
                                )
                            }
                        }
                    }
                    item {
                        // Time Filters Section
                        SearchSection(
                            title = stringResource(R.string.time_filters),
                            icon = Icons.Default.AccessTime
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SearchDropdownField(
                                    label = stringResource(R.string.day_of_week),
                                    options = dayOfWeekMap.keys.toList(),
                                    selectedOption = reverseDayOfWeekMap[dayOfWeek]
                                        ?: stringResource(R.string.all),
                                    onOptionSelected = { selected ->
                                        dayOfWeek = dayOfWeekMap[selected] ?: ""
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                SearchDropdownField(
                                    label = stringResource(R.string.time_range),
                                    options = timeRangeMap.keys.toList(),
                                    selectedOption = reverseTimeRangeMap[timeRange]
                                        ?: stringResource(R.string.all),
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
                            contentDescription = stringResource(R.string.reset),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (startDate.isEmpty() || endDate.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    nonComposableStrings.pleaseSelectDates,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val startDateMillis = formatter.parse(startDate)?.time ?: 0
                            val endDateMillis = formatter.parse(endDate)?.time ?: 0

                            if (startDateMillis > endDateMillis) {
                                Toast.makeText(
                                    context,
                                    nonComposableStrings.startDateLaterError,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val adjustedAmountRange = if (!isVND && exchangeRate != null) {
                                amountRange.split("-").joinToString("-") { range ->
                                    val value = range.removeSuffix("+").toDoubleOrNull() ?: 0.0
                                    ((value * exchangeRate!!).toInt()
                                        .toString() + (if (range.endsWith("+")) "+" else ""))
                                }
                            } else {
                                amountRange
                            }

                            onSearch(
                                TransactionSearchRequest(
                                    startDate, endDate, type, category,
                                    adjustedAmountRange, keywords, timeRange, dayOfWeek
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
                        Text(
                            stringResource(R.string.search),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
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
            onValueChange = if (isDateField) {
                {}
            } else onChange,
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
                                color = if (option == selectedOption) Color(0xFF00D09E) else Color(
                                    0xFF1A1A1A
                                ),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
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


