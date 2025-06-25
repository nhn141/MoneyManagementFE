package DI.Composables.HomeSection

import DI.Composables.TransactionSection.GeneralTransactionItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

// Theme Colors
object MoneyAppColors {
    val Primary = Color(0xFF10B981) // Emerald-500
    val PrimaryVariant = Color(0xFF059669) // Emerald-600
    val Secondary = Color(0xFF34D399) // Emerald-400
    val Background = Color(0xFFF0FDF4) // Green-50
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color.White
    val OnSurface = Color(0xFF1F2937) // Gray-800
    val OnBackground = Color(0xFF424242)
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
}

data class UserProfile(
    val name: String,
    val avatarUrl: String,
    val avatarVersion: String,
    val currentDate: String
)

data class SocialNotification(
    val friendRequests: Int,
    val unreadMessages: Int,
    val recentActivity: String
)

data class FinancialOverview(
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double
)

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val destination: String
)

fun parseDateTime(timestamp: String?): LocalDateTime? {
    if (timestamp.isNullOrBlank()) return null

    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTime.parse(timestamp, formatter)
    } catch (e: Exception) {
        null
    }
}

fun parseMonth(timestamp: String?): Month? {
    return parseDateTime(timestamp)?.month
}

fun getMostRecentTransactions(
    transactions: List<GeneralTransactionItem>,
    limit: Int
): List<GeneralTransactionItem> {
    return transactions
        .sortedByDescending { parseDateTime(it.timestamp) ?: LocalDateTime.MIN }
        .take(limit)
}

