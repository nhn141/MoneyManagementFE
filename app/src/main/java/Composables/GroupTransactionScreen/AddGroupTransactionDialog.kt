package DI.Composables.GroupTransactionScreen

import DI.Models.Category.Category
import DI.Models.Wallet.Wallet
import Utils.CurrencyInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupTransactionDialog(
    walletList: List<Wallet>,
    categoryList: List<Category>,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String, String, String) -> Unit,
    isVND: Boolean,
) {
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    var walletExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    val transactionTypes = listOf("Income", "Expense")
    var typeExpanded by remember { mutableStateOf(false) }

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

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = DI.Utils.CurrencyUtils.parseAmount(amount) ?: 0.0
                    val walletId = selectedWallet?.walletID ?: ""
                    val categoryId = selectedCategory?.categoryID ?: ""

                    onSave(walletId, categoryId, parsedAmount, description, storageDate, type)
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
                ExposedDropdownMenuBox(
                    expanded = walletExpanded,
                    onExpandedChange = { walletExpanded = !walletExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedWallet?.walletName
                            ?: stringResource(R.string.select_wallet),
                        onValueChange = {},
                        label = {
                            Text(
                                text = stringResource(R.string.wallet),
                                color = Color(0xFF00D09E)
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(walletExpanded) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00D09E),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF00D09E),
                            unfocusedLabelColor = Color(0xFF00D09E),
                            cursorColor = Color(0xFF00D09E)
                        ),
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = walletExpanded,
                        onDismissRequest = { walletExpanded = false }
                    ) {
                        walletList.forEach { wallet ->
                            DropdownMenuItem(
                                text = { Text(wallet.walletName) },
                                onClick = {
                                    selectedWallet = wallet
                                    walletExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedCategory?.name ?: stringResource(R.string.select_category),
                        onValueChange = {},
                        label = {
                            Text(
                                text = stringResource(R.string.category),
                                color = Color(0xFF00D09E)
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00D09E),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF00D09E),
                            unfocusedLabelColor = Color(0xFF00D09E),
                            cursorColor = Color(0xFF00D09E)
                        ),
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                CurrencyInput(
                    isVND = isVND, // or pass a prop if you want to support USD
                    label = {
                        Text(
                            text = stringResource(R.string.amount),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    value = amount,
                    onValueChange = { amount = it },
                    onValidationResult = { /* Optionally handle error */ }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            text = stringResource(R.string.description),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                        androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D09E),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color(0xFF00D09E),
                        unfocusedLabelColor = Color(0xFF00D09E),
                        cursorColor = Color(0xFF00D09E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Date/Time Field
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(R.string.date_time),
                            color = Color(0xFF00D09E)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF00D09E),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.select_date),
                                tint = Color(0xFF00D09E)
                            )
                        }
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D09E),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFF00D09E),
                        unfocusedLabelColor = Color(0xFF00D09E),
                        cursorColor = Color(0xFF00D09E)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = type.ifEmpty { stringResource(R.string.select_type) },
                        onValueChange = {},
                        label = {
                            Text(
                                text = stringResource(R.string.type),
                                color = Color(0xFF00D09E)
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00D09E),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF00D09E),
                            unfocusedLabelColor = Color(0xFF00D09E),
                            cursorColor = Color(0xFF00D09E)
                        ),
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        transactionTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type =
                                        option.lowercase() // giữ nguyên 'income' hoặc 'expense' đúng định dạng backend
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
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
