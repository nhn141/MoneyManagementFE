package DI.API.TokenHandler

import DI.Navigation.Routes
import ViewModels.AuthViewModel
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TokenExpirationHandler(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val showDialog = remember { mutableStateOf(false) }
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    val authenticatedState by authViewModel.isAuthenticated.collectAsState()

    // Observe token expiration
    LaunchedEffect(Unit) {
        AuthInterceptor.tokenExpiredFlow.collectLatest {
            Log.d("TokenExpiredState", AuthInterceptor.tokenExpiredFlow.toString())
            showDialog.value = true
            Log.d("ShowDialogValue", showDialog.value.toString())
        }
    }

    LaunchedEffect(refreshTokenState, authenticatedState) {
        Log.d("OnAuthenticatedStateChange", authenticatedState.toString())
        // If user stay in
        refreshTokenState?.let { result ->
            showDialog.value = false
            result.onFailure {
                navController.navigate(Routes.Auth) {
                    popUpTo(Routes.Auth) { inclusive = true }
                }
            }
            // Success case: stay on current screen (no navigation needed)
        }

        // If user log out
        if(!authenticatedState) {
            navController.navigate(Routes.Auth) {
                popUpTo(Routes.Auth) { inclusive = true }
            }
        }
    }

    Log.d("ShowDiaLogBeforeAlert", showDialog.value.toString())
    if (showDialog.value) {
        Log.d("AlertDialog", "Activated!")
        AlertDialog(
            onDismissRequest = { /* Prevent dismissing without action */ },
            title = { Text("Session Expired") },
            text = { Text("Your session has expired. Would you like to stay in or log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    authViewModel.refreshToken()
                }) {
                    Text("Stay In")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    authViewModel.logout()
                }) {
                    Text("Log Out")
                }
            }
        )
    }
}