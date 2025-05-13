package DI.Navigation

import DI.API.TokenHandler.TokenExpirationHandler
import Screens.MainScreen
import ViewModels.AuthViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = if(isAuthenticated) Routes.Main else Routes.Auth) {
        authGraph(navController)
        composable(Routes.Main) {
            MainScreen()
            TokenExpirationHandler(navController)
        }
    }
}


