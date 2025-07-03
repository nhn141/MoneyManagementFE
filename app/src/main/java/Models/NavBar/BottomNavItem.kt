package DI.Models.NavBar

import com.example.moneymanagement_frontend.R

sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Analysis : BottomNavItem("analysis", R.drawable.ic_analytics, "Analysis")
    object Transaction : BottomNavItem("transaction", R.drawable.ic_transaction, "Transaction")
    object Setting : BottomNavItem("user", R.drawable.ic_setting, "Profile")
    object NewsFeed : BottomNavItem("newsfeed", R.drawable.ic_newsfeed, "NewsFeed")

    companion object {
        val allRoutes = listOf(
            Home,
            Transaction,
            Analysis,
            Setting,
            NewsFeed,
        )
    }
}