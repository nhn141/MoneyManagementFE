package DI.Composables.Currency

import DI.ViewModels.CurrencyConverterViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CurrencyConverterScreen(viewModel: CurrencyConverterViewModel) {
    var amountInput by remember { mutableStateOf("") }
    var usdToVnd by remember { mutableStateOf(true) }

    val rate by viewModel.exchangeRate.collectAsState()

    val result = remember(amountInput, usdToVnd, rate) {
        val input = amountInput.toDoubleOrNull()
        if (input != null && rate != null) {
            viewModel.convert(input, usdToVnd)
        } else null
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Chuyển đổi USD ↔ VND", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            label = { Text("Số tiền") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Chiều chuyển đổi:")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = usdToVnd, onClick = { usdToVnd = true })
            Text("USD ➜ VND")
            Spacer(Modifier.width(16.dp))
            RadioButton(selected = !usdToVnd, onClick = { usdToVnd = false })
            Text("VND ➜ USD")
        }

        Spacer(Modifier.height(16.dp))

        if (rate == null) {
            Text("Đang tải tỷ giá...", color = MaterialTheme.colorScheme.primary)
        } else {
            Text(
                "Kết quả: ${result?.let { String.format("%.2f", it) } ?: "---"} ${if (usdToVnd) "VND" else "USD"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
