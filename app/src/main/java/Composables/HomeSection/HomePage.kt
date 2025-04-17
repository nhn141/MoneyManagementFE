package DI.Composables.HomeSection

import DI.Composables.CategorySection.CustomProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun HomePageHeaderSection(navController: NavController) {
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        ) {
            Column {
                Text(
                    text = "Hi, Welcome Back",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Good Morning",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = { }
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
                .padding(20.dp),
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
                        text = "-\$1,187.40",
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
