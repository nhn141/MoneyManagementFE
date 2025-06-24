package com.moneymanager.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Data Classes for hardcoded data - Replace with API models
data class UserProfile(
    val name: String,
    val avatarResource: Int, // Replace with avatar URL from API
    val currentDate: String
)

data class FinancialOverview(
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val currency: String = "₫" // Vietnamese Dong as per user location
)

data class Transaction(
    val id: String,
    val date: String,
    val category: String,
    val categoryIcon: ImageVector,
    val description: String,
    val amount: Double,
    val isIncome: Boolean
)

data class SocialNotification(
    val friendRequests: Int,
    val unreadMessages: Int,
    val recentActivity: String
)

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val destination: String,
    val backgroundColor: Color
)

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val destination: String
)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Hardcoded data - Replace with API calls
    val userProfile = remember {
        UserProfile(
            name = "John",
            avatarResource = android.R.drawable.ic_menu_gallery, // Placeholder
            currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
        )
    }

    val financialOverview = remember {
        FinancialOverview(
            totalBalance = 30000000.0, // 30 million VND
            monthlyIncome = 12000000.0, // 12 million VND
            monthlyExpenses = 7500000.0, // 7.5 million VND
            currency = "₫"
        )
    }

    val recentTransactions = remember {
        listOf(
            Transaction(
                "1",
                "June 23",
                "Food",
                Icons.Default.Restaurant,
                "Dinner at Cafe",
                -150000.0,
                false
            ),
            Transaction(
                "2",
                "June 23",
                "Salary",
                Icons.Default.Work,
                "Monthly Salary",
                12000000.0,
                true
            ),
            Transaction(
                "3",
                "June 22",
                "Transport",
                Icons.Default.DirectionsCar,
                "Grab to Office",
                -45000.0,
                false
            ),
            Transaction(
                "4",
                "June 22",
                "Shopping",
                Icons.Default.ShoppingCart,
                "Grocery Shopping",
                -320000.0,
                false
            ),
            Transaction(
                "5",
                "June 21",
                "Entertainment",
                Icons.Default.Movie,
                "Movie Tickets",
                -180000.0,
                false
            )
        )
    }

    val socialNotifications = remember {
        SocialNotification(
            friendRequests = 2,
            unreadMessages = 3,
            recentActivity = "John posted: 'New budget goal achieved!'"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoneyAppColors.Background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = MoneyAppColors.Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Personalized Greeting
            item {
                PersonalizedGreeting(userProfile = userProfile)
            }

            // Financial Overview
            item {
                FinancialOverviewCard(financialOverview = financialOverview)
            }

            // Quick Actions
            item {
                QuickActionsSection(navController = navController)
            }

            // Recent Transactions
            item {
                RecentTransactionsSection(
                    transactions = recentTransactions,
                    navController = navController,
                    currency = financialOverview.currency
                )
            }

            // Navigation Links Grid
            item {
                NavigationLinksSection(navController = navController)
            }

            // Social Notifications
            item {
                SocialNotificationsSection(
                    notifications = socialNotifications,
                    navController = navController
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PersonalizedGreeting(userProfile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MoneyAppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar - Replace with AsyncImage for API data
            Image(
                painter = painterResource(id = userProfile.avatarResource),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MoneyAppColors.Primary)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Hi, ${userProfile.name}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MoneyAppColors.OnSurface
                )
                Text(
                    text = userProfile.currentDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MoneyAppColors.OnBackground
                )
            }
        }
    }
}

@Composable
fun FinancialOverviewCard(financialOverview: FinancialOverview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MoneyAppColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = MoneyAppColors.OnPrimary.copy(alpha = 0.8f)
            )

            Text(
                text = "${financialOverview.currency}${formatCurrency(financialOverview.totalBalance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MoneyAppColors.OnPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.bodySmall,
                        color = MoneyAppColors.OnPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${financialOverview.currency}${formatCurrency(financialOverview.monthlyIncome)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MoneyAppColors.OnPrimary
                    )
                }

                Column {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MoneyAppColors.OnPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${financialOverview.currency}${formatCurrency(financialOverview.monthlyExpenses)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MoneyAppColors.OnPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    val quickActions = listOf(
        QuickAction("Add Transaction", Icons.Default.Add, "transaction", MoneyAppColors.Primary),
        QuickAction(
            "View Wallet",
            Icons.Default.AccountBalanceWallet,
            "wallet",
            MoneyAppColors.Secondary
        ),
        QuickAction(
            "Analytics",
            Icons.Default.Analytics,
            "analytics",
            MoneyAppColors.PrimaryVariant
        )
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(quickActions) { action ->
            FilledTonalButton(
                onClick = {
                    // Replace with actual navigation
                    navController.navigate(action.destination)
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = action.backgroundColor
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    navController: NavController,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MoneyAppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MoneyAppColors.OnSurface
                )

                TextButton(
                    onClick = { navController.navigate("transaction") }
                ) {
                    Text(
                        text = "View All",
                        color = MoneyAppColors.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            transactions.forEach { transaction ->
                TransactionItem(
                    transaction = transaction,
                    currency = currency,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    currency: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Handle transaction click */ }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (transaction.isIncome) MoneyAppColors.Success.copy(alpha = 0.1f)
                    else MoneyAppColors.Error.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transaction.categoryIcon,
                contentDescription = transaction.category,
                tint = if (transaction.isIncome) MoneyAppColors.Success else MoneyAppColors.Error,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Transaction Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MoneyAppColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${transaction.date} • ${transaction.category}",
                style = MaterialTheme.typography.bodySmall,
                color = MoneyAppColors.OnBackground
            )
        }

        // Amount
        Text(
            text = "${if (transaction.isIncome) "+" else ""}$currency${
                formatCurrency(
                    kotlin.math.abs(
                        transaction.amount
                    )
                )
            }",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.isIncome) MoneyAppColors.Success else MoneyAppColors.Error
        )
    }
}

@Composable
fun NavigationLinksSection(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem("Category", Icons.Default.Category, "category"),
        NavigationItem("Report", Icons.Default.Assessment, "report"),
        NavigationItem("Settings", Icons.Default.Settings, "settings"),
        NavigationItem("Friends", Icons.Default.People, "friends"),
        NavigationItem("Chat", Icons.Default.Chat, "chat"),
        NavigationItem("News Feed", Icons.Default.Feed, "news_feed"),
        NavigationItem("Group Chat", Icons.Default.Groups, "group_chat"),
        NavigationItem("Group Fund", Icons.Default.AccountBalance, "group_fund"),
        NavigationItem("Group Transaction", Icons.Default.SwapHoriz, "group_transaction")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MoneyAppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MoneyAppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of navigation items
            for (i in navigationItems.indices step 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 0..2) {
                        if (i + j < navigationItems.size) {
                            NavigationItemCard(
                                item = navigationItems[i + j],
                                navController = navController,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NavigationItemCard(
    item: NavigationItem,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { navController.navigate(item.destination) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MoneyAppColors.Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MoneyAppColors.Primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall,
            color = MoneyAppColors.OnSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SocialNotificationsSection(
    notifications: SocialNotification,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MoneyAppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Social Updates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MoneyAppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Friend Requests
            if (notifications.friendRequests > 0) {
                SocialNotificationItem(
                    icon = Icons.Default.PersonAdd,
                    text = "${notifications.friendRequests} Friend Requests",
                    onClick = { navController.navigate("friends") }
                )
            }

            // Unread Messages
            if (notifications.unreadMessages > 0) {
                SocialNotificationItem(
                    icon = Icons.Default.Message,
                    text = "${notifications.unreadMessages} Unread Messages",
                    onClick = { navController.navigate("chat") }
                )
            }

            // Recent Activity
            SocialNotificationItem(
                icon = Icons.Default.Notifications,
                text = notifications.recentActivity,
                onClick = { navController.navigate("news_feed") }
            )
        }
    }
}

@Composable
fun SocialNotificationItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MoneyAppColors.Primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MoneyAppColors.OnSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MoneyAppColors.OnBackground,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val bottomNavItems = listOf(
        NavigationItem("Home", Icons.Default.Home, "home"),
        NavigationItem("Transaction", Icons.Default.Receipt, "transaction"),
        NavigationItem("Wallet", Icons.Default.AccountBalanceWallet, "wallet"),
        NavigationItem("Analytics", Icons.Default.Analytics, "analytics"),
        NavigationItem("Profile", Icons.Default.Person, "profile")
    )

    NavigationBar(
        containerColor = MoneyAppColors.Surface,
        contentColor = MoneyAppColors.Primary
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = false, // Implement proper selection logic
                onClick = {
                    navController.navigate(item.destination)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MoneyAppColors.Primary,
                    selectedTextColor = MoneyAppColors.Primary,
                    indicatorColor = MoneyAppColors.Primary.copy(alpha = 0.1f),
                    unselectedIconColor = MoneyAppColors.OnBackground,
                    unselectedTextColor = MoneyAppColors.OnBackground
                )
            )
        }
    }
}

// Utility function to format currency
fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return formatter.format(amount.toLong())
}

// Main Activity Integration Example
/*
@Composable
fun MoneyAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        // Add other screen composables here
        // Replace with actual screen implementations
        composable("transaction") { /* TransactionScreen(navController) */ }
        composable("wallet") { /* WalletScreen(navController) */ }
        composable("category") { /* CategoryScreen(navController) */ }
        composable("analytics") { /* AnalyticsScreen(navController) */ }
        composable("report") { /* ReportScreen(navController) */ }
        composable("settings") { /* SettingsScreen(navController) */ }
        composable("friends") { /* FriendsScreen(navController) */ }
        composable("chat") { /* ChatScreen(navController) */ }
        composable("news_feed") { /* NewsFeedScreen(navController) */ }
        composable("group_chat") { /* GroupChatScreen(navController) */ }
        composable("group_fund") { /* GroupFundScreen(navController) */ }
        composable("group_transaction") { /* GroupTransactionScreen(navController) */ }
        composable("profile") { /* ProfileScreen(navController) */ }
    }
}
*/