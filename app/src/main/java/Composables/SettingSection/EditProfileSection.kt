package DI.Composables.ProfileSection

import DI.Models.UserInfo.UpdatedProfile
import DI.ViewModels.ProfileViewModel
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditProfileScreen(navController: NavController, profileViewModel: ProfileViewModel) {
    val context = LocalContext.current

    // Create a class for accessing strings in non-composable contexts
    val nonComposableStrings = remember {
        object {
            val profileUpdateSuccess = context.getString(R.string.profile_update_success)
            val profileUpdateFailed = context.getString(R.string.profile_update_failed)
            val avatarUploadSuccess = context.getString(R.string.avatar_upload_success)
            val avatarUploadFailed = context.getString(R.string.avatar_upload_failed)
            val passwordMinLength = context.getString(R.string.password_min_length)
            val passwordNumberRequired = context.getString(R.string.password_number_required)
            val passwordUppercaseRequired = context.getString(R.string.password_uppercase_required)
            val passwordAtRequired = context.getString(R.string.password_at_required)
            val passwordDifferent = context.getString(R.string.password_different)
            val passwordsNotMatch = context.getString(R.string.passwords_not_match)
        }
    }

    LaunchedEffect(Unit) {
        launch {
            profileViewModel.updatedProfileState.collect { updatedState ->
                val message =
                    if (updatedState) {
                        nonComposableStrings.profileUpdateSuccess
                    } else {
                        nonComposableStrings.profileUpdateFailed
                    }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val profileResult = profileViewModel.profile.collectAsState()
    val profile = profileResult.value?.getOrNull()

    // These are safe to use directly within the composable
    val noName = stringResource(R.string.no_name)
    val noEmail = stringResource(R.string.no_email)
    val noFirstName = stringResource(R.string.no_first_name)
    val noLastName = stringResource(R.string.no_last_name)

    val displayName = profile?.displayName ?: noName // Not editable
    val email = profile?.email ?: noEmail // Not editable
    var firstName by remember { mutableStateOf(profile?.firstName ?: noFirstName) }
    var lastName by remember { mutableStateOf(profile?.lastName ?: noLastName) }

    LaunchedEffect(profileResult.value) {
        if (profileResult.value?.isSuccess == true) {
            firstName = profile?.firstName ?: noFirstName
            lastName = profile?.lastName ?: noLastName
        }
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Validation states
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Form valid state
    val isFormValid =
        remember(
            firstName,
            lastName,
            currentPassword,
            newPassword,
            confirmPassword,
            currentPasswordError,
            newPasswordError,
            confirmPasswordError
        ) {
            (currentPassword.isEmpty() || currentPassword.length >= 6) &&
                    (newPassword.isEmpty() ||
                            (newPassword.length >= 6 && newPassword != currentPassword)) &&
                    (confirmPassword.isEmpty() || confirmPassword == newPassword) &&
                    currentPasswordError == null &&
                    newPasswordError == null &&
                    confirmPasswordError == null
        }

    val uploadAvatarResult = profileViewModel.uploadAvatarState.collectAsState()
    val avatarUrl = uploadAvatarResult.value?.getOrNull() ?: profile?.avatarUrl

    val avatarVersion = profileViewModel.avatarVersion.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                uri: Uri? ->
            uri?.let {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val file = uriToFile(it, context)
                        if (file != null) {
                            profileViewModel.uploadAvatar(file)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    nonComposableStrings.avatarUploadSuccess,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                nonComposableStrings.avatarUploadFailed,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }

    // Password validation functions
    val validateCurrentPassword: (String) -> Unit = { password ->
        currentPasswordError =
            when {
                password.isNotEmpty() && password.length < 6 ->
                    nonComposableStrings.passwordMinLength
                else -> null
            }
    }

    val validateNewPassword: (String) -> Unit = { password ->
        newPasswordError =
            when {
                password.isNotEmpty() && password.length < 6 ->
                    nonComposableStrings.passwordMinLength
                password.isNotEmpty() && !password.any { it.isDigit() } ->
                    nonComposableStrings.passwordNumberRequired
                password.isNotEmpty() && !password.any { it.isUpperCase() } ->
                    nonComposableStrings.passwordUppercaseRequired
                password.isNotEmpty() && !password.contains('@') ->
                    nonComposableStrings.passwordAtRequired
                password == currentPassword && password.isNotEmpty() ->
                    nonComposableStrings.passwordDifferent
                else -> null
            }
    }

    val validateConfirmPassword: (String) -> Unit = { password ->
        confirmPasswordError =
            when {
                password != newPassword -> nonComposableStrings.passwordsNotMatch
                else -> null
            }
    }

    // Use Scaffold to properly handle IME adjustments
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier =
            Modifier.fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding) // Use the padding from Scaffold
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Edit Profile Top Bar with card styling
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier =
                        Modifier.size(40.dp)
                            .clip(CircleShape)
                            .background(MainColor.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                            tint = MainColor
                        )
                    }

                    Text(
                        text = stringResource(R.string.edit_profile),
                        style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryColor
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            // Avatar Edit Section
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        // Profile Image
                        var isImageLoading by remember { mutableStateOf(false) }
                        Box(
                            modifier =
                            Modifier.size(130.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    MainColor.copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .background(MainColor.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoadingAvatar.value || isImageLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MainColor,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                if (avatarUrl != null) {
                                    AvatarImage(avatarUrl, avatarVersion) { isImageLoading = false }
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.profile_image),
                                        contentDescription =
                                        stringResource(R.string.default_avatar)
                                    )
                                }
                            }
                        }

                        // Edit Icon
                        Box(
                            modifier =
                            Modifier.size(44.dp)
                                .clip(CircleShape)
                                .background(MainColor)
                                .align(Alignment.BottomEnd)
                                .clickable { launcher.launch("image/*") }
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription =
                                stringResource(R.string.change_profile_picture),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // User Details Section
            Text(
                text = stringResource(R.string.user_details),
                style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Form Fields
                    EnhancedProfileTextField(
                        label = stringResource(R.string.first_name),
                        value = firstName,
                        onValueChange = { firstName = it },
                        leadingIcon = Icons.Default.Person
                    )

                    EnhancedProfileTextField(
                        label = stringResource(R.string.last_name),
                        value = lastName,
                        onValueChange = { lastName = it },
                        leadingIcon = Icons.Default.Person
                    )

                    // Non-editable fields with distinct styling
                    ReadOnlyProfileField(
                        label = stringResource(R.string.display_name),
                        value = displayName,
                        leadingIcon = Icons.Default.Badge
                    )

                    ReadOnlyProfileField(
                        label = stringResource(R.string.email),
                        value = email,
                        leadingIcon = Icons.Default.Email
                    )
                }
            }

            // Password Change Section
            Text(
                text = stringResource(R.string.change_password),
                style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    var currentPasswordVisible by remember { mutableStateOf(false) }
                    var newPasswordVisible by remember { mutableStateOf(false) }
                    var confirmPasswordVisible by remember { mutableStateOf(false) }

                    var currentPasswordFocusState by remember { mutableStateOf(false) }

                    // Current Password
                    PasswordTextField(
                        label = stringResource(R.string.current_password),
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        isVisible = currentPasswordVisible,
                        onVisibilityToggle = {
                            currentPasswordVisible = !currentPasswordVisible
                        },
                        errorMessage = currentPasswordError,
                        leadingIcon = Icons.Default.Lock,
                        modifier =
                        Modifier.onFocusChanged { focusState ->
                            // Validation only when losing focus
                            if (currentPasswordFocusState && !focusState.isFocused) {
                                validateCurrentPassword(currentPassword)
                            }
                            currentPasswordFocusState = focusState.isFocused
                        }
                    )

                    var newPasswordFocusState by remember { mutableStateOf(false) }

                    // New Password
                    PasswordTextField(
                        label = stringResource(R.string.new_password),
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            validateNewPassword(it)
                            if (confirmPassword.isNotEmpty()) {
                                validateNewPassword(newPassword)
                            }
                        },
                        isVisible = newPasswordVisible,
                        onVisibilityToggle = { newPasswordVisible = !newPasswordVisible },
                        errorMessage = newPasswordError,
                        leadingIcon = Icons.Default.VpnKey,
                        modifier =
                        Modifier.onFocusChanged { focusState ->
                            // Validation only when losing focus
                            if (newPasswordFocusState && !focusState.isFocused) {
                                validateNewPassword(newPassword)
                            }
                            newPasswordFocusState = focusState.isFocused
                        }
                    )

                    var confirmPasswordFocusState by remember { mutableStateOf(false) }

                    // Confirm Password
                    PasswordTextField(
                        label = stringResource(R.string.confirm_password),
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            validateConfirmPassword(it)
                        },
                        isVisible = confirmPasswordVisible,
                        onVisibilityToggle = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        },
                        errorMessage = confirmPasswordError,
                        leadingIcon = Icons.Default.VerifiedUser,
                        modifier =
                        Modifier.onFocusChanged { focusState ->
                            // Validation only when losing focus
                            if (confirmPasswordFocusState && !focusState.isFocused) {
                                validateConfirmPassword(confirmPassword)
                            }
                            confirmPasswordFocusState = focusState.isFocused
                        }
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    if (profile == null) {
                        Log.e("ProfileViewModel", "Profile is null")
                        return@Button
                    }

                    // Create updated profile
                    val updatedProfile =
                        UpdatedProfile(
                            FirstName = firstName,
                            LastName = lastName,
                            CurrentPassword = currentPassword,
                            NewPassword = newPassword,
                            ConfirmNewPassword = confirmPassword
                        )

                    // Handle password change separately
                    if (isFormValid) {
                        Log.d("Running updateProfile", "$updatedProfile")
                        profileViewModel.updateProfile(updatedProfile)
                    }
                },
                modifier =
                Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 24.dp)
                    .height(56.dp),
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    disabledContainerColor = MainColor.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid
            ) {
                Text(
                    text = stringResource(R.string.save_changes),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun EnhancedProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style =
            MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = leadingIcon, contentDescription = null, tint = MainColor)
            },
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Composable
fun ReadOnlyProfileField(label: String, value: String, leadingIcon: ImageVector) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style =
            MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier =
            Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MainColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = value,
                    style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimaryColor.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    errorMessage: String?,
    leadingIcon: ImageVector,
    modifier: Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style =
            MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = leadingIcon, contentDescription = null, tint = MainColor)
            },
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector =
                        if (isVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription =
                        if (isVisible) stringResource(R.string.hide_password)
                        else stringResource(R.string.show_password),
                        tint = MainColor
                    )
                }
            },
            visualTransformation =
            if (isVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorMessage != null,
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                errorBorderColor = Color.Red.copy(alpha = 0.7f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style =
                MaterialTheme.typography.bodySmall.copy(
                    color = Color.Red.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
