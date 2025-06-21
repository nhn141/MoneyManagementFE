package DI.Composables.GroupTransactionScreen

import DI.Models.Category.Category
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.Wallet.Wallet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
    onDelete: () -> Unit
) {
    var description by remember { mutableStateOf(transaction.description) }
    var amountRaw by remember { mutableStateOf(transaction.amount.toLong().toString()) }
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
        } catch (_: Exception) { }
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

    val formattedAmount by remember(amountRaw) {
        mutableStateOf(formatAmount(amountRaw))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amountRaw.toDoubleOrNull() ?: 0.0
                    val walletID = selectedWallet?.walletID ?: ""
                    val categoryID = selectedCategory?.categoryID ?: ""
                    onUpdate(walletID, categoryID, parsedAmount, description, storageDate, type)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        title = { Text("Edit Group Transaction") },
        text = {
            Column {
                // Wallet dropdown
                ExposedDropdownMenuBox(
                    expanded = walletExpanded,
                    onExpandedChange = { walletExpanded = !walletExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedWallet?.walletName ?: "Select Wallet",
                        onValueChange = {},
                        label = { Text("Wallet") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded)
                        },
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
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
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

                OutlinedTextField(
                    value = formattedAmount,
                    onValueChange = { input ->
                        amountRaw = input.filter { char -> char.isDigit() }
                    },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date picker
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date & Time") },
                    trailingIcon = {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pick Date",
                                tint = Color(0xFF00D09E)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("OK")
            negativeButton("Cancel")
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
