package DI.Composables.TransactionSection

import DI.Composables.CategorySection.GeneralTemplate
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionScreenViewModel
import DI.ViewModels.WalletViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TransactionDetailScreen(
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
            TransactionDetailHeader(
                navController = navController,
                onEditClick = {
                    if (isLoaded && selectedTransaction != null) {
                        navController.navigate("transaction_edit/${selectedTransaction!!.transactionID}")
                    }
                }
            )
        },
        contentBody = {
            if (isLoaded && selectedTransaction != null && categories != null && wallets != null) {
                val generalTransaction = selectedTransaction!!.toGeneralTransactionItem()

                val categoryList = categories?.getOrNull() ?: emptyList()
                val walletList = wallets?.getOrNull() ?: emptyList()

                val category = categoryList.find { it.categoryID == generalTransaction.categoryID }
                val wallet = walletList.find { it.walletID == generalTransaction.walletID }

                TransactionDetailBody(
                    navController = navController,
                    title = generalTransaction.title,
                    categoryName = category?.name ?: "Unknown",
                    amount = generalTransaction.amount,
                    walletName = wallet?.walletName ?: "Unknown",
                    date = generalTransaction.timestamp ?: "Unknown",
                    type = if (generalTransaction.isIncome) "Income" else "Expense",
                    transactionId = transactionId,
                    viewModel = viewModel
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        },
        fraction = 0.14f
    )
}




@Composable
fun TransactionDetailHeader(
    navController: NavController,
    onEditClick: () -> Unit
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
                text = "Transaction Detail",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onEditClick)
            )
        }
    }
}

@Composable
fun TransactionDetailBody(
    navController: NavController,
    title: String,
    categoryName: String,
    amount: String,
    walletName: String,
    date: String,
    type: String,
    transactionId: String,
    viewModel: TransactionScreenViewModel
) {
    val typeColor = if (type.equals("Income", ignoreCase = true)) Color(0xFF1DC418) else Color(0xFFC62828)
    var showDeleteDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = type.uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = typeColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            DetailItem(label = "Title", value = title)
            DetailItem(label = "Category", value = categoryName)
            DetailItem(label = "Amount", value = amount)
            DetailItem(label = "Wallet", value = walletName)
            DetailItem(label = "Date", value = date)

            Spacer(modifier = Modifier.height(80.dp)) // Để tránh bị che bởi FAB
        }

        FloatingActionButton(
            onClick = {
                showDeleteDialog = true
            },
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
            )
        }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Delete Transaction",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete this transaction? This action cannot be undone.",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteTransaction(transactionId) { success ->
                                if (success) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Transaction deleted successfully")
                                        navController.popBackStack()
                                    }
                                }

                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteDialog = false },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
        }
    }
}

