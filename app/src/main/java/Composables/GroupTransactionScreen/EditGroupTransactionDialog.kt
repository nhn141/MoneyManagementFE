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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupTransactionDialog(
    walletList: List<Wallet>,
    categoryList: List<Category>,
    transaction: GroupTransactionDto,
    onDismiss: () -> Unit,
    onUpdate: (String, String, Double, String, String) -> Unit,
    onDelete: () -> Unit
) {
    var description by remember { mutableStateOf(transaction.description) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var type by remember { mutableStateOf(transaction.type) }

    var selectedWallet by remember {
        mutableStateOf(walletList.find { it.walletID == transaction.userWalletID })
    }
    var selectedCategory by remember {
        mutableStateOf(categoryList.find { it.categoryID == transaction.userCategoryID })
    }

    var walletExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    val walletID = selectedWallet?.walletID ?: ""
                    val categoryID = selectedCategory?.categoryID ?: ""
                    onUpdate(walletID, categoryID, parsedAmount, description, type)
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
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
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
}
