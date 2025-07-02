package Utils

import DI.Utils.CurrencyUtils
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanagement_frontend.R

@Composable
fun CurrencyInput(
    isVND: Boolean,
    label: String? = null,
    value: String = "",
    onValueChange: ((String) -> Unit)? = null,
    onValidationResult: ((String?) -> Unit)? = null
) {
    val context = LocalContext.current
    Column {
        var showUSDPreview by remember { mutableStateOf(false) }
        var isAmountFieldFocused by remember { mutableStateOf(false) }
        var amountTextFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
        Log.d("CurrencyInput", "AmountTextFieldValue: $amountTextFieldValue")
        var parsedAmount by remember { mutableDoubleStateOf(0.0) }
        var amountError by remember { mutableStateOf<String?>(null) }
        var isValid by remember { mutableStateOf(true) }

        LaunchedEffect(value) {
            if (value != amountTextFieldValue.text) {
                amountTextFieldValue = TextFieldValue(value, TextRange(value.length))
            }
        }

        fun validateAmount(rawAmount: String) {
            if (rawAmount.isEmpty()) {
                amountError = context.getString(R.string.please_enter_amount)
                isValid = false
            } else if (CurrencyUtils.parseAmount(rawAmount) == null) {
                amountError = context.getString(R.string.amount_invalid_error)
                isValid = false
            } else {
                amountError = null
                isValid = true
            }
        }

        CurrencyInputTextField(
            label = label,
            value = amountTextFieldValue,
            onValueChange = { newValue ->
                Log.d("CurrencyInput", "New value: ${newValue.text}")
                amountTextFieldValue = newValue
                validateAmount(newValue.text)
                if (onValueChange != null) {
                    onValueChange(newValue.text)
                }
                if (onValidationResult != null) {
                    onValidationResult(amountError)
                }
            },
            isVND = isVND,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isAmountFieldFocused = focusState.isFocused
                },
            placeholder = stringResource(R.string.enter_amount),
            onFormatted = { _, amount ->
                parsedAmount = amount ?: 0.0
            },
            leadingIcon = Icons.Default.MonetizationOn,
            error = amountError
        )

        LaunchedEffect(amountTextFieldValue.text, isVND, isAmountFieldFocused) {
            showUSDPreview = !isVND
                    && amountTextFieldValue.text.isNotEmpty()
                    && isAmountFieldFocused
        }

        if (showUSDPreview) {
            USDInputPreview(
                inputText = amountTextFieldValue.text,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CurrencyInputTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isVND: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    error: String? = null, // new: error message
    leadingIcon: ImageVector? = null, // new: leading icon
    trailingIcon: @Composable (() -> Unit)? = null, // new: trailing icon
    onFormatted: ((formattedText: String, parsedAmount: Double?) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var showDecimalWarning by remember { mutableStateOf(false) }

    // Default placeholder based on currency
    val defaultPlaceholder = if (isVND) "e.g. 1.000.000₫" else "e.g. 1000.51 (formats when done)"

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = { newTextFieldValue ->
                if (isVND) {
                    // VND: Live formatting while typing
                    val inputText = newTextFieldValue.text
                    if (inputText.isEmpty()) {
                        onValueChange(newTextFieldValue)
                        onFormatted?.invoke("", null)
                        return@OutlinedTextField
                    }

                    val rawMoney = CurrencyUtils.parseAmount(inputText)
                    if (rawMoney != null && rawMoney > 0) {
                        val formattedText = CurrencyUtils.formatAmount(rawMoney, isVND)
                        val cursorPosition =
                            formattedText.lastIndexOf('₫').takeIf { it != -1 }
                                ?: formattedText.length
                        val formattedValue = TextFieldValue(
                            text = formattedText,
                            selection = TextRange(cursorPosition)
                        )
                        onValueChange(formattedValue)
                        onFormatted?.invoke(formattedText, rawMoney)
                    } else {
                        onValueChange(newTextFieldValue)
                        onFormatted?.invoke(newTextFieldValue.text, null)
                    }
                } else {
                    // USD: Validate decimal places and allow natural typing
                    val newText = newTextFieldValue.text
                    val decimalIndex = newText.lastIndexOf('.')

                    // Update warning state
                    if (decimalIndex != -1) {
                        val decimalPart = newText.substring(decimalIndex + 1)
                        showDecimalWarning = decimalPart.length > 2

                        if (decimalPart.length > 2) {
                            // Don't update the text field if exceeding decimal limit
                            return@OutlinedTextField
                        }
                    } else {
                        showDecimalWarning = false
                    }

                    onValueChange(newTextFieldValue)
                    val parsedAmount = CurrencyUtils.parseAmount(newText)
                    onFormatted?.invoke(newText, parsedAmount)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    val wasFocused = isFocused
                    isFocused = focusState.isFocused

                    // USD formatting ONLY on focus loss
                    if (!isVND && wasFocused && !focusState.isFocused && value.text.isNotEmpty()) {
                        val parsed = CurrencyUtils.parseAmount(value.text)
                        if (parsed != null) {
                            val formattedText = CurrencyUtils.formatUSD(parsed)
                            val formattedValue = TextFieldValue(
                                text = formattedText,
                                selection = TextRange(formattedText.length)
                            )
                            onValueChange(formattedValue)
                            onFormatted?.invoke(formattedText, parsed)
                        }
                    }

                    // VND formatting on focus loss (for consistency)
                    if (isVND && wasFocused && !focusState.isFocused && value.text.isNotEmpty()) {
                        val parsed = CurrencyUtils.parseAmount(value.text)
                        if (parsed != null) {
                            val formattedText = CurrencyUtils.formatAmount(parsed, true)
                            val finalPosition = formattedText.lastIndexOf('₫').takeIf { it != -1 }
                                ?: formattedText.length
                            val formattedValue = TextFieldValue(
                                text = formattedText,
                                selection = TextRange(finalPosition)
                            )
                            onValueChange(formattedValue)
                            onFormatted?.invoke(formattedText, parsed)
                        }
                    }
                },
            label = null, // handled above
            placeholder = {
                Text(placeholder ?: defaultPlaceholder, color = Color(0xFFCCCCCC))
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (error != null || isError || (!isVND && showDecimalWarning)) Color.Red else Color(
                            0xFF00D09E
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null || isError || (!isVND && showDecimalWarning)) Color.Red else Color(
                    0xFF00D09E
                ),
                unfocusedBorderColor = if (error != null || isError || (!isVND && showDecimalWarning)) Color.Red else Color(
                    0xFFE0E0E0
                ),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = if (error != null || isError || (!isVND && showDecimalWarning)) Color.Red else Color(
                    0xFF00D09E
                ),
                errorBorderColor = Color.Red,
                errorContainerColor = Color.White,
                errorCursorColor = Color.Red,
                errorLeadingIconColor = Color.Red,
                errorTrailingIconColor = Color.Red
            ),
            singleLine = true,
            enabled = enabled,
            isError = error != null || isError || (!isVND && showDecimalWarning),
            supportingText = {
                when {
                    !isVND && showDecimalWarning -> {
                        Text(
                            text = stringResource(id = R.string.usd_decimal_warning),
                            color = Color.Red
                        )
                    }

                    error != null -> {
                        Text(error, color = Color.Red)
                    }

                    supportingText != null -> {
                        Text(supportingText)
                    }
                }
            }
        )
    }
}

/**
 * Preview component for USD input while typing (shows formatted preview)
 * Use this when isFocused = true and currency is USD
 */
@Composable
fun USDInputPreview(
    inputText: String,
    modifier: Modifier = Modifier
) {
    if (inputText.isNotEmpty()) {
        val parsed = CurrencyUtils.parseAmount(inputText)
        if (parsed != null) {
            val preview = CurrencyUtils.formatUSD(parsed)
            Text(
                text = stringResource(id = R.string.preview_label, preview),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = modifier
            )
        }
    }
}