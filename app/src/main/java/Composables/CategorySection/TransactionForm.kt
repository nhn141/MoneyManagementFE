package DI.Composables.CategorySection

import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import DI.ViewModels.WalletViewModel
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.text.NumberFormat
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionForm(viewModel: TransactionScreenViewModel,
                    navController: NavController,
                    type: String,
                    onTypeChange: (String) -> Unit) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val walletViewModel: WalletViewModel = hiltViewModel()
    val context = LocalContext.current
    val categoriesResult by categoryViewModel.categories.collectAsState()
    val walletsResult by walletViewModel.wallets.collectAsState()


    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.fetchWallets()
    }

    val selectedDateTime = remember { mutableStateOf(Calendar.getInstance()) }

    val storageFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }
    val displayFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    }

    val displayDate by remember {
        derivedStateOf { displayFormatter.format(selectedDateTime.value.time) }
    }

    val storageDate by remember {
        derivedStateOf { storageFormatter.format(selectedDateTime.value.time) }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var categoryId by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf("") }
    var walletName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    fun formatAmount(input: String): String {
        return input.toLongOrNull()?.let {
            NumberFormat.getNumberInstance(Locale.US).format(it)
        } ?: input
    }

    fun unformatAmount(formatted: String): String {
        return formatted.replace(",", "").replace(".", "")
    }

    var rawAmount by remember { mutableStateOf("") }

    val formattedAmount by remember(rawAmount) {
        mutableStateOf(formatAmount(rawAmount))
    }


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
            value = formattedAmount,
            onValueChange = {
                rawAmount = unformatAmount(it.filter { char -> char.isDigit() })
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    rawAmount += "000"
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("[000]", color = Color.Black)
            }
        }

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
            value = displayDate,
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
                positiveButton("Next") {
                    timeDialogState.show()
                }
                negativeButton("Cancel")
            },
            backgroundColor = Color(0xFFF1FFF3)
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Select a date"
            ) { localDate ->
                val newDate = selectedDateTime.value.clone() as Calendar
                newDate.set(Calendar.YEAR, localDate.year)
                newDate.set(Calendar.MONTH, localDate.monthValue - 1)
                newDate.set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
                selectedDateTime.value = newDate
            }
        }
        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton("OK")
                negativeButton("Cancel")
            },
            backgroundColor = Color(0xFFF1FFF3)
        ) {
            timepicker(
                initialTime = LocalTime.now(),
                title = "Select a time"
            ) { time ->
                val newTime = selectedDateTime.value.clone() as Calendar
                newTime.set(Calendar.HOUR_OF_DAY, time.hour)
                newTime.set(Calendar.MINUTE, time.minute)
                newTime.set(Calendar.SECOND, 0)
                selectedDateTime.value = newTime
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val parsedAmount = unformatAmount(formattedAmount).toDoubleOrNull()
                if (parsedAmount != null) {
                    viewModel.createTransaction(
                        amount = parsedAmount,
                        description = title,
                        categoryId = categoryId,
                        walletId = walletId,
                        type = type,
                        transactionDate = storageDate
                    ) { success ->
                        if (success) {
                            Toast.makeText(context, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Failed to save transaction", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
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

