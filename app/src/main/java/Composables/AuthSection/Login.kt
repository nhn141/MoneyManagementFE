package DI.Composables.AuthSection

import AuthScreen
import AuthTextField
import CustomButton
import CustomRow
import DI.Navigation.Routes
import SocialLoginSection
import ViewModels.AuthViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneymanagement_frontend.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Khi trạng thái loginState thay đổi, hiển thị Snackbar
    LaunchedEffect(loginState) {
        loginState?.let { result ->
            if (result.isSuccess) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Login successful!")
                }
                navController.navigate(Routes.Main) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
            } else {
                snackbarHostState.showSnackbar("Login failed: ${result.exceptionOrNull()?.message}")
            }
            viewModel.resetLoginState() // 🔥 Reset state ngay sau khi hiển thị thông báo
        }
    }

    var emailError by remember { mutableStateOf<String?>(null)}
    var passwordError by remember { mutableStateOf<String?>(null)}

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AuthScreen("Welcome", 0.3f) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 35.dp, end = 35.dp, top = 25.dp)
            ) {
                Text(
                    text = "Email",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = colorResource(R.color.textColor),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
                AuthTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null },
                    placeholder = "Ex: example@example.com",
                    isError = emailError != null)
                if (emailError != null) {
                    ErrorAlert(emailError!!)
                }
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = "Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = colorResource(R.color.textColor),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
                AuthTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null },
                    placeholder = "●●●●●●●●",
                    isPassword = true,
                    isError = passwordError != null)
                if (passwordError != null) {
                    ErrorAlert(passwordError!!)
                }
                Spacer(modifier = Modifier.height(50.dp))

                CustomRow {
                    CustomButton("Log In") {
                        val emailResult = Validator.validateField("email", email)
                        val passwordResult = Validator.validateField("password", password)

                        emailError = emailResult.errorMessage
                        passwordError = passwordResult.errorMessage

                        if(emailResult.isValid && passwordResult.isValid) {
                            viewModel.login(email, password)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                CustomRow {
                    Text(
                        text = "Forgot Password?",
                        color = colorResource(R.color.textColor),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { },
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Don't have an account?", fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign Up",
                        fontSize = 14.sp,
                        color = Color.Blue.copy(0.5f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }
    }
}
