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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneymanagement_frontend.R

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
        title = {
            Text(
                text = stringResource(R.string.add_group_fund),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            text = stringResource(R.string.description_label),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
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
                    )
                )
                CurrencyInput(
                    isVND = isVND,
                    label = {
                        Text(
                            text = stringResource(R.string.saving_goal_label),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    value = savingGoalText,
                    onValueChange = { savingGoalText = it },
                    onValidationResult = { savingGoalError = it }
                )
                if (savingGoalError != null) {
                    Text(
                        savingGoalError ?: "",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
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
        tonalElevation = 2.dp
    )
}
