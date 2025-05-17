package DI.Composables.TransactionSection

import DI.Composables.CategorySection.DropdownSelector
import DI.Composables.CategorySection.GeneralTemplate
import DI.Composables.CategorySection.TransactionTextField
import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Wallet
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import DI.ViewModels.WalletViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale


@Composable
fun TransactionEditScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionScreenViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    walletViewModel: WalletViewModel = hiltViewModel()
) {
    val selectedTransaction by viewModel.selectedTransaction
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        categoryViewModel.getCategories()
        walletViewModel.fetchWallets()
        viewModel.loadTransactionById(transactionId) {
            isLoaded = it
        }
    }

    GeneralTemplate(
        contentHeader = {
            TransactionEditHeader(navController = navController)
        },
        contentBody = {
            if (isLoaded && selectedTransaction != null && categories != null && wallets != null) {
                val categoryList = categories?.getOrNull() ?: emptyList()
                val walletList = wallets?.getOrNull() ?: emptyList()

                TransactionEditBody(
                    transaction = selectedTransaction!!,
                    categoryList = categoryList,
                    walletList = walletList,
                    viewModel = viewModel,
                    navController = navController
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    )
}




@Composable
fun TransactionEditHeader(
    navController: NavController,
) {
    Column(
        modifier = Modifier.background(Color(0xFF53DBA9)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = { navController.popBackStack() })
            )

            Text(
                text = "Edit Transaction",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionEditBody(
    transaction: Transaction,
    categoryList: List<Category>,
    walletList: List<Wallet>,
    viewModel: TransactionScreenViewModel,
    navController: NavController
) {
    val dateFormatStorage = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    val dateFormatDisplay = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    }

    val selectedDateTime = remember {
        mutableStateOf<Calendar?>(
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(transaction.transactionDate)
                ?.let {
                    Calendar.getInstance().apply { time = it }
                }
        )
    }

    val displayDate by remember {
        derivedStateOf {
            selectedDateTime.value?.let { dateFormatDisplay.format(it.time) } ?: ""
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var categoryId by remember { mutableStateOf(transaction.categoryID) }
    var categoryName by remember {
        mutableStateOf(categoryList.find { it.categoryID == transaction.categoryID }?.name ?: "")
    }

    var walletId by remember { mutableStateOf(transaction.walletID) }
    var walletName by remember {
        mutableStateOf(walletList.find { it.walletID == transaction.walletID }?.walletName ?: "")
    }

    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var title by remember { mutableStateOf(transaction.description) }
    var type by remember { mutableStateOf(transaction.type) }
    val isIncome = type.equals("income", ignoreCase = true)
    val isExpense = type.equals("expense", ignoreCase = true)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(if (isIncome) Color(0xFF1DC418) else Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .clickable { type = "Income" }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("INCOME", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .background(if (isExpense) Color(0xFFC62828) else Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .clickable { type = "Expense" }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("EXPENSE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        TransactionTextField(
            label = "Title",
            value = title,
            onValueChange = { title = it }
        )

        TransactionTextField(
            label = "Amount",
            value = amount,
            onValueChange = { amount = it }
        )

        DropdownSelector(
            label = "Category",
            selectedName = categoryName,
            options = categoryList.map { it.name to it.categoryID }
        ) { name, id ->
            categoryName = name
            categoryId = id
        }

        DropdownSelector(
            label = "Wallet",
            selectedName = walletName,
            options = walletList.map { it.walletName to it.walletID }
        ) { name, id ->
            walletName = name
            walletId = id
        }

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
                positiveButton("Next") { timeDialogState.show() }
                negativeButton("Cancel")
            }
        ) {
            datepicker(
                initialDate = selectedDateTime.value?.let {
                    LocalDate.of(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH))
                } ?: LocalDate.now(),
                title = "Select a date"
            ) { localDate ->
                val currentCal = selectedDateTime.value ?: Calendar.getInstance()
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = currentCal.timeInMillis
                    set(Calendar.YEAR, localDate.year)
                    set(Calendar.MONTH, localDate.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
                }
                selectedDateTime.value = newCal
            }
        }

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton("OK")
                negativeButton("Cancel")
            }
        ) {
            timepicker(
                initialTime = selectedDateTime.value?.let {
                    LocalTime.of(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
                } ?: LocalTime.now(),
                title = "Select a time"
            ) { time ->
                val currentCal = selectedDateTime.value ?: Calendar.getInstance()
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = currentCal.timeInMillis
                    set(Calendar.HOUR_OF_DAY, time.hour)
                    set(Calendar.MINUTE, time.minute)
                    set(Calendar.SECOND, 0)
                }
                selectedDateTime.value = newCal
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val updatedTransaction = transaction.copy(
                    description = title,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    categoryID = categoryId,
                    walletID = walletId,
                    type = type,
                    transactionDate = selectedDateTime.value?.let {
                        dateFormatStorage.format(it.time)
                    } ?: ""
                )

                viewModel.updateTransaction(
                    transactionID = updatedTransaction.transactionID,
                    amount = updatedTransaction.amount,
                    description = updatedTransaction.description,
                    categoryId = updatedTransaction.categoryID,
                    walletId = updatedTransaction.walletID,
                    type = updatedTransaction.type,
                    transactionDate = updatedTransaction.transactionDate
                ) { success ->
                    if (success) {
                        Toast.makeText(context, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Failed to update transaction", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

