package DI.Navigation

import ViewModels.AuthViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

val LocalMainNavBackStackEntry = compositionLocalOf<NavBackStackEntry> {
    error("No parent NavBackStackEntry provided")
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = if(isAuthenticated) Routes.Main else Routes.Auth) {
        authGraph(navController)
        mainGraph(navController)
    }
}

