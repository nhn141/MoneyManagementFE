package DI.Navigation

import DI.API.TokenHandler.TokenExpirationHandler
import DI.Composables.AnalysisSection.AnalysisScreen
import DI.Composables.AnalysisSection.CalendarScreen
import DI.Composables.AuthSection.LoginScreen
import DI.Composables.AuthSection.RegisterScreen
import DI.Composables.ChatSection.ChatMessageScreen
import DI.Composables.ChatSection.ChatScreen
import DI.Composables.ExportReports.ReportScreen
import DI.Composables.FriendSection.FriendProfileScreen
import DI.Composables.GroupChat.GroupChatMessageScreen
import DI.Composables.GroupChat.GroupChatScreen
import DI.Composables.GroupChat.GroupProfileScreen
import DI.Composables.GroupTransactionScreen.GroupTransactionScreen
import DI.Composables.HomeSection.HomeScreen
import DI.Composables.NewsFeedSection.NewsFeedScreen
import DI.Composables.ProfileSection.EditProfileScreen
import DI.Composables.TransactionSection.AddTransactionScreen
import DI.Composables.TransactionSection.TransactionDetailScreen
import DI.Composables.TransactionSection.TransactionEditScreen
import DI.Composables.TransactionSection.TransactionScreen
import DI.Composables.WalletSection.WalletScreen
import DI.Models.NavBar.BottomNavItem
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.GroupFundViewModel
import DI.ViewModels.GroupTransactionCommentViewModel
import DI.ViewModels.GroupTransactionViewModel
import DI.ViewModels.NewsFeedViewModel
import DI.ViewModels.OcrViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.ReportViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import GroupFundScreen
import ModernCategoriesScreen
import ProfileScreen
import Screens.MainLayout
import ViewModels.AuthViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.friendsapp.FriendsScreen
import com.example.friendsapp.FriendsScreenTheme

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(startDestination = Routes.Login, route = Routes.Auth) {
        composable(Routes.Login) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.Register) },
                navController = navController
            )
        }
        composable(Routes.Register) {
            RegisterScreen {
                navController.navigate(Routes.Login) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    composable(Routes.Main) {
        val parentEntry = navController.rememberParentEntry(Routes.Main) ?: it
        val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
        CompositionLocalProvider(
            LocalMainNavBackStackEntry provides parentEntry
        ) {
            MainLayout { innerNavController, modifier ->
                InnerNavHost(
                    navController,
                    innerNavController,
                    modifier,
                    parentEntry,
                    authViewModel
                )
            }
            TokenExpirationHandler(navController)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InnerNavHost(
    appNavController: NavController,
    navController: NavHostController,
    modifier: Modifier,
    parentEntry: NavBackStackEntry,
    authViewModel: AuthViewModel
) {
    val friendViewModel = hiltViewModel<FriendViewModel>(parentEntry)
    val chatViewModel = hiltViewModel<ChatViewModel>(parentEntry)
    val profileViewModel = hiltViewModel<ProfileViewModel>(parentEntry)
    val analysisViewModel = hiltViewModel<AnalysisViewModel>(parentEntry)
    val categoryViewModel = hiltViewModel<CategoryViewModel>(parentEntry)
    val transactionViewModel = hiltViewModel<TransactionViewModel>(parentEntry)
    val walletViewModel = hiltViewModel<WalletViewModel>(parentEntry)
    val ocrViewModel = hiltViewModel<OcrViewModel>(parentEntry)
    val currencyViewModel = hiltViewModel<CurrencyConverterViewModel>(parentEntry)
    val groupFundViewModel = hiltViewModel<GroupFundViewModel>(parentEntry)
    val groupTransactionViewModel = hiltViewModel<GroupTransactionViewModel>(parentEntry)
    val groupChatViewModel = hiltViewModel<GroupChatViewModel>(parentEntry)
    val groupTransactionCommentViewModel =
        hiltViewModel<GroupTransactionCommentViewModel>(parentEntry)
    val newsFeedViewModel = hiltViewModel<NewsFeedViewModel>(parentEntry)
    val reportViewModel = hiltViewModel<ReportViewModel>(parentEntry)

    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()

    fun refreshAppData() {
        transactionViewModel.refreshData()
        profileViewModel.refreshAllData()
        newsFeedViewModel.loadNextPost()
        analysisViewModel.refreshAllData()

        // For HomeScreen
        currencyViewModel.refreshAllData()
        walletViewModel.getWallets()
        friendViewModel.refreshAllData()
        chatViewModel.getLatestChats()
    }

    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            // Call your reload functions here
            refreshAppData()
        }
    }

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Setting.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                navController = navController,
                friendViewModel = friendViewModel,
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                transactionViewModel = transactionViewModel,
                currencyConverterViewModel = currencyViewModel,
                walletViewModel = walletViewModel,
                categoryViewModel = categoryViewModel,
                newsFeedViewModel = newsFeedViewModel
            )
        }

        composable(Routes.Friend) {
            FriendsScreenTheme {
                FriendsScreen(
                    friendViewModel = friendViewModel,
                    profileViewModel = profileViewModel,
                    navController = navController
                )
            }
        }

        composable(BottomNavItem.Chat.route) {
            ChatScreen(
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel,
                navController = navController
            )
        }

        composable(BottomNavItem.Transaction.route) {
            TransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                currencyViewModel = currencyViewModel
            )
        }

        composable(Routes.Wallet) {
            WalletScreen(
                walletViewModel = walletViewModel,
                currencyConverterViewModel = currencyViewModel,
                navController = navController
            )
        }

        composable(BottomNavItem.GroupChat.route) {
            GroupChatScreen(navController, groupChatViewModel, profileViewModel)
        }

        composable(BottomNavItem.NewsFeed.route) {
            NewsFeedScreen(
                navController = navController,
                viewModel = newsFeedViewModel,
                profileViewModel = profileViewModel,
                chatViewModel = chatViewModel,
                groupChatViewModel = groupChatViewModel
            )
        }

        composable(Routes.Report) {
            ReportScreen(
                viewModel = reportViewModel
            )
        }

        composable(
            route = "${BottomNavItem.NewsFeed.route}?postIdToFocus={postIdToFocus}",
            arguments = listOf(navArgument("postIdToFocus") { defaultValue = null; nullable = true })
        ) { backStackEntry ->
            val postIdToFocus = backStackEntry.arguments?.getString("postIdToFocus")
            NewsFeedScreen(
                navController = navController,
                viewModel = newsFeedViewModel,
                profileViewModel = profileViewModel,
                chatViewModel = chatViewModel,
                groupChatViewModel = groupChatViewModel,
                postIdToFocus = postIdToFocus
            )
        }

        composable(
            route = Routes.ChatMessage,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            ChatMessageScreen(
                navController = navController,
                friendId = friendId,
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel
            )
        }

        composable(
            route = Routes.FriendProfile,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            FriendProfileScreen(
                friendId = friendId,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel,
                navController = navController
            )
        }

        composable(BottomNavItem.Setting.route) {
            ProfileScreen(
                appNavController = appNavController,
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                currencyViewModel = currencyViewModel
            )
        }

        composable(Routes.EditProfile) {
            EditProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }

        composable(BottomNavItem.Analysis.route) {
            AnalysisScreen(
                navController = navController,
                analysisViewModel = analysisViewModel,
                currencyConverterViewModel = currencyViewModel
            )
        }

        composable(Routes.Calendar) {
            CalendarScreen(
                analysisViewModel = analysisViewModel,
                currencyConverterViewModel = currencyViewModel
            )
        }

        composable(Routes.AddTransaction) {
            AddTransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                ocrViewModel = ocrViewModel,
                currencyViewModel = currencyViewModel
            )
        }

        composable(Routes.Category) {
            ModernCategoriesScreen(
                categoryViewModel = categoryViewModel,
                navController = navController
            )
        }

        composable(
            route = Routes.TransactionDetail,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""

            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId,
                viewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyViewModel = currencyViewModel
            )
        }

        composable(
            route = Routes.TransactionEdit,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""

            TransactionEditScreen(
                navController = navController,
                transactionId = transactionId,
                viewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                currencyViewModel = currencyViewModel
            )
        }

        composable("group_transaction/{groupFundId}") { backStackEntry ->
            val groupFundId = backStackEntry.arguments?.getString("groupFundId")
            if (groupFundId != null) {
                GroupTransactionScreen(
                    navController,
                    groupTransactionViewModel,
                    walletViewModel,
                    categoryViewModel,
                    currencyViewModel,
                    groupFundId = groupFundId
                )
            }
        }

        composable(
            route = Routes.GroupChatMessage,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            GroupChatMessageScreen(
                navController = navController,
                groupId = groupId,
                groupChatViewModel = groupChatViewModel,
                groupTransactionCommentViewModel = groupTransactionCommentViewModel,
                profileViewModel = profileViewModel
            )
        }

        composable("group_profile_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            GroupProfileScreen(
                groupId = groupId,
                navController = navController,
                groupChatViewModel = groupChatViewModel,
            )
        }

        composable("group_fund_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            GroupFundScreen(
                navController = navController,
                groupFundViewModel = hiltViewModel(),
                groupId = groupId
            )
        }
    }
}

@Composable
fun NavHostController.rememberParentEntry(route: String): NavBackStackEntry? {
    val currentEntry by currentBackStackEntryAsState()
    return remember(currentEntry) {
        try {
            getBackStackEntry(route)
        } catch (e: IllegalArgumentException) {
            currentEntry // Fallback to current entry if route not found
        }
    }
}
