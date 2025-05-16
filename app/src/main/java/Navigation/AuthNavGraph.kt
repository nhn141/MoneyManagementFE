package DI.Navigation

import DI.Composables.AuthSection.LoginScreen
import DI.Composables.AuthSection.RegisterScreen
import DI.Composables.ChatSection.ChatScreen
import DI.Models.BottomNavItem
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import Screens.MainLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.example.friendsapp.FriendsScreen
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
        val parentEntry = navController.rememberParentEntry(Routes.Main)
        CompositionLocalProvider(
            LocalMainNavBackStackEntry provides parentEntry
        ) {
            MainLayout { innerNavController, modifier ->
                InnerNavHost(innerNavController, modifier, parentEntry)
            }
        }
    }
}

@Composable
private fun InnerNavHost(
    navController: NavHostController,
    modifier: Modifier,
    parentEntry: NavBackStackEntry
) {
    val friendViewModel = hiltViewModel<FriendViewModel>(parentEntry)
    val chatViewModel = hiltViewModel<ChatViewModel>(parentEntry)
    val profileViewModel = hiltViewModel<ProfileViewModel>(parentEntry)

    NavHost(
        navController    = navController,
        startDestination = BottomNavItem.Home.route,
        modifier         = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            FriendsScreen(
                friendViewModel = friendViewModel,
                navController = navController
            )
        }
        composable(BottomNavItem.Transaction.route) {
            ChatScreen(
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                friendViewModel = friendViewModel,
                navController = navController
            )
        }
    }
}

@Composable
fun NavHostController.rememberParentEntry(route: String): NavBackStackEntry {
    val currentEntry by currentBackStackEntryAsState()
    return remember(currentEntry) {
        getBackStackEntry(route)
    }
}
