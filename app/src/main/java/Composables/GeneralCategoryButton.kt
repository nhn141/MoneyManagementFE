package Composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanagement_frontend.R
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun CategoryButton(category: Category, navController: NavController) {
    val imageRes = when (category.name) {
        "Food" -> R.drawable.ic_food
        "Transport" -> R.drawable.ic_transport
        "Medicine" -> R.drawable.ic_medicine
        "Groceries" -> R.drawable.ic_groceries
        "Rent" -> R.drawable.ic_rent
        "Gifts" -> R.drawable.ic_gifts
        "Savings" -> R.drawable.ic_savings
        "Entertainment" -> R.drawable.ic_entertainment
        else -> R.drawable.ic_more
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        Button(
            onClick = { navController.navigate("category_specific_type") },
            modifier = Modifier
                .size(100.dp)
                .padding(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
            shape = MaterialTheme.shapes.medium
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = category.name,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = category.name,
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}