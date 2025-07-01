package DI.Navigation

import DI.API.TokenHandler.TokenExpirationHandler
import DI.Composables.AnalysisSection.AnalysisBody
import DI.Composables.AnalysisSection.CalendarScreen
import DI.Composables.AuthSection.LoginScreen
import DI.Composables.AuthSection.RegisterScreen
import DI.Composables.TransactionSection.AddTransactionScreen
import DI.Composables.ChatSection.ChatMessageScreen
import DI.Composables.ChatSection.ChatScreen
import DI.Composables.FriendSection.FriendProfileScreen
import DI.Composables.GroupChat.GroupChatMessageScreen
import DI.Composables.GroupChat.GroupChatScreen
import DI.Composables.GroupChat.GroupProfileScreen
import DI.Composables.GroupTransactionComment.GroupTransactionCommentTestScreen
import DI.Composables.GroupTransactionScreen.GroupTransactionScreen
import DI.Composables.ProfileSection.EditProfileScreen
import DI.Composables.TransactionSection.TransactionDetailScreen
import DI.Composables.TransactionSection.TransactionEditScreen
import DI.Composables.TransactionSection.TransactionScreen
import DI.Models.NavBar.BottomNavItem
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.OcrViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.GroupChatViewModel
import DI.ViewModels.GroupFundViewModel
import DI.ViewModels.GroupModerationViewModel
import DI.ViewModels.GroupTransactionCommentViewModel
import DI.ViewModels.GroupTransactionViewModel
import GroupFundScreen
import ModernCategoriesScreen
import ProfileScreen
import Screens.MainLayout
import ViewModels.AuthViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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
                InnerNavHost(navController, innerNavController, modifier, parentEntry, authViewModel)
            }
            TokenExpirationHandler(navController)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InnerNavHost(
    appNavController : NavController,
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
    val currencyViewModel = viewModel<CurrencyConverterViewModel>(parentEntry)
    val groupFundViewModel = hiltViewModel<GroupFundViewModel>(parentEntry)
    val groupTransactionViewModel = hiltViewModel<GroupTransactionViewModel>(parentEntry)
    val groupChatViewModel = hiltViewModel<GroupChatViewModel>(parentEntry)
    val groupTransactionCommentViewModel = hiltViewModel<GroupTransactionCommentViewModel>(parentEntry)
    val groupModerationViewModel = hiltViewModel<GroupModerationViewModel>(parentEntry)

    NavHost(
        navController    = navController,
        startDestination = BottomNavItem.Profile.route,
        modifier         = modifier
    ) {
        composable(BottomNavItem.Home.route) {

        }

        composable(BottomNavItem.Friend.route) {
            FriendsScreenTheme {
                FriendsScreen(
                    authViewModel = authViewModel,
                    friendViewModel = friendViewModel,
                    profileViewModel = profileViewModel,
                    navController = navController
                )
            }
        }

        composable(BottomNavItem.Chat.route) {
            ChatScreen(
                authViewModel = authViewModel,
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

        composable(BottomNavItem.Wallet.route) {
//            WalletScreen(
//                viewModel = walletViewModel,
//            )
//            GroupFundScreen(
//                navController = navController,
//                groupFundViewModel = groupFundViewModel,
//                groupId = "727b116f-140c-4e1c-ad5a-ab35bc0ff089")
//            GroupTransactionScreen(
//                navController = navController,
//                viewModel = groupTransactionViewModel,
//                walletViewModel = walletViewModel,
//                categoryViewModel = categoryViewModel,
//                currencyViewModel = currencyViewModel,
//                groupFundId = "7a4258a0-e192-4886-8c3d-1dbe48041606"
//                )
            GroupChatScreen(navController, groupChatViewModel, profileViewModel)
        }

        composable(
            route = Routes.ChatMessage,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            ChatMessageScreen(
                navController = navController ,
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

        composable(BottomNavItem.Profile.route) {
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
            AnalysisBody(
                navController = navController,
                authViewModel = authViewModel,
                analysisViewModel = analysisViewModel
            )
        }

        composable(Routes.Calendar) {
            CalendarScreen(analysisViewModel = analysisViewModel)
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

        composable(BottomNavItem.Category.route) {
            ModernCategoriesScreen(
                categoryViewModel = categoryViewModel,
                authViewModel = authViewModel,
            )
  //          CurrencyConverterScreen(viewModel = currencyViewModel)
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
            if (groupFundId != null)
            {
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
                profileViewModel = profileViewModel,
                groupModerationViewModel = groupModerationViewModel
            )
        }

        composable("group_chat_message_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            GroupChatMessageScreen(
                groupId = groupId,
                navController = navController,
                groupChatViewModel = hiltViewModel(),
                groupTransactionCommentViewModel = groupTransactionCommentViewModel,
                profileViewModel = hiltViewModel(),
                groupModerationViewModel = groupModerationViewModel
            )
        }

        composable("group_profile_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            GroupProfileScreen(
                groupId = groupId,
                navController = navController,
                groupChatViewModel = groupChatViewModel,
                groupModerationViewModel = groupModerationViewModel,
                profileViewModel = profileViewModel
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
