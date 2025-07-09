package DI.Composables.GroupTransactionScreen

import DI.Composables.TransactionSection.DropdownSelector
import DI.Composables.TransactionSection.TransactionTextField
import DI.Models.Category.Category
import DI.Models.Wallet.Wallet
import Utils.CurrencyInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneymanagement_frontend.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddGroupTransactionDialog(
    walletList: List<Wallet>,
    categoryList: List<Category>,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String, String, String) -> Unit,
    isVND: Boolean,
    exchangeRate: Double? = null
) {
    val context = LocalContext.current

    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }


    var description by remember { mutableStateOf("") }
    var amountRaw by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    val transactionTypes = listOf("Income", "Expense")

    val calendar = remember { mutableStateOf(Calendar.getInstance()) }

    val displayFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    }
    val displayDate by remember {
        mutableStateOf(displayFormatter.format(calendar.value.time))
    }

    val storageFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }
    val storageDate by remember {
        derivedStateOf { storageFormatter.format(calendar.value.time) }
    }

    var walletError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var typeError by remember { mutableStateOf<String?>(null) }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    fun validateForm(): Boolean {
        var isValid = true

        if (selectedWallet == null) {
            walletError = context.getString(R.string.please_select_wallet)
            isValid = false
        } else {
            walletError = null
        }

        if (selectedCategory == null) {
            categoryError = context.getString(R.string.please_select_category)
            isValid = false
        } else {
            categoryError = null
        }

        val parsedAmount = DI.Utils.CurrencyUtils.parseAmount(amountRaw)
        if (parsedAmount == null) {
            amountError = context.getString(R.string.amount_invalid_error)
            isValid = false
        } else {
            amountError = null
        }

        if (description.trim().isEmpty()) {
            titleError = context.getString(R.string.please_enter_title)
            isValid = false
        } else {
            titleError = null
        }

        if (type.isEmpty()) {
            typeError = context.getString(R.string.please_select_type)
            isValid = false
        } else {
            typeError = null
        }

        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (validateForm()) {

                        val parsedAmount = DI.Utils.CurrencyUtils.parseAmount(amountRaw) ?: 0.0

                        val amountToSave = if (isVND) {
                            parsedAmount
                        } else {
                            val rate = exchangeRate ?: 1.0
                            DI.Utils.CurrencyUtils.usdToVnd(parsedAmount, rate)
                        }
                        val walletId = selectedWallet?.walletID ?: ""
                        val categoryId = selectedCategory?.categoryID ?: ""

                        onSave(walletId, categoryId, amountToSave, description, storageDate, type)
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
            }
        },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        title = {
            Text(
                text = stringResource(R.string.add_group_transaction),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wallet Dropdown
                DropdownSelector(
                    label = stringResource(R.string.wallet),
                    selectedName = selectedWallet?.walletName ?: "",
                    options = walletList.map { it.walletName to it.walletID },
                    onSelect = { _, id ->
                        selectedWallet = walletList.firstOrNull { it.walletID == id }
                        walletError = null
                    },
                    icon = Icons.Default.AccountBalance,
                    placeholder = stringResource(R.string.select_wallet),
                    error = walletError
                )

                // Category Dropdown
                DropdownSelector(
                    label = stringResource(R.string.category),
                    selectedName = selectedCategory?.name ?: "",
                    options = categoryList.map { it.name to it.categoryID },
                    onSelect = { _, id ->
                        selectedCategory = categoryList.firstOrNull { it.categoryID == id }
                        categoryError = null
                    },
                    icon = Icons.Default.Category,
                    placeholder = stringResource(R.string.select_category),
                    error = categoryError
                )

                CurrencyInput(
                    isVND = isVND,
                    label = {
                        Text(
                            text = stringResource(R.string.amount),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    value = amountRaw,
                    onValueChange = { newRaw ->
                        amountRaw = newRaw
                    },
                    onValidationResult = { error ->
                        amountError = error
                    }
                )

                TransactionTextField(
                    label = stringResource(R.string.description),
                    value = description,
                    onValueChange = {
                        description = it
                        titleError = null
                    },
                    placeholder = stringResource(R.string.enter_transaction_title),
                    leadingIcon = Icons.Default.Title,
                    error = titleError
                )

                // Date/Time Field
                TransactionTextField(
                    label = stringResource(R.string.date_time),
                    value = displayDate,
                    onValueChange = {},
                    isDropdown = true,
                    leadingIcon = Icons.Default.CalendarToday,
                    trailingIcon = {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.select_date),
                                tint = Color(0xFF00D09E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                // Type Dropdown
                DropdownSelector(
                    label = stringResource(R.string.type),
                    selectedName = if (type.isNotEmpty()) type.replaceFirstChar { it.uppercase() } else "",
                    options = transactionTypes.map { it to it.lowercase() },
                    onSelect = { _, value ->
                        type = value
                        typeError = null
                    },
                    icon = Icons.Default.Payments,
                    placeholder = stringResource(R.string.select_type),
                    error = typeError
                )
            }
        }
    )

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(stringResource(R.string.ok))
            negativeButton(stringResource(R.string.cancel))
        }
    ) {
        datepicker { date ->
            calendar.value.set(Calendar.YEAR, date.year)
            calendar.value.set(Calendar.MONTH, date.monthValue - 1)
            calendar.value.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            timeDialogState.show()
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("OK")
            negativeButton("Cancel")
        }
    ) {
        timepicker { time ->
            calendar.value.set(Calendar.HOUR_OF_DAY, time.hour)
            calendar.value.set(Calendar.MINUTE, time.minute)
        }
    }
}
