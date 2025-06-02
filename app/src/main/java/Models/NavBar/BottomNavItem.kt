package DI.Models.NavBar

import com.example.moneymanagement_frontend.R

sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Analysis : BottomNavItem("analysis", R.drawable.ic_analytics, "Analysis")
    object Transaction : BottomNavItem("transaction", R.drawable.ic_transaction, "Transaction")
    object Category : BottomNavItem("category", R.drawable.ic_category, "Category")
    object Profile : BottomNavItem("user", R.drawable.ic_setting, "Profile")
    object Friend : BottomNavItem("friend", R.drawable.ic_friend, "Friend")
    object Chat : BottomNavItem("chat", R.drawable.ic_chat, "Chat")
    object Wallet : BottomNavItem("wallet", R.drawable.ic_wallet, "Wallet")

    companion object {
        val allRoutes = listOf(Transaction, Category, Wallet, Analysis, Profile, Friend, Chat)
    }
}