package DI.Composables.TransactionSection

import DI.Models.Category.Category
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun TransactionIconButton(navController: NavController,
                          transactionID: String,
                          categoryID: String,
                          categories: List<Category>) {
    val categoryName = categories.find { it.categoryID == categoryID }?.name ?: "Other"
    val iconRes = getCategoryIcon(categoryName)

    Button(
        onClick = {
            navController.navigate("transaction_detail/$transactionID")
        },
        modifier = Modifier.size(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

fun getCategoryIcon(categoryName: String): Int {
    return when (categoryName.trim().lowercase()) {
        "savings" -> R.drawable.ic_savings
        "medicine" -> R.drawable.ic_medicine
        "groceries" -> R.drawable.ic_groceries
        "rent" -> R.drawable.ic_rent
        "transport" -> R.drawable.ic_transport
        "food" -> R.drawable.ic_food
        "entertainment" -> R.drawable.ic_entertainment
        else -> R.drawable.ic_more
    }
}

