package DI.Composables.SettingSection

import DI.Composables.ProfileSection.AvatarImage
import DI.Composables.ProfileSection.BackgroundColor
import DI.Composables.ProfileSection.CardColor
import DI.Composables.ProfileSection.CurrencySettingsItem
import DI.Composables.ProfileSection.LanguageSelector
import DI.Composables.ProfileSection.MainColor
import DI.Composables.ProfileSection.TextPrimaryColor
import DI.Composables.ProfileSection.TextSecondaryColor
import DI.Models.UserInfo.Profile
import DI.Navigation.Routes
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.ProfileViewModel
import ViewModels.AuthViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun SettingsScreen(
    appNavController: NavController,
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel = hiltViewModel()
) {
    val profileResult = profileViewModel.profile.collectAsState()
    val profile = profileResult.value?.getOrNull()

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top App Bar
                TopAppBar()

                Spacer(modifier = Modifier.height(8.dp))

                // Profile Card
                ProfileHeaderCard(profile, navController, profileViewModel)

                Spacer(modifier = Modifier.height(24.dp))

                // Currency Settings
                SectionTitle(stringResource(R.string.currency_settings))
                CurrencySettingsItem(currencyConverterViewModel)

                Spacer(modifier = Modifier.height(24.dp))

                // Support and Info
                SectionTitle(stringResource(R.string.support_info))
                LanguageSelector()

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        authViewModel.logout()
                        appNavController.navigate(Routes.Auth) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = stringResource(R.string.log_out),
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.settings_screen_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )

        IconButton(onClick = { /* Handle settings */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = TextPrimaryColor
            )
        }
    }
}

@Composable
fun ProfileHeaderCard(
    profile: Profile?,
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val avatarVersion = profileViewModel.avatarVersion.collectAsState().value

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (profile == null) {
                Text(stringResource(R.string.no_profile_data), color = TextPrimaryColor)
                return@Column
            }

            // Profile Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MainColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Log.d("AvatarVersionProfile", avatarVersion)
                AvatarImage(profile.avatarUrl ?: "", avatarVersion)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Name
            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stylish User ID with Copy Button
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            val idText = profile.id
            val copiedMessage = stringResource(R.string.id_copied)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MainColor.copy(alpha = 0.1f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Icon(
                            imageVector = Icons.Default.PermIdentity,
                            contentDescription = null,
                            tint = MainColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.user_id),
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MainColor
                            )

                        )
                    }
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(idText))
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_id),
                            tint = MainColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MainColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = idText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimaryColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = { navController.navigate(Routes.EditProfile) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MainColor
                ),
                border = BorderStroke(1.dp, MainColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.edit_profile))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimaryColor
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

