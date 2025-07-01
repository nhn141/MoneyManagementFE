package DI.Models.NavBar

import com.example.moneymanagement_frontend.R

sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Analysis : BottomNavItem("analysis", R.drawable.ic_analytics, "Analysis")
    object Transaction : BottomNavItem("transaction", R.drawable.ic_transaction, "Transaction")
    object Setting : BottomNavItem("user", R.drawable.ic_setting, "Profile")
    object NewsFeed : BottomNavItem("newsfeed", R.drawable.ic_newsfeed, "NewsFeed")
    object Chat : BottomNavItem("chat", R.drawable.ic_chat, "Chat")
    object GroupChat : BottomNavItem("groupchat", R.drawable.ic_friend, "GroupChat")

    companion object {
        val allRoutes = listOf(
            Home,
            Transaction,
            Analysis,
            Setting,
            NewsFeed,
            Chat,
            GroupChat
        )
    }
}