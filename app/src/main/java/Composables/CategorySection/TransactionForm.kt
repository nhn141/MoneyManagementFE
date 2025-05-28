package DI.Composables.CategorySection

import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.OcrViewModel
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.text.NumberFormat
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionForm(
    viewModel: TransactionScreenViewModel,
    navController: NavController,
    type: String,
    onTypeChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    ocrViewModel: OcrViewModel,
    walletViewModel: WalletViewModel
) {
    val context = LocalContext.current
    val categoriesResult by categoryViewModel.categories.collectAsState()
    val walletsResult by walletViewModel.wallets.collectAsState()

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.fetchWallets()
    }

    val selectedDateTime = remember { mutableStateOf(Calendar.getInstance()) }

    val storageFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }
    val displayFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    }

    val displayDate by remember {
        derivedStateOf { displayFormatter.format(selectedDateTime.value.time) }
    }

    val storageDate by remember {
        derivedStateOf { storageFormatter.format(selectedDateTime.value.time) }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var categoryId by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf("") }
    var walletName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    fun formatAmount(input: String): String {
        return input.toLongOrNull()?.let {
            NumberFormat.getNumberInstance(Locale.US)
                .format(it)
                .replace(",", ".")
        } ?: input
    }

    fun unformatAmount(formatted: String): String {
        return formatted.replace(".", "").replace(",", "")
    }

    var rawAmount by remember { mutableStateOf("") }

    val formattedAmount by remember(rawAmount) {
        mutableStateOf(formatAmount(rawAmount))
    }

    val ocrResult by ocrViewModel.ocrResult.collectAsState()

    LaunchedEffect(ocrResult) {
        ocrResult?.let { result ->
            rawAmount = result.amount.toBigDecimal().toPlainString()

            walletsResult?.getOrNull()?.find { it.walletName.equals("Bank", ignoreCase = true) }?.let { wallet ->
                walletName = wallet.walletName
                walletId = wallet.walletID
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
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
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .zIndex(1f)
        ) {
            TransactionFormHeader(type = type)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 20.dp, end = 20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Wallet & Category Section
            item {
                FormSection(
                    title = "Account Details",
                    icon = Icons.Default.AccountBalanceWallet
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DropdownSelector(
                            label = "Wallet",
                            selectedName = walletName,
                            options = walletsResult?.getOrNull()?.map { it.walletName to it.walletID } ?: emptyList(),
                            onSelect = { name, id ->
                                walletName = name
                                walletId = id
                            },
                            icon = Icons.Default.AccountBalance
                        )

                        DropdownSelector(
                            label = "Category",
                            selectedName = categoryName,
                            options = categoriesResult?.getOrNull()?.map { it.name to it.categoryID } ?: emptyList(),
                            onSelect = { name, id ->
                                categoryName = name
                                categoryId = id
                            },
                            icon = Icons.Default.Category
                        )
                    }
                }
            }

            // Amount Section
            item {
                FormSection(
                    title = "Amount",
                    icon = Icons.Default.Payments
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TransactionTextField(
                            label = "Amount (VND)",
                            value = formattedAmount,
                            onValueChange = {
                                rawAmount = unformatAmount(it.filter { char -> char.isDigit() })
                            },
                            leadingIcon = Icons.Default.AttachMoney,
                            keyboardType = KeyboardType.Number
                        )

                        // Quick Amount Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickAmountButton(
                                text = "+1K",
                                onClick = {
                                    val current = rawAmount.toLongOrNull() ?: 0L
                                    rawAmount = (current + 1_000).toString()
                                }
                            )
                            QuickAmountButton(
                                text = "+10K",
                                onClick = {
                                    val current = rawAmount.toLongOrNull() ?: 0L
                                    rawAmount = (current + 10_000).toString()
                                }
                            )
                            QuickAmountButton(
                                text = "+100K",
                                onClick = {
                                    val current = rawAmount.toLongOrNull() ?: 0L
                                    rawAmount = (current + 100_000).toString()
                                }
                            )
                            QuickAmountButton(
                                text = "000",
                                onClick = {
                                    rawAmount += "000"
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                    }
                }
            }

            // Transaction Details Section
            item {
                FormSection(
                    title = "Transaction Details",
                    icon = Icons.Default.Receipt
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TransactionTextField(
                            label = "Title",
                            value = title,
                            onValueChange = { title = it },
                            leadingIcon = Icons.Default.Title,
                            placeholder = "Enter transaction title"
                        )

                        TransactionTextField(
                            label = "Type",
                            value = type,
                            onValueChange = onTypeChange,
                            isDropdown = true,
                            leadingIcon = if (type == "Income") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
                        )
                    }
                }
            }

            // Date & Time Section
            item {
                FormSection(
                    title = "Date & Time",
                    icon = Icons.Default.Schedule
                ) {
                    TransactionTextField(
                        label = "Date & Time",
                        value = displayDate,
                        onValueChange = {},
                        isDropdown = true,
                        leadingIcon = Icons.Default.CalendarToday,
                        trailingIcon = {
                            IconButton(
                                onClick = { dateDialogState.show() },
                                modifier = Modifier
                                    .background(
                                        Color(0xFF00D09E).copy(alpha = 0.1f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF00D09E)
                                )
                            }
                        }
                    )
                }
            }

            // Save Button
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val parsedAmount = unformatAmount(formattedAmount).toDoubleOrNull()
                        if (parsedAmount != null) {
                            viewModel.createTransaction(
                                amount = parsedAmount,
                                description = title,
                                categoryId = categoryId,
                                walletId = walletId,
                                type = type,
                                transactionDate = storageDate
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to save transaction", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D09E)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        "Save Transaction",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // Date & Time Dialogs
    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = Color(0xFFF1FFF3),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF00D09E)),
        elevation = 8.dp,
        buttons = {
            positiveButton(
                text = "Next",
                textStyle = TextStyle(color = Color(0xFF00D09E), fontWeight = FontWeight.Bold)
            ) {
                timeDialogState.show()
            }
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(color = Color(0xFF999999), fontWeight = FontWeight.Medium)
            )
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Select a date",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = Color(0xFF00D09E),
                headerTextColor = Color.White,
                calendarHeaderTextColor = Color(0xFF00D09E),
                dateActiveBackgroundColor = Color(0xFF00D09E),
                dateActiveTextColor = Color.White,
                dateInactiveTextColor = Color.Gray
            )
        ) { localDate ->
            val newDate = selectedDateTime.value.clone() as Calendar
            newDate.set(Calendar.YEAR, localDate.year)
            newDate.set(Calendar.MONTH, localDate.monthValue - 1)
            newDate.set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
            selectedDateTime.value = newDate
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        backgroundColor = Color(0xFFF1FFF3),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF00D09E)),
        elevation = 8.dp,
        buttons = {
            positiveButton(
                text = "OK",
                textStyle = TextStyle(color = Color(0xFF00D09E), fontWeight = FontWeight.Bold)
            )
            negativeButton(
                text = "Cancel",
                textStyle = TextStyle(color = Color(0xFF999999), fontWeight = FontWeight.Medium)
            )
        }
    ) {
        timepicker(
            initialTime = LocalTime.now(),
            title = "Select a time",
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = Color(0xFF00D09E),
                activeTextColor = Color.White,
                selectorColor = Color(0xFF00D09E),
                headerTextColor = Color.Black
            )
        ) { time ->
            val newTime = selectedDateTime.value.clone() as Calendar
            newTime.set(Calendar.HOUR_OF_DAY, time.hour)
            newTime.set(Calendar.MINUTE, time.minute)
            newTime.set(Calendar.SECOND, 0)
            selectedDateTime.value = newTime
        }
    }
}

@Composable
fun TransactionFormHeader(type: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = if (type == "Income") {
                            listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                        } else {
                            listOf(Color(0xFFFF5722), Color(0xFFFF7043))
                        }
                    ),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (type == "Income") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "New $type",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
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
fun QuickAmountButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFF00D09E)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF00D09E)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TransactionTextField(
    label: String,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isDropdown: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = isDropdown,
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = Color(0xFFCCCCCC)) }
            } else null,
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color(0xFF00D09E),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00D09E),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF00D09E)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    selectedName: String,
    options: List<Pair<String, String>>,
    onSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    placeholder: String = "Select an option"
) {
    var expanded by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "dropdown_rotation"
    )

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
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedName.ifEmpty { "" },
                onValueChange = {},
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Color(0xFFCCCCCC)
                    )
                },
                readOnly = true,
                leadingIcon = icon?.let {
                    {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = Color(0xFF00D09E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = Color(0xFF00D09E),
                        modifier = Modifier
                            .rotate(animatedRotation)
                            .padding(8.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryEditable,
                        enabled = true
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00D09E),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                options.forEach { (name, id) ->
                    val isSelected = selectedName == name
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSelected) Color(0xFF00D09E) else Color(0xFF1A1A1A),
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF00D09E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSelect(name, id)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFFF0FDF4) else Color.Transparent
                            )
                    )
                }
            }
        }

    }
}

