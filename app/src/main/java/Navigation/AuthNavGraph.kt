package DI.Navigation

import DI.Composables.AnalysisSection.AnalysisBody
import DI.Composables.AnalysisSection.CalendarScreen
import DI.Composables.AuthSection.LoginScreen
import DI.Composables.AuthSection.RegisterScreen
import DI.Composables.ChatSection.ChatMessageScreen
import DI.Composables.ChatSection.ChatScreen
import DI.Composables.FriendSection.FriendProfileScreen
import DI.Composables.GeneralTemplate
import DI.Composables.ProfileSection.EditProfileScreen
import DI.Models.BottomNavItem
import DI.ViewModels.AnalysisViewModel
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import ProfileScreen
import Screens.AnalysisHeader
import Screens.MainLayout
import ViewModels.AuthViewModel
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
import okhttp3.Route

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

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    composable(Routes.Main) {
        val parentEntry = navController.rememberParentEntry(Routes.Main) ?: it
        CompositionLocalProvider(
            LocalMainNavBackStackEntry provides parentEntry
        ) {
            MainLayout { innerNavController, modifier ->
                InnerNavHost(navController, innerNavController, modifier, parentEntry)
            }
        }
    }
}

@Composable
private fun InnerNavHost(
    appNavController : NavController,
    navController: NavHostController,
    modifier: Modifier,
    parentEntry: NavBackStackEntry
) {
    val friendViewModel = hiltViewModel<FriendViewModel>(parentEntry)
    val chatViewModel = hiltViewModel<ChatViewModel>(parentEntry)
    val profileViewModel = hiltViewModel<ProfileViewModel>(parentEntry)
    val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
    val analysisViewModel = hiltViewModel<AnalysisViewModel>(parentEntry)

    NavHost(
        navController    = navController,
        startDestination = BottomNavItem.Home.route,
        modifier         = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            FriendsScreenTheme {
                FriendsScreen(
                    friendViewModel = friendViewModel,
                    profileViewModel = profileViewModel,
                    navController = navController
                )
            }
        }
        composable(BottomNavItem.Transaction.route) {
            ChatScreen(
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel,
                navController = navController
            )
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
                profileViewModel = profileViewModel
            )
        }

        composable(Routes.EditProfile) {
            EditProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }

        composable(BottomNavItem.Analysis.route) {
            GeneralTemplate(
                contentHeader = { AnalysisHeader() },
                contentBody = { AnalysisBody(
                    navController = navController,
                    analysisViewModel = analysisViewModel)
                }
            )
        }

        composable(Routes.Calendar) {
            CalendarScreen(analysisViewModel = analysisViewModel)
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
