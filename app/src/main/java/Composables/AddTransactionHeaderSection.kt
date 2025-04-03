package Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun AddTransactionHeaderSection(navController: NavController) {
    var isIncome by remember { mutableStateOf(true) }

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
                .padding(18.dp)
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
            Row(
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { isIncome = !isIncome },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Income",
                    color = if (isIncome) Color(0xFF4CAF50) else Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Expense",
                    color = if (!isIncome) Color(0xFFF44336) else Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clickable { }
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notifications),
                    contentDescription = "notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
