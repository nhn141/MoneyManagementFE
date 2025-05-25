package DI.Composables.TransactionSection

import DI.Composables.GeneralTemplate
import DI.Models.Category.Transaction
import DI.Models.Transaction.TransactionSearchRequest
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun TransactionPageScreen(
    navController: NavController,
    transactionViewModel: TransactionScreenViewModel,
    categoryViewModel: CategoryViewModel
) {
    GeneralTemplate(
        contentHeader = { TransactionHeaderSection(navController, transactionViewModel) },
        contentBody = { TransactionBodySection(navController, transactionViewModel, categoryViewModel) },
        fraction = 0.35f
    )
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
    viewModel: TransactionScreenViewModel
) {
    val selected = viewModel.selectedType.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF53dba9))
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== Top Bar =====
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.popBackStack()
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Title
            Text(
                text = "Transaction",
                color = Color.Black,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            // Notification icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { /* Handle Notifications */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notifications),
                    contentDescription = "Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Total Balance =====
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FFF3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$7,783.00",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1F2D)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Income + Expense =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Income Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == "Income") Color(0xFF0068FF) else Color(0xFFF1FFF3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .clickable { viewModel.onTypeSelected("Income") }

            ) {
                Column(
                    modifier = Modifier.fillMaxSize() .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_income),
                        contentDescription = "Income",
                        tint = if (selected == "Income") Color.White else Color(0xFF00D09E),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected == "Income") Color.White else Color.Black,
                    )
                    Text(
                        text = "$4,120.00",
                        fontWeight = FontWeight.Bold,
                        color = if (selected == "Income") Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Expense Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == "Expense") Color(0xFF0068FF) else Color(0xFFF1FFF3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .clickable { viewModel.onTypeSelected("Expense") }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize() .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_expense),
                        contentDescription = "Expense",
                        tint = if (selected == "Expense") Color.White else Color(0xFF0068FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Expense",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected == "Expense") Color.White else Color.Black
                    )
                    Text(
                        text = "$1,187.40",
                        fontWeight = FontWeight.Bold,
                        color = if (selected == "Expense") Color.White else Color(0xFF0068FF)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionBodySection(
    navController: NavController,
    viewModel: TransactionScreenViewModel,
    categoryViewModel: CategoryViewModel
) {
    val showSearchDialog = remember { mutableStateOf(false) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf<String>("") }
    val formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formatterDateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val transactions = viewModel.filteredTransactions.value

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        viewModel.fetchTransactions()
    }

    // Snap effect: cuộn tới item gần nhất khi scroll kết thúc
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
            firstVisibleItem?.let { item ->
                val offset = item.offset
                val threshold = item.size / 2
                val targetIndex = if (offset < -threshold) item.index + 1 else item.index
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 25.dp, top = 70.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
            ) {
                item {
                    Spacer(modifier = Modifier.height(7.dp))
                }

                if (transactions.isNotEmpty()) {
                    itemsIndexed(transactions) { index, transaction ->
                        GeneralTransactionRow(navController = navController, transaction = transaction)
                    }
                } else {
                    item {
                        Text("No transactions found.")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Nút chức năng ở góc trên bên phải
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(28.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút tìm
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xFF00D09E))
                        .size(30.dp)
                        .clickable { showSearchDialog.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Advanced Search",
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

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

                // Nút Calendar
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xFF00D09E))
                        .size(30.dp)
                        .clickable { showDatePickerDialog.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = selectedDate.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )

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

                // Nút Add Transaction
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xFF00D09E))
                        .size(30.dp)
                        .clickable {
                            navController.navigate("add_transaction")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "Add Transaction",
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GeneralTransactionRow(
    navController: NavController,
    transaction: GeneralTransactionItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF0068FF).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            TransactionIconButton(
                navController = navController,
                transactionID = transaction.transactionID,
                categoryID = transaction.categoryID,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black
            )

            Text(
                text = transaction.timestamp ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF0068FF)
            )
        }

        Column(
            modifier = Modifier.padding(start = 12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = transaction.amount,
                color = if (transaction.isIncome) Color.Black else Color(0xFF0080FF),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun GeneralTransactionSummary(
    navController: NavController,
    transactions: List<GeneralTransactionItem>,
) {
    Column {
        transactions.forEach { transaction ->
            GeneralTransactionRow(navController = navController, transaction = transaction)
        }
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
        Box(
            modifier = Modifier
                .background(Color(0xFFDFF7E2), shape = RoundedCornerShape(16.dp))
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Advanced Search",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        SearchField(
                            label = "Start Date",
                            value = startDate,
                            isDropdown = true,
                            onChange = { startDate = it },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Start Date",
                                    tint = Color(0xFF00D09E),
                                    modifier = Modifier.clickable { showStartDatePicker = true }
                                )
                            }
                        )

                        SearchDropdownField(
                            label = "Type",
                            options = listOf("Expense", "Income"),
                            selectedOption = type,
                            onOptionSelected = { type = it }
                        )
                        SearchDropdownField(
                            label = "Amount Range (VND)",
                            options = amountRangeMap.keys.toList(),
                            selectedOption = reverseAmountRangeMap[amountRange] ?: "",
                            onOptionSelected = { selected ->
                                amountRange = amountRangeMap[selected] ?: ""
                            }
                        )
                        SearchField(label = "Keywords", value = keywords, onChange = { keywords = it })
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        SearchField(
                            label = "End Date",
                            isDropdown = true,
                            value = endDate,
                            onChange = { endDate = it },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select End Date",
                                    tint = Color(0xFF00D09E),
                                    modifier = Modifier.clickable { showEndDatePicker = true }
                                )
                            }
                        )
                        SearchDropdownField(
                            label = "Category",
                            options = categoryNames,
                            selectedOption = category,
                            onOptionSelected = { category = it }
                        )
                        SearchDropdownField(
                            label = "Day of Week",
                            options = dayOfWeekMap.keys.toList(),
                            selectedOption = reverseDayOfWeekMap[dayOfWeek] ?: "",
                            onOptionSelected = { selected ->
                                dayOfWeek = dayOfWeekMap[selected] ?: ""
                            }
                        )
                        SearchDropdownField(
                            label = "Time Range",
                            options = timeRangeMap.keys.toList(),
                            selectedOption = reverseTimeRangeMap[timeRange] ?: "",
                            onOptionSelected = { selected ->
                                timeRange = timeRangeMap[selected] ?: ""
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D09E),
                        contentColor = Color.White
                    )
                ) {
                    Text("Search", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
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
fun SearchField(
    label: String,
    value: String,
    isDropdown: Boolean = false,
    onChange: (String) -> Unit,
    trailingIcon: (@Composable (() -> Unit))? = null
) {
    OutlinedTextField(
        value = value,
        readOnly = isDropdown,
        onValueChange = onChange,
        label = { Text(label) },
        trailingIcon = trailingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF1FFF3), shape = RoundedCornerShape(8.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00D09E),
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color(0xFFF1FFF3),
            unfocusedContainerColor = Color(0xFFF1FFF3)
        )
    )
}

@Composable
fun SearchDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {

        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1FFF3), shape = RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00D09E),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF1FFF3),
                unfocusedContainerColor = Color(0xFFF1FFF3)
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


