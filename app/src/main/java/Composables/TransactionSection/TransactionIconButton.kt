package DI.Composables.TransactionSection

import DI.Composables.CategorySection.getIconResIdByName
import DI.Models.Category.CategoryIconStorage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TransactionIconButton(
    navController: NavController,
    transactionID: String,
    categoryID: String
) {
    val context = LocalContext.current
    val iconStorage = remember { CategoryIconStorage(context) }
    val iconKey = iconStorage.getIconKey(categoryID)
    val iconRes = getIconResIdByName(context, iconKey)

    Button(
        onClick = {
            navController.navigate("transaction_detail/$transactionID")
        },
        modifier = Modifier.size(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
        shape = CircleShape,
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


