package DI.Composables.HomeSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moneymanager.ui.screens.FinancialOverview
import com.moneymanager.ui.screens.FinancialOverviewCard
import com.moneymanager.ui.screens.MoneyAppColors
import com.moneymanager.ui.screens.NavigationLinksSection
import com.moneymanager.ui.screens.PersonalizedGreeting
import com.moneymanager.ui.screens.QuickActionsSection
import com.moneymanager.ui.screens.RecentTransactionsSection
import com.moneymanager.ui.screens.SocialNotification
import com.moneymanager.ui.screens.SocialNotificationsSection
import com.moneymanager.ui.screens.Transaction
import com.moneymanager.ui.screens.UserProfile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomePage(navController: NavController) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MoneyAppColors.Background,
                        Color(0xFFECFDF5)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
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

