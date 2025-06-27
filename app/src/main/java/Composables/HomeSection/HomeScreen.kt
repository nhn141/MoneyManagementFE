package DI.Composables.HomeSection

import DI.Composables.CategorySection.getCategoryIcon
import DI.Composables.ProfileSection.AvatarImage
import DI.Composables.ProfileSection.MainColor
import DI.Composables.TransactionSection.GeneralTransactionItem
import DI.Models.NavBar.BottomNavItem
import DI.Models.UserInfo.Profile
import DI.Navigation.Routes
import DI.Utils.CurrencyUtils
import DI.Utils.DateTimeUtils
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.NewsFeedViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import Utils.LanguageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavController,
    walletViewModel: WalletViewModel,
    categoryViewModel: CategoryViewModel,
    transactionViewModel: TransactionViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel,
    chatViewModel: ChatViewModel,
    newsFeedViewModel: NewsFeedViewModel
) {
    val exchangeRate =
        currencyConverterViewModel.exchangeRate.collectAsStateWithLifecycle().value ?: 1.0
    val isVND = currencyConverterViewModel.isVND.collectAsStateWithLifecycle().value

    val languageCode = LanguageManager.getLanguagePreferenceSync(LocalContext.current)

    val profile =
        profileViewModel.profile.collectAsStateWithLifecycle().value?.getOrNull() ?: Profile()
    val avatarUrl = profile.avatarUrl
    val avatarVersion = profileViewModel.avatarVersion.collectAsState().value
    val userProfile = remember(profile, avatarUrl, avatarVersion, isVND) {
        UserProfile(
            name = profile.displayName.ifBlank { "${profile.firstName} ${profile.lastName}" },
            avatarUrl = avatarUrl,
            avatarVersion = avatarVersion,
            currentDate = DateTimeUtils.getReadableCurrentDate(languageCode)
        )
    }

    val wallets =
        walletViewModel.wallets.collectAsStateWithLifecycle().value?.getOrNull() ?: emptyList()
    val transactions = transactionViewModel.allTransactions
    val currentMonthTransactions =
        transactions.filter { parseMonth(it.timestamp) == LocalDate.now().month }

    val financialOverview = remember(wallets, currentMonthTransactions) {
        val totalBalance = wallets.sumOf { it.balance.toDouble() }
        val incomeThisMonth = currentMonthTransactions.filter { it.isIncome }
        val expenseThisMonth = currentMonthTransactions.filter { !it.isIncome }
        val monthlyIncome = incomeThisMonth.sumOf { it.amount.toDouble() }
        val monthlyExpenses = expenseThisMonth.sumOf { it.amount.toDouble() }

        FinancialOverview(
            totalBalance = totalBalance,
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses,
        )
    }

    val recentTransactions = getMostRecentTransactions(currentMonthTransactions, 5)

    val friendRequests =
        friendViewModel.friendRequests.collectAsStateWithLifecycle().value?.getOrNull()
            ?: emptyList()
    val latestChats =
        chatViewModel.latestChats.collectAsStateWithLifecycle().value?.getOrNull() ?: emptyList()

    val latestSocialNotification = newsFeedViewModel.getLatestSocialInfo()

    val socialNotifications = remember {
        SocialNotification(
            friendRequests = friendRequests.size,
            unreadMessages = latestChats.sumOf { it.unreadCount },
            recentActivity = latestSocialNotification.latestPost
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
                FinancialOverviewCard(
                    financialOverview = financialOverview,
                    isVND = isVND,
                    exchangeRate = exchangeRate
                )
            }

            // Charts
            item {

            }

            // Recent Transactions
            item {
                RecentTransactionsSection(
                    transactions = recentTransactions,
                    navController = navController,
                    categoryViewModel = categoryViewModel,
                    isVND = isVND,
                    exchangeRate = exchangeRate
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
            // User Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MainColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                AvatarImage(userProfile.avatarUrl, userProfile.avatarVersion)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.greeting_text, userProfile.name),
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
fun FinancialOverviewCard(
    financialOverview: FinancialOverview,
    isVND: Boolean,
    exchangeRate: Double
) {
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
                text = stringResource(R.string.total_balance),
                style = MaterialTheme.typography.bodyMedium,
                color = MoneyAppColors.OnPrimary.copy(alpha = 0.8f)
            )

            Text(
                text = CurrencyUtils.formatAmount(
                    financialOverview.totalBalance,
                    isVND,
                    exchangeRate
                ),
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
                        text = stringResource(R.string.income),
                        style = MaterialTheme.typography.bodySmall,
                        color = MoneyAppColors.OnPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = CurrencyUtils.formatAmount(
                            financialOverview.monthlyIncome,
                            isVND,
                            exchangeRate
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MoneyAppColors.OnPrimary
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.expense),
                        style = MaterialTheme.typography.bodySmall,
                        color = MoneyAppColors.OnPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = CurrencyUtils.formatAmount(
                            financialOverview.monthlyExpenses,
                            isVND,
                            exchangeRate
                        ),
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
fun RecentTransactionsSection(
    transactions: List<GeneralTransactionItem>,
    categoryViewModel: CategoryViewModel,
    isVND: Boolean,
    exchangeRate: Double,
    navController: NavController,
) {
    val categories = categoryViewModel.categories.collectAsState().value?.getOrNull() ?: emptyList()

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
                    text = stringResource(R.string.recent_transactions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MoneyAppColors.OnSurface
                )

                TextButton(
                    onClick = { navController.navigate(BottomNavItem.Transaction.route) }
                ) {
                    Text(
                        text = stringResource(R.string.view_all),
                        color = MoneyAppColors.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            transactions.forEach { transaction ->
                val category = categories.find { it.categoryID == transaction.categoryID }
                TransactionItem(
                    transaction = transaction,
                    categoryName = category?.name ?: stringResource(R.string.unknown_category),
                    isVND = isVND,
                    exchangeRate = exchangeRate,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: GeneralTransactionItem,
    categoryName: String,
    isVND: Boolean,
    exchangeRate: Double,
    modifier: Modifier = Modifier
) {
    val formattedDate = DateTimeUtils.formatDateTime(transaction.timestamp, LocalContext.current)

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
                imageVector = getCategoryIcon(categoryName),
                contentDescription = categoryName,
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
                text = transaction.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MoneyAppColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${formattedDate.formattedDate} • $categoryName",
                style = MaterialTheme.typography.bodySmall,
                color = MoneyAppColors.OnBackground
            )
        }

        // Amount
        Text(
            text = CurrencyUtils.formatAmount(
                transaction.amount.toDoubleOrNull() ?: 1.0,
                isVND,
                exchangeRate
            ),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.isIncome) MoneyAppColors.Success else MoneyAppColors.Error
        )
    }
}

@Composable
fun NavigationLinksSection(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem("Category", Icons.Default.Category, Routes.Category),
        NavigationItem("Wallet", Icons.Default.AccountBalanceWallet, Routes.Wallet),
        NavigationItem("Report", Icons.Default.Assessment, Routes.Report),
        NavigationItem("Friends", Icons.Default.People, Routes.Friend),
        NavigationItem("Chat", Icons.AutoMirrored.Filled.Chat, Routes.Chat),
        NavigationItem("Group Chat", Icons.Default.Groups, Routes.GroupChat),
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
                text = stringResource(R.string.more_features),
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
                text = stringResource(R.string.social_updates),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MoneyAppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Friend Requests
            if (notifications.friendRequests > 0) {
                SocialNotificationItem(
                    icon = Icons.Default.PersonAdd,
                    text = stringResource(
                        R.string.friend_requests_count,
                        notifications.friendRequests
                    ),
                    onClick = { navController.navigate(Routes.Friend) }
                )
            }

            // Unread Messages
            if (notifications.unreadMessages > 0) {
                SocialNotificationItem(
                    icon = Icons.AutoMirrored.Filled.Message,
                    text = stringResource(
                        R.string.unread_messages_count,
                        notifications.unreadMessages
                    ),
                    onClick = { navController.navigate(Routes.Chat) }
                )
            }

            // Recent Activity
            SocialNotificationItem(
                icon = Icons.Default.Notifications,
                text = notifications.recentActivity,
                onClick = { navController.navigate(BottomNavItem.NewsFeed.route) }
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
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MoneyAppColors.OnBackground,
            modifier = Modifier.size(16.dp)
        )
    }
}
