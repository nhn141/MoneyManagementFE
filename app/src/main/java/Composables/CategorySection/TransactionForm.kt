package DI.Composables.CategorySection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TransactionForm() {
    var date by remember { mutableStateOf("March 24, 2025") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(TextFieldValue("$0")) }
    var title by remember { mutableStateOf(TextFieldValue("Dinner")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 25.dp)
    ) {
        TransactionTextField(label = "Transaction Code")
        TransactionTextField(label = "Account Number")
        TransactionTextField(label = "Bank")

        TransactionTextField(
            label = "Date",
            value = date,
            onValueChange = { date = it },
            isDropdown = true,
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TransactionTextField(label = "Category", value = category, onValueChange = { category = it }, isDropdown = true)
        TransactionTextField(label = "Amount", value = amount.text, onValueChange = { amount = TextFieldValue(it) })
        TransactionTextField(label = "Transaction Title", value = title.text, onValueChange = { title = TextFieldValue(it) })

        TransactionTextField(label = "Enter Message", value = message.text, onValueChange = { message = TextFieldValue(it) }, isMultiline = true)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C187)),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(45.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun TransactionTextField(
    label: String,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isDropdown: Boolean = false,
    isMultiline: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        readOnly = isDropdown,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

