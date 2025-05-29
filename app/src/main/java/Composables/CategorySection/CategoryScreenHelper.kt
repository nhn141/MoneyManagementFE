package DI.Composables.CategorySection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Modern color scheme
object ModernColors {
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val SurfaceVariant = Color(0xFFF1F5F9)
    val Primary = Color(0xFF6366F1)
    val PrimaryLight = Color(0xFF818CF8)
    val Secondary = Color(0xFF10B981)
    val SecondaryLight = Color(0xFF34D399)
    val Accent = Color(0xFFEC4899)
    val OnSurface = Color(0xFF0F172A)
    val OnSurfaceVariant = Color(0xFF64748B)
    val Error = Color(0xFFEF4444)
    val Success = Color(0xFF10B981)

    val primaryGradient = Brush.linearGradient(
        colors = listOf(Primary, PrimaryLight)
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(Color.White, Color(0xFFFAFBFC))
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
    )
}

// Enhanced icon mapping
fun getCategoryIcon(categoryName: String): ImageVector {
    val lowerName = categoryName.lowercase()

    val keywordIconMap = mapOf(
        listOf("electronics", "phone", "android") to Icons.Default.PhoneAndroid,
        listOf("clothing", "fashion") to Icons.Default.Checkroom,
        listOf("food", "cream", "grocery", "dining", "restaurant") to Icons.Default.Restaurant,
        listOf("books", "education", "study") to Icons.Default.MenuBook,
        listOf("sports", "fitness", "gym") to Icons.Default.FitnessCenter,
        listOf("travel", "vacation", "flight") to Icons.Default.Flight,
        listOf("music", "entertainment", "song") to Icons.Default.MusicNote,
        listOf("health", "medical", "hospital", "doctor") to Icons.Default.LocalHospital,
        listOf("home", "furniture", "house") to Icons.Default.Home,
        listOf("automotive", "cars", "car", "transport", "vehicle") to Icons.Default.DirectionsCar,
        listOf("beauty", "cosmetics", "makeup") to Icons.Default.Face,
        listOf("gaming", "games", "game") to Icons.Default.SportsEsports,
        listOf("business", "work", "office") to Icons.Default.Business,
        listOf("technology", "tech", "computer", "laptop") to Icons.Default.Computer,
        listOf("art", "creative", "design") to Icons.Default.Palette,
        listOf("photography", "camera", "photo") to Icons.Default.CameraAlt,
        listOf("shopping", "shop", "market") to Icons.Default.ShoppingCart,
        listOf("finance", "money", "bank") to Icons.Default.AccountBalance,
        listOf("social", "friends", "group") to Icons.Default.Group,
        listOf("tools", "build", "repair") to Icons.Default.Build
    )

    return keywordIconMap.entries.firstOrNull { (keywords, _) ->
        keywords.any { keyword -> lowerName.contains(keyword) }
    }?.value ?: Icons.Default.Category
}