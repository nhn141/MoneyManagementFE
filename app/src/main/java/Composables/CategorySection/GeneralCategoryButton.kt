package DI.Composables.CategorySection

import DI.Composables.TransactionSection.getCategoryIcon
import DI.Models.Category.Category
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanagement_frontend.R
import androidx.navigation.NavController


@Composable
fun GeneralCategoryButton(
    category: Category,
    onClick: () -> Unit
) {
    val iconRes = getCategoryIcon(category.name)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFF6F9FF),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0068FF)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = category.name,
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

