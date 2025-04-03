package Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanagement_frontend.R
import androidx.navigation.NavController

@Composable
fun HeaderSection(balanceInfo: BalanceInfo, navController: NavController) {
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                text = "Categories",
                color = Color.Black,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = { /* Handle Notifications click */ }
                    )
                    .size(40.dp)
                    .background(Color(0xFF53dba9))
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFF53dba9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = "Notifications Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_total_balance),
                                contentDescription = "Total Balance Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        Text(
                            text = "Total Balance",
                            color = Color.Black,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${balanceInfo.totalBalance}",
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_total_expense),
                                contentDescription = "Total Expense Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        Text(
                            text = "Total Expense",
                            color = Color.Black,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${balanceInfo.totalExpense}",
                        color = Color(0xFF008DDD),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            CustomProgressBar(0.3f, "$20,000.00")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tick),
                        contentDescription = "Tick Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "30% Of Your Expenses, Looks Good.",
                    color = Color.Black,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp
                )
            }
        }
    }
}

