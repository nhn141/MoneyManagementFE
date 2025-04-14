package DI.Composables.AuthSection

import AuthScreen
import AuthTextField
import CustomButton
import CustomRow
import ViewModels.AuthViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(viewModel: AuthViewModel = hiltViewModel(), onNavigateToLogin: () -> Unit) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
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

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val invalidSpacer = 10
    val validSpacer = 25

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AuthScreen("Create Account", 0.12f) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 35.dp, end = 35.dp, top = 10.dp)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "First Name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W500,
                        color = colorResource(R.color.textColor),
                        modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                    )
                    AuthTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            firstNameError = null },
                        placeholder = "Ex: John",
                        isError = firstNameError != null)
                    if (firstNameError != null) {
                        ErrorAlert(firstNameError!!)
                        SpacerForEachField(invalidSpacer)
                    } else {
                        SpacerForEachField(validSpacer)
                    }



                    Text(
                        text = "Last Name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W500,
                        color = colorResource(R.color.textColor),
                        modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                    )
                    AuthTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            lastNameError = null },
                        placeholder = "Ex: Smith",
                        isError = lastNameError != null)
                    if (lastNameError != null) {
                        ErrorAlert(lastNameError!!)
                        SpacerForEachField(invalidSpacer)
                    } else {
                        SpacerForEachField(validSpacer)
                    }


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
                        placeholder = "Ex: joinsmith@example.com",
                        isError = emailError != null)
                    if (emailError != null) {
                        ErrorAlert(emailError!!)
                        SpacerForEachField(invalidSpacer)
                    } else {
                        SpacerForEachField(validSpacer)
                    }


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
                        placeholder = "Ex: 123@John",
                        isPassword = true,
                        isError = passwordError != null)
                    if (passwordError != null) {
                        ErrorAlert(passwordError!!)
                        SpacerForEachField(invalidSpacer)
                    } else {
                        SpacerForEachField(validSpacer)
                    }

                    Text(
                        text = "Confirm Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W500,
                        color = colorResource(R.color.textColor),
                        modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                    )
                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = null },
                        placeholder = "Ex: 123@John",
                        isPassword = true,
                        isError = confirmPasswordError != null)
                    if (confirmPasswordError != null) {
                        ErrorAlert(confirmPasswordError!!)
                    }
                }

                Column (
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                ) {
                    CustomRow {
                        CustomButton("Sign Up") {
                            // Validate all fields
                            val firstNameResult = Validator.validateField("firstName", firstName)
                            val lastNameResult = Validator.validateField("lastName", lastName)
                            val emailResult = Validator.validateField("email", email)
                            val passwordResult = Validator.validateField("password", password)
                            val confirmPasswordResult = Validator.validateField("confirmPassword", password, confirmPassword)

                            // Update error states
                            firstNameError = firstNameResult.errorMessage
                            lastNameError = lastNameResult.errorMessage
                            emailError = emailResult.errorMessage
                            passwordError = passwordResult.errorMessage
                            confirmPasswordError = confirmPasswordResult.errorMessage

                            // Check if all fields are valid
                            if(firstNameResult.isValid &&
                                lastNameResult.isValid &&
                                emailResult.isValid &&
                                passwordResult.isValid &&
                                confirmPasswordResult.isValid
                            ) {
                                viewModel.register(firstName, lastName, email, password, confirmPassword)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    CustomRow {
                        Text(text = "Already have an account?", fontSize = 14.sp, color = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log In",
                            fontSize = 16.sp,
                            color = Color.Blue.copy(0.5f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
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
fun SpacerForEachField(value: Int) {
    Spacer(modifier = Modifier.height(value.dp))
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




