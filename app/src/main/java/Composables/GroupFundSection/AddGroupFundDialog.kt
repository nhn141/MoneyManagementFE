package DI.Composables.GroupFundSection

import DI.Utils.CurrencyUtils
import Utils.CurrencyInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun AddGroupFundDialog(
    onDismiss: () -> Unit,
    onSave: (description: String, savingGoal: Double) -> Unit,
    isVND: Boolean,
) {
    var description by remember { mutableStateOf("") }
    var savingGoalText by remember { mutableStateOf("") }
    var savingGoalError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Group Fund") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
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
                    val savingGoal = CurrencyUtils.parseAmount(savingGoalText) ?: 0.0
                    onSave(description, savingGoal)
                },
                enabled = savingGoalError == null && savingGoalText.isNotBlank() && CurrencyUtils.parseAmount(
                    savingGoalText
                ) != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
