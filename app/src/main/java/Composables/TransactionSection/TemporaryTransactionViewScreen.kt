package DI.Composables.TransactionSection

import DI.API.TokenHandler.AuthStorage
import DI.Composables.CategorySection.getCategoryIcon
import DI.Models.Chat.LatestChat
import DI.Models.Group.Group
import DI.Models.NewsFeed.Post
import DI.Utils.CurrencyUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moneymanagement_frontend.R

@Composable
fun TemporaryTransactionViewScreen(
    navController: NavController,
    messageContent: String // Truyền nội dung tin nhắn chứa thông tin giao dịch
) {
    var transactionDetails by remember { mutableStateOf<Map<String, String>?>(null) }

    // Trích xuất thông tin từ messageContent
    LaunchedEffect(messageContent) {
        val details = mutableMapOf<String, String>()
        val lines = messageContent.lines()
        lines.forEach { line ->
            when {
                line.startsWith("Title:") -> details["title"] = line.removePrefix("Title: ").trim()
                line.startsWith("Type:") -> details["type"] = line.removePrefix("Type: ").trim()
                line.startsWith("Amount:") -> details["amount"] = line.removePrefix("Amount: ").trim()
                line.startsWith("Date:") -> details["date"] = line.removePrefix("Date: ").trim()
                line.startsWith("Category:") -> details["category"] = line.removePrefix("Category: ").trim()
                line.startsWith("Wallet:") -> details["wallet"] = line.removePrefix("Wallet: ").trim()
            }
        }
        transactionDetails = details
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA5DABC),
                        Color(0xFF87EAAF),
                        Color(0xFF00BE7C).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        TemporaryTransactionHeader(navController = navController)

        if (transactionDetails != null) {
            TemporaryTransactionBody(transactionDetails = transactionDetails!!)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun TemporaryTransactionHeader(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Text(
            text = "Shared Transaction",
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748)
        )
    }
}

@Composable
fun TemporaryTransactionBody(
    transactionDetails: Map<String, String>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val type = transactionDetails["type"]?.lowercase()
                    val typeColor = if (type == "income") Color(0xFF48BB78) else Color(0xFFE53E3E)
                    val typeIcon = if (type == "income") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                typeColor.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = type,
                            tint = typeColor,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = (transactionDetails["type"] ?: "Unknown").uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = transactionDetails["amount"] ?: "0",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Transaction Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DetailItem(
                        icon = Icons.Default.Title,
                        label = "Title",
                        value = transactionDetails["title"] ?: "Unknown",
                        iconTint = Color(0xFF667eea)
                    )

                    DetailItem(
                        icon = Icons.Default.Category,
                        label = "Type",
                        value = transactionDetails["type"] ?: "Unknown",
                        iconTint = Color(0xFF9F7AEA)
                    )

                    DetailItem(
                        icon = Icons.Default.Money,
                        label = "Amount",
                        value = transactionDetails["amount"] ?: "Unknown",
                        iconTint = Color(0xFF38B2AC)
                    )

                    DetailItem(
                        icon = Icons.Default.DateRange,
                        label = "Date",
                        value = transactionDetails["date"] ?: "Unknown",
                        iconTint = Color(0xFFED8936)
                    )

                    DetailItem(
                        icon = getCategoryIcon(transactionDetails["category"] ?: ""),
                        label = "Category",
                        value = transactionDetails["category"] ?: "Unknown",
                        iconTint = Color(0xFF9F7AEA)
                    )

                    DetailItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        label = "Wallet",
                        value = transactionDetails["wallet"] ?: "Unknown",
                        iconTint = Color(0xFF38B2AC)
                    )
                }
            }
        }
    }
}