package DI.Composables.TransactionSection

import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import Utils.CurrencyInput
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale


@Composable
fun TransactionEditScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyViewModel: CurrencyConverterViewModel
) {
    val selectedTransaction by viewModel.selectedTransaction
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
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

    Box(
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
        if (isLoaded && selectedTransaction != null && categories != null && wallets != null) {
            val categoryList = categories?.getOrNull() ?: emptyList()
            val walletList = wallets?.getOrNull() ?: emptyList()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TransactionEditHeader(navController = navController)
                TransactionEditBody(
                    transaction = selectedTransaction!!,
                    categoryList = categoryList,
                    walletList = walletList,
                    viewModel = viewModel,
                    navController = navController,
                    currencyViewModel = currencyViewModel, // Truyền CurrencyViewModel
                    isVND = isVND,
                    exchangeRate = exchangeRate
                )
            }
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
fun TransactionEditHeader(
    navController: NavController,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = CircleShape
                    )
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF2D3748),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = stringResource(R.string.edit_transaction),
                color = Color(0xFF2D3748),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            // Spacer to balance the layout
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

fun formatAmountForInput(amount: Double, isVND: Boolean, exchangeRate: Double?): String {
    val displayAmount = if (isVND || exchangeRate == null) {
        amount
    } else {
        amount / exchangeRate
    }
    val decimalFormat = DecimalFormat("#,##0.00").apply {
        decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
    }
    return decimalFormat.format(displayAmount)
}

@Composable
fun TransactionEditBody(
    transaction: Transaction,
    categoryList: List<Category>,
    walletList: List<Wallet>,
    viewModel: TransactionViewModel,
    navController: NavController,
    currencyViewModel: CurrencyConverterViewModel,
    isVND: Boolean, // Thêm isVND
    exchangeRate: Double? // Thêm exchangeRate
) {
    val dateFormatStorage = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    val dateFormatDisplay = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    }

    val selectedDateTime = remember {
        mutableStateOf<Calendar?>(
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(transaction.transactionDate)
                ?.let {
                    Calendar.getInstance().apply { time = it }
                }
        )
    }

    val displayDate by remember {
        derivedStateOf {
            selectedDateTime.value?.let { dateFormatDisplay.format(it.time) } ?: ""
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var categoryId by remember { mutableStateOf(transaction.categoryID) }
    var categoryName by remember {
        mutableStateOf(categoryList.find { it.categoryID == transaction.categoryID }?.name ?: "")
    }

    var walletId by remember { mutableStateOf(transaction.walletID) }
    var walletName by remember {
        mutableStateOf(walletList.find { it.walletID == transaction.walletID }?.walletName ?: "")
    }

    // Hiển thị số tiền theo đơn vị được chọn
    var amount by remember {
        mutableStateOf(
            CurrencyUtils.formatAmount(
                amount = transaction.amount,
                isVND = isVND,
                exchangeRate = exchangeRate
            )
        )
    }
    Log.d(
        "TransactionEditBody",
        "Initial amount: $amount, isVND: $isVND, exchangeRate: $exchangeRate"
    )

    var title by remember { mutableStateOf(transaction.description) }
    val type by remember { mutableStateOf(transaction.type) }
    val isIncome = type.equals("income", ignoreCase = true)
    val isExpense = type.equals("expense", ignoreCase = true)
    val context = LocalContext.current

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var amountError by remember { mutableStateOf<String?>(null) }

    // Create non-composable strings object for error messages and toasts
    val nonComposableStrings = remember {
        object {
            val titleEmptyError = context.getString(R.string.title_empty_error)
            val categoryEmptyError = context.getString(R.string.category_empty_error)
            val walletEmptyError = context.getString(R.string.wallet_empty_error)
            val dateTimeEmptyError = context.getString(R.string.date_time_empty_error)
            val transactionUpdateSuccess = context.getString(R.string.transaction_update_success)
            val transactionUpdateFailed = context.getString(R.string.transaction_update_failed)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Transaction Type Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.transaction_type_unchangeable),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TransactionTypeButton(
                            text = stringResource(R.string.income_uppercase),
                            isSelected = isIncome,
                            selectedColor = Color(0xFF48BB78),
                            modifier = Modifier.weight(1f)
                        )

                        TransactionTypeButton(
                            text = stringResource(R.string.expense_uppercase),
                            isSelected = isExpense,
                            selectedColor = Color(0xFFE53E3E),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            // Form Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TransactionTextField(
                        label = stringResource(R.string.title),
                        value = title,
                        onValueChange = { title = it },
                        leadingIcon = Icons.Default.Edit,
                        placeholder = stringResource(R.string.enter_transaction_title)
                    )

                    CurrencyInput(
                        isVND = isVND,
                        label = {
                            Text(stringResource(R.string.amount, if (isVND) "₫" else "$"))
                        },
                        value = amount,
                        onValueChange = { newAmount ->
                            amount = newAmount
                        },
                        onValidationResult = { errorMessage ->
                            amountError = errorMessage
                        }
                    )

                    DropdownSelector(
                        label = stringResource(R.string.category),
                        selectedName = categoryName,
                        options = categoryList.map { it.name to it.categoryID.toString() },
                        onSelect = { name, id ->
                            categoryName = name
                            categoryId = id
                        },
                        icon = Icons.Default.Category,
                        placeholder = stringResource(R.string.select_category)
                    )

                    DropdownSelector(
                        label = stringResource(R.string.wallet),
                        selectedName = walletName,
                        options = walletList.map { it.walletName to it.walletID.toString() },
                        onSelect = { name, id ->
                            walletName = name
                            walletId = id
                        },
                        icon = Icons.Default.AccountBalanceWallet,
                        placeholder = stringResource(R.string.select_wallet)
                    )

                    TransactionTextField(
                        label = stringResource(R.string.date_time),
                        value = displayDate,
                        onValueChange = {},
                        isDropdown = true,
                        leadingIcon = Icons.Default.DateRange,
                        placeholder = stringResource(R.string.select_date_time),
                        trailingIcon = {
                            IconButton(onClick = { dateDialogState.show() }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = stringResource(R.string.select_date),
                                    tint = Color(0xFF00D09E)
                                )
                            }
                        }
                    )
                }
            }
        }

        item {
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            // Save Button
            Button(
                onClick = {

                    // Validate all fields
                    when {
                        title.isBlank() -> {
                            showError = true
                            errorMessage = nonComposableStrings.titleEmptyError
                        }

                        amountError != null -> {
                            showError = true
                            errorMessage = amountError!!
                        }

                        categoryId.isBlank() -> {
                            showError = true
                            errorMessage = nonComposableStrings.categoryEmptyError
                        }

                        walletId.isBlank() -> {
                            showError = true
                            errorMessage = nonComposableStrings.walletEmptyError
                        }

                        selectedDateTime.value == null -> {
                            showError = true
                            errorMessage = nonComposableStrings.dateTimeEmptyError
                        }

                        else -> {
                            showError = false
                            val amountInVND = currencyViewModel.toVND(
                                CurrencyUtils.parseAmount(amount) ?: 0.0,
                            ) ?: return@Button
                            val updatedTransaction = transaction.copy(
                                description = title,
                                amount = amountInVND, // Lưu số tiền ở VND
                                categoryID = categoryId,
                                walletID = walletId,
                                type = type,
                                transactionDate = selectedDateTime.value?.let {
                                    dateFormatStorage.format(it.time)
                                } ?: ""
                            )

                            viewModel.updateTransaction(
                                transactionID = updatedTransaction.transactionID,
                                amount = updatedTransaction.amount,
                                description = updatedTransaction.description,
                                categoryId = updatedTransaction.categoryID,
                                walletId = updatedTransaction.walletID,
                                type = updatedTransaction.type,
                                transactionDate = updatedTransaction.transactionDate
                            ) { success ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        nonComposableStrings.transactionUpdateSuccess,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(
                                        context,
                                        nonComposableStrings.transactionUpdateFailed,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.save_changes),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Date and Time Dialogs
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(stringResource(R.string.ok))
            negativeButton(stringResource(R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = selectedDateTime.value?.let {
                LocalDate.of(
                    it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH) + 1,
                    it.get(Calendar.DAY_OF_MONTH)
                )
            } ?: LocalDate.now(),
            title = stringResource(R.string.select_a_date)
        ) { localDate ->
            val currentCal = selectedDateTime.value ?: Calendar.getInstance()
            val newCal = Calendar.getInstance().apply {
                timeInMillis = currentCal.timeInMillis
                set(Calendar.YEAR, localDate.year)
                set(Calendar.MONTH, localDate.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
            }
            selectedDateTime.value = newCal
            timeDialogState.show()
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(stringResource(R.string.ok))
            negativeButton(stringResource(R.string.cancel))
        }
    ) {
        timepicker(
            initialTime = selectedDateTime.value?.let {
                LocalTime.of(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
            } ?: LocalTime.now(),
            title = stringResource(R.string.select_a_time)
        ) { time ->
            val currentCal = selectedDateTime.value ?: Calendar.getInstance()
            val newCal = Calendar.getInstance().apply {
                timeInMillis = currentCal.timeInMillis
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
            }
            selectedDateTime.value = newCal
        }
    }
}

@Composable
fun TransactionTypeButton(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(300)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300)
    )

    Card(
        modifier = modifier.scale(animatedScale),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor else Color(0xFFF7FAFC)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF718096)
            )
        }
    }
}



