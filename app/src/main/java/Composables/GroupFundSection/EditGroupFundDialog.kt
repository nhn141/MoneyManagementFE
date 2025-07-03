package DI.Composables.GroupFundSection

import DI.Models.GroupFund.GroupFundDto
import DI.Utils.CurrencyUtils
import Utils.CurrencyInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EditGroupFundDialog(
    fund: GroupFundDto,
    onDismiss: () -> Unit,
    onUpdate: (newDescription: String, newSavingGoal: Double) -> Unit,
    onDelete: () -> Unit,
    isVND: Boolean,
    exchangeRate: Double? = 1.0
) {
    var description by remember { mutableStateOf(fund.description) }
    var savingGoalText by remember {
        mutableStateOf(
            CurrencyUtils.formatAmount(
                fund.savingGoal,
                isVND,
                exchangeRate
            )
        )
    }
    var savingGoalError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Group Fund", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true
                )
                CurrencyInput(
                    isVND = isVND,
                    label = "Saving Goal",
                    value = savingGoalText,
                    onValueChange = { savingGoalText = it },
                    onValidationResult = { savingGoalError = it }
                )
                if (savingGoalError != null) {
                    Text(savingGoalError ?: "", color = androidx.compose.ui.graphics.Color.Red)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val goal = CurrencyUtils.parseAmount(savingGoalText)
                    if (goal != null) {
                        onUpdate(description.trim(), goal)
                        onDismiss()
                    }
                },
                enabled = savingGoalError == null && savingGoalText.isNotBlank() && CurrencyUtils.parseAmount(
                    savingGoalText
                ) != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
//                TextButton(onClick = onDelete) {
//                    Text("Delete", color = Color.Red)
//                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}