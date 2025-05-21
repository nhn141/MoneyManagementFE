package DI.Models

import com.example.moneymanagement_frontend.R

sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Analysis : BottomNavItem("analysis", R.drawable.ic_analytics, "Analysis")
    object Transaction : BottomNavItem("transaction", R.drawable.ic_transaction, "Transaction")
    object Category : BottomNavItem("category", R.drawable.ic_category, "Category")
    object Profile : BottomNavItem("user", R.drawable.ic_user, "Profile")

    companion object {
        val allRoutes = listOf(Home, Analysis, Transaction, Category, Profile)
    }
}