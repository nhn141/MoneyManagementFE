package DI.Composables.CategorySection

import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import DI.ViewModels.WalletViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionForm(viewModel: TransactionScreenViewModel,
                    navController: NavController,
                    type: String,
                    onTypeChange: (String) -> Unit) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val walletViewModel: WalletViewModel = hiltViewModel()

    val categoriesResult by categoryViewModel.categories.collectAsState()
    val walletsResult by walletViewModel.wallets.collectAsState()

    // Trigger load
    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.fetchWallets()
    }

    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    var date by remember { mutableStateOf(formatter.format(Calendar.getInstance().time)) }

    var categoryId by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf("") }
    var walletName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    val dateDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 25.dp)
    ) {
        DropdownSelector(
            label = "Wallet",
            selectedName = walletName,
            options = walletsResult?.getOrNull()?.map { it.walletName to it.walletID } ?: emptyList(),
            onSelect = { name, id ->
                walletName = name
                walletId = id
            }
        )

        DropdownSelector(
            label = "Category",
            selectedName = categoryName,
            options = categoriesResult?.getOrNull()?.map { it.name to it.categoryID } ?: emptyList(),
            onSelect = { name, id ->
                categoryName = name
                categoryId = id
            }
        )

        TransactionTextField(
            label = "Amount",
            value = amount,
            onValueChange = { amount = it }
        )

        TransactionTextField(
            label = "Title",
            value = title,
            onValueChange = { title = it }
        )
        TransactionTextField(
            label = "Type",
            value = type,
            onValueChange = onTypeChange,
            isDropdown = true
        )

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

        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton("OK")
                negativeButton("Cancel")
            },
            backgroundColor = Color(0xFFF1FFF3)
        ) {
            datepicker(
                title = "Select a date",
                colors = DatePickerDefaults.colors(
                    headerBackgroundColor = Color(0xFF00D09E)
                )
            ) { localDate ->
                val calendar = Calendar.getInstance()
                calendar.set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth)
                date = formatter.format(calendar.time)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null) {
                    viewModel.createTransaction(
                        amount = parsedAmount,
                        description = title,
                        categoryId = categoryId,
                        walletId = walletId,
                        type = type,
                        transactionDate = date
                    ) { success ->
                        if (success) navController.popBackStack()
                    }
                }
            },
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

@Composable
fun DropdownSelector(
    label: String,
    selectedName: String,
    options: List<Pair<String, String>>,
    onSelect: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (name, id) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(name, id)
                        expanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreen(navController: NavController) {
    val viewModel: TransactionScreenViewModel = hiltViewModel()
    var type by remember { mutableStateOf("Expense") }
    GeneralTemplate(
        contentHeader = { AddTransactionHeaderSection(navController, type) { newType -> type = newType } },
        contentBody = { TransactionForm(viewModel, navController, type, onTypeChange = { type = it }) },
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
