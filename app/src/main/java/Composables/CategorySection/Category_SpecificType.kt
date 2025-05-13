package DI.Composables.CategorySection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.moneymanagement_frontend.R


data class TransactionItem (val title: String, val timestamp: String, val amount: String)
fun getTransactionData() : List<TransactionItem> {
    return listOf(
        TransactionItem("Dinner", "18:27 - April 30", "-$26,00"),
        TransactionItem("Dinner", "18:27 - April 30", "-$26,00"),
        TransactionItem("Dinner", "18:27 - April 30", "-$26,00"),
        TransactionItem("Dinner", "18:27 - April 30", "-$26,00"),
        TransactionItem("Delivery Pizza", "15:00 - April 24", "-$18,35"),
        TransactionItem("Lunch", "12:30 - April 15", "-$15,40"),
        TransactionItem("Brunch", "9:30 - April 08", "-$12,13"),
        TransactionItem("Dinner", "20:50 - March 31", "-$27,20")
    )
}

@Composable
fun Category_SpecificType_Body(navController: NavController) {
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

                // 🔹 April Section
                MonthSection("April")
                Spacer(modifier = Modifier.height(5.dp))
                TransactionList(getTransactionData().filter { it.timestamp.contains("April") })


                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 March Section
                MonthSection("March")
                Spacer(modifier = Modifier.height(5.dp))
                TransactionList(getTransactionData().filter { it.timestamp.contains("March") })
            }

            Spacer(modifier = Modifier.height(8.dp))
            AddTransactionsButton(navController)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(28.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFF00D09E))
                .size(35.dp)

        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = "Calendar",
                modifier = Modifier.align(Alignment.Center),
                tint = Color.Black.copy(alpha = 0.8f)
            )
        }

    }

}

@Composable
fun MonthSection(month: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = month,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun TransactionList(transactions: List<TransactionItem>) {
    Column {
        transactions.forEach { transaction ->
            TransactionRow(transaction)
        }
    }
}

@Composable
fun TransactionRow(transaction: TransactionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Circle Icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF5B93FF)), // Blue color
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_food),
                contentDescription = "Food Icon",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 🔹 Transaction Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = transaction.timestamp,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // 🔹 Transaction Amount
        Text(
            text = transaction.amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E88E5) // Blue color
        )
    }
}

@Composable
fun AddTransactionsButton(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 45.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF00D09E))
            .clickable { navController.navigate("add_transaction") },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add Transaction",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}


@Composable
fun Category_SpecificType_Header(navController: NavController) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // Removes the ripple effect
                        onClick = {
                            navController.popBackStack()
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Transaction",
                color = Color.Black,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // Removes the ripple effect
                        onClick = { }
                    )
                    .size(30.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_circle_notifications),
                    contentDescription = "notifications",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            )
            {
                Column {
                    Text(
                        text = "Total Balance",
                        color = Color.Black,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$7,783.00",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.8f)
                        .background(Color.White)

                )

                Column {
                    Text(
                        text = "Total Transaction",
                        color = Color.Black,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "-$1,187.40",
                        color = Color(0xFF1E88E5),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            CustomProgressBar(0.3f, "$20,000.00");

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "30% Of Your Incomes, Looks Good.",
                color = Color.Black,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CustomProgressBar(progress: Float, totalAmount: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(30.dp)
    ) {
        // 🔹 Background (Full Bar - White)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFEFF7EC)) // Light Greenish-White Background
        )

        // 🔹 Foreground (Progress Bar - Dark)
        Box(
            modifier = Modifier
                .fillMaxWidth(progress) // Set progress width dynamically
                .height(30.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF131E1F)) // Dark Color
        )

        // 🔹 Progress Text inside the Bar
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .padding(start = 24.dp) // Add padding inside the progress bar
                .align(Alignment.CenterStart) // Align to start of progress
        )

        // 🔹 Total Amount Text at the End
        Text(
            text = totalAmount,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier
                .padding(end = 24.dp)
                .align(Alignment.CenterEnd) // Align to end of the bar
        )
    }
}
