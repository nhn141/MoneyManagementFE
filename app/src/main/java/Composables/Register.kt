package Composables

import AuthScreen
import AuthTextField
import CustomButton
import CustomRow
import ViewModels.AuthViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(viewModel: AuthViewModel = viewModel(), onNavigateToLogin: () -> Unit) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Khi trạng thái loginState thay đổi, hiển thị Snackbar
    LaunchedEffect(registerState) {
        registerState?.let { result ->
            coroutineScope.launch {
                if (result.isSuccess) {
                    snackbarHostState.showSnackbar("Register successful!")
                } else {
                    snackbarHostState.showSnackbar("Register failed: ${result.exceptionOrNull()?.message}")
                }
            }
            viewModel.resetRegisterState() // 🔥 Reset ngay sau khi hiển thị thông báo
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AuthScreen("Create Account") {
            Column(modifier = Modifier.padding(paddingValues).padding(start = 35.dp, end = 35.dp, top = 10.dp)) {
                Text(
                    text = "Username",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = colorResource(R.color.textColor),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
                AuthTextField(username, { username = it }, "Ex: username")

                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Email",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = colorResource(R.color.textColor),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
                AuthTextField(email, { email = it }, "Ex: example@example.com")

                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = colorResource(R.color.textColor),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
                AuthTextField(password, { password = it }, "●●●●●●●●", isPassword = true)

                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Confirm Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = colorResource(R.color.textColor),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
                AuthTextField(confirmPassword, { confirmPassword = it }, "●●●●●●●●", isPassword = true)

                Spacer(modifier = Modifier.height(27.dp))
                TermsAndConditions()
                Spacer(modifier = Modifier.height(10.dp))
                CustomRow { CustomButton("Sign Up") {
                    viewModel.register(username, email, password)
                } }
                Spacer(modifier = Modifier.height(10.dp))
                CustomRow {
                    Text(text = "Already have an account?", fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Log In",
                        fontSize = 14.sp,
                        color = Color.Blue.copy(0.5f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }

}

@Composable
fun ErrorAlert(errorText: String) {
    Text(
        text = errorText,
        color = Color.Red,
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 20.dp, top = 10.dp)
    )
}

@Composable
fun TermsAndConditions() {
    CustomRow {
        Column {
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "By continuing, you agree to", fontSize = 14.sp, color = Color.Black)
            }
            Row {
                Text(
                    text = "Terms of Use",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "and", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Privacy Policy",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}




