package DI.Composables.TransactionSection

import DI.Composables.CategorySection.AddTransactionsButton
import DI.Composables.CategorySection.GeneralTemplate
import DI.Composables.CategorySection.MonthSection
import DI.Composables.CategorySection.TransactionList
import DI.Composables.CategorySection.getTransactionData
import DI.Composables.TransactionSection.GeneralTransactionRow
import DI.Composables.TransactionSection.GeneralTransactionSummary
import DI.ViewModels.TransactionViewModel
import Screens.TransactionScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController

@Composable
fun TransactionPageScreen(navController: NavController) {
    GeneralTemplate(
        contentHeader = { TransactionHeaderSection(navController) },
        contentBody = { TransactionBodySection(navController) },
        fraction = 0.35f
    )
}

data class GeneralTransactionItem (val icon: Int, val title: String, val timestamp: String, val type: String, val amount: String, val isIncome: Boolean)
fun getGeneralTransactionData() : List<GeneralTransactionItem> {
    return listOf(
        GeneralTransactionItem(R.drawable.ic_total_expense, "Salary", "18:27 - April 30", "Monthly", "$26,00", true),
        GeneralTransactionItem(R.drawable.ic_groceries, "Groceries", "18:27 - April 30", "Pantry", "-$100,00", false),
        GeneralTransactionItem(R.drawable.ic_rent, "Rent", "18:27 - April 30", "Rent", "-$674,00", false),
        GeneralTransactionItem(R.drawable.ic_transport, "Transport", "18:27 - April 30", "Fuel", "-$4,00", false),
        GeneralTransactionItem(R.drawable.ic_food, "Food", "20:50 - March 31", "Dinner", "-$70,20", false)
    )
}

@Composable
fun TransactionHeaderSection(navController: NavController) {
    val viewModel: TransactionViewModel = hiltViewModel()
    val selected = viewModel.selectedType.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF53dba9))
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== Top Bar =====
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.popBackStack()
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Title
            Text(
                text = "Transaction",
                color = Color.Black,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            // Notification icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { /* Handle Notifications */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notifications),
                    contentDescription = "Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Total Balance =====
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FFF3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$7,783.00",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1F2D)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Income + Expense =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Income Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == "Income") Color(0xFF0068FF) else Color(0xFFF1FFF3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .clickable { viewModel.onTypeSelected("Income") }

            ) {
                Column(
                    modifier = Modifier.fillMaxSize() .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_income),
                        contentDescription = "Income",
                        tint = if (selected == "Income") Color.White else Color(0xFF00D09E),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected == "Income") Color.White else Color.Black,
                    )
                    Text(
                        text = "$4,120.00",
                        fontWeight = FontWeight.Bold,
                        color = if (selected == "Income") Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Expense Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == "Expense") Color(0xFF0068FF) else Color(0xFFF1FFF3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .clickable { viewModel.onTypeSelected("Expense") }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize() .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_expense),
                        contentDescription = "Expense",
                        tint = if (selected == "Expense") Color.White else Color(0xFF0068FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Expense",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected == "Expense") Color.White else Color.Black
                    )
                    Text(
                        text = "$1,187.40",
                        fontWeight = FontWeight.Bold,
                        color = if (selected == "Expense") Color.White else Color(0xFF0068FF)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionBodySection(navController: NavController) {
    val showDatePicker = remember { mutableStateOf(false) }
    val viewModel: TransactionViewModel = hiltViewModel()
    val transactionsByMonth = viewModel.filteredTransactions.value

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 25.dp, top = 25.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(7.dp))

                transactionsByMonth.forEach { (month, items) ->
                    MonthSection(month)
                    Spacer(modifier = Modifier.height(5.dp))
                    GeneralTransactionSummary(transactions = items)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(28.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔘 Nút Add Transaction
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xFF00D09E))
                        .size(30.dp)
                        .clickable {
                            navController.navigate("add_transaction")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "Add Transaction",
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 📅 Nút Calendar
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xFF00D09E))
                        .size(30.dp)
                        .clickable {
                            showDatePicker.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

    }
}


@Composable
fun GeneralTransactionRow(
    icon: Int,
    title: String,
    time: String,
    type: String,
    amount: String,
    isIncome: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon bên trái
        Button(
            onClick = {},
            modifier = Modifier.size(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        // Thông tin chính
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF0068FF)
            )
        }

        VerticalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            color = Color(0xFFB0F3D3)
        )

        Text(
            text = type,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .weight(0.6f)
                .padding(horizontal = 8.dp)
        )

        VerticalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            color = Color(0xFFB0F3D3)
        )

        Column(
            modifier = Modifier
                .padding(start = 12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                color = if (isIncome) Color.Black else Color(0xFF0080FF),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}




@Composable
fun GeneralTransactionSummary(transactions: List<GeneralTransactionItem>) {
    Column {
        transactions.forEach { transaction ->
            GeneralTransactionRow(
                icon = transaction.icon,
                title = transaction.title,
                time = transaction.timestamp,
                type = transaction.type,
                amount = transaction.amount,
                isIncome = transaction.isIncome
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun TransactionPageScreenPreview() {
    TransactionPageScreen(navController = rememberNavController())
}


