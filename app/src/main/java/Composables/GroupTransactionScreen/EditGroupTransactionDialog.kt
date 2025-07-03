package DI.Composables.GroupTransactionScreen

import DI.Models.Category.Category
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
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
fun EditGroupTransactionDialog(
    walletList: List<Wallet>,
    categoryList: List<Category>,
    transaction: GroupTransactionDto,
    onDismiss: () -> Unit,
    onUpdate: (String, String, Double, String, String, String) -> Unit,
    onDelete: () -> Unit,
    isVND: Boolean,
    exchangeRate: Double? = 1.0
) {
    var description by remember { mutableStateOf(transaction.description) }
    var amountRaw by remember {
        mutableStateOf(
            CurrencyUtils.formatAmount(
                transaction.amount,
                isVND,
                exchangeRate
            )
        )
    }
    var type by remember { mutableStateOf(transaction.type) }

    var selectedWallet by remember {
        mutableStateOf(walletList.find { it.walletID == transaction.userWalletID })
    }
    var selectedCategory by remember {
        mutableStateOf(categoryList.find { it.categoryID == transaction.userCategoryID })
    }

    var walletExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val calendar = remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val parsed = inputFormat.parse(transaction.transactionDate)
            parsed?.let { calendar.value.time = it }
        } catch (_: Exception) {
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    val displayDate by remember {
        derivedStateOf {
            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(calendar.value.time)
        }
    }

    val storageDate by remember {
        derivedStateOf {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.value.time)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = CurrencyUtils.parseAmount(amountRaw) ?: 0.0
                    val walletID = selectedWallet?.walletID ?: ""
                    val categoryID = selectedCategory?.categoryID ?: ""
                    onUpdate(walletID, categoryID, parsedAmount, description, storageDate, type)
                }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        title = {
            Text(
                text = stringResource(R.string.edit_group_transaction),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Wallet dropdown
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
                                text = stringResource(R.string.select_wallet),
                                color = Color(0xFF00D09E)
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded)
                        },
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

                // Category dropdown
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
                                text = stringResource(R.string.select_category),
                                color = Color(0xFF00D09E)
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
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
                        categoryList.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Utils.CurrencyInput(
                    isVND = isVND,
                    label = {
                        Text(
                            text = stringResource(R.string.amount),
                            color = Color(0xFF00D09E)
                        )
                    },
                    value = amountRaw,
                    onValueChange = { amountRaw = it },
                    onValidationResult = { /* Optionally handle error */ }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            text = stringResource(R.string.description),
                            color = Color(0xFF00D09E)
                        )
                    },
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
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

                // Date picker
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(R.string.date_and_time),
                            color = Color(0xFF00D09E)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF00D09E),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.pick_date),
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

                // Type field
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = {
                        Text(
                            text = stringResource(R.string.type),
                            color = Color(0xFF00D09E)
                        )
                    },
                    singleLine = true,
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
            positiveButton(stringResource(R.string.ok))
            negativeButton(stringResource(R.string.cancel))
        }
    ) {
        timepicker { time ->
            calendar.value.set(Calendar.HOUR_OF_DAY, time.hour)
            calendar.value.set(Calendar.MINUTE, time.minute)
        }
    }
}
