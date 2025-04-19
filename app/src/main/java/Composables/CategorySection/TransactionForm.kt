package DI.Composables.CategorySection

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionForm() {
    val dateDialogState = rememberMaterialDialogState()
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

        // 🔽 Date field with picker
        TransactionTextField(
            label = "Date",
            value = date,
            onValueChange = {},
            isDropdown = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    modifier = Modifier.clickable { dateDialogState.show() }
                )
            }
        )

        // 📅 Date Picker Dialog
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton("OK")
                negativeButton("Cancel")
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Select a date"
            ) {
                date = it.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) // Ví dụ: April 19, 2025
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TransactionTextField(label = "Category", value = category, onValueChange = { category = it }, isDropdown = true)
        TransactionTextField(label = "Amount", value = amount.text, onValueChange = { amount = TextFieldValue(it) })
        TransactionTextField(label = "Transaction Title", value = title.text, onValueChange = { title = TextFieldValue(it) })

        TransactionTextField(
            label = "Enter Message",
            value = message.text,
            onValueChange = { message = TextFieldValue(it) },
            isMultiline = true
        )

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
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            readOnly = isDropdown,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = if (isMultiline) 4 else 1,
            singleLine = !isMultiline,
            enabled = true
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreen(navController: NavController) {
    GeneralTemplate(
        contentHeader = { AddTransactionHeaderSection(navController) },
        contentBody = { TransactionForm() },
        fraction = 0.14f,
    )
}





@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    val navController = rememberNavController()
    AddTransactionScreen(navController = navController)
}
