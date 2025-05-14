import DI.Composables.ProfileSection.AvatarImage
import DI.Composables.ProfileSection.BackgroundColor
import DI.Composables.ProfileSection.CardColor
import DI.Composables.ProfileSection.DividerColor
import DI.Composables.ProfileSection.MainColor
import DI.Composables.ProfileSection.TextPrimaryColor
import DI.Composables.ProfileSection.TextSecondaryColor
import DI.Composables.ProfileSection.uriToFile
import DI.Models.UserInfo.Profile
import DI.ViewModels.ProfileViewModel
import ViewModels.AuthViewModel
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun EditProfileScreen(
    profile: Profile,
    onSaveChanges: (Profile) -> Unit,
    onNavigateBack: () -> Unit,
    profileViewMode: ProfileViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf(profile.firstName) }
    var lastName by remember { mutableStateOf(profile.lastName) }
    var displayName by remember { mutableStateOf(profile.displayName) }
    var userName by remember { mutableStateOf(profile.userName) }
    var email by remember { mutableStateOf(profile.email) }

    val context = LocalContext.current
    val uploadState = profileViewMode.uploadState

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            val file = uriToFile(it, context)
            file?.let { profileViewMode.uploadAvatar(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Edit Profile Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = TextPrimaryColor
                )
            }

            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar Edit Section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MainColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarImage("https://firebasestorage.googleapis.com/v0/b/moneymanagementliveserver.firebasestorage.app/o/user_avatars%2F687baa6b-b371-4a0b-9eb2-d7f8ffc4b21f%2Favatar.jpg?alt=media")
                }

                // Edit Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MainColor)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            launcher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change profile picture",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Form Fields
        ProfileTextField(
            label = "First Name",
            value = firstName,
            onValueChange = { firstName = it }
        )

        ProfileTextField(
            label = "Last Name",
            value = lastName,
            onValueChange = { lastName = it }
        )

        ProfileTextField(
            label = "Display Name",
            value = displayName,
            onValueChange = { displayName = it }
        )

        ProfileTextField(
            label = "Username",
            value = userName,
            onValueChange = { userName = it }
        )

        ProfileTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                // Create updated profile
                val updatedProfile = profile.copy(
                    firstName = firstName,
                    lastName = lastName,
                    displayName = displayName,
                    userName = userName,
                    email = email
                )
                onSaveChanges(updatedProfile)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
        ) {
            Text(
                text = "Save Changes",
                fontSize = 16.sp,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryColor
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainColor,
                cursorColor = MainColor
            )
        )
    }
}

@Composable
fun ProfileScreenWithEditing(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    var showEditScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.getProfile()
    }

    val profileResult = profileViewModel.profile.collectAsState()
    var profile = profileResult.value?.getOrNull()

    if (showEditScreen && profile != null) {
        EditProfileScreen(
            profile = profile,
            onSaveChanges = { updatedProfile ->
                profile = updatedProfile
                showEditScreen = false
            },
            onNavigateBack = { showEditScreen = false }
        )
    } else {
        ProfileScreen(
            profile = profile,
            onEditProfileClick = { showEditScreen = true }
        )
    }
}

@Composable
fun ProfileScreen(
    profile: Profile?,
    onEditProfileClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {

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
            ProfileHeaderCard(profile, onEditProfileClick)

            Spacer(modifier = Modifier.height(24.dp))

            // Account Settings
            SectionTitle("Account Settings")
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Personal Information",
                subtitle = "Update your personal details"
            )
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Security",
                subtitle = "Password and authentication"
            )
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Manage your alerts and notifications"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Financial Settings
            SectionTitle("Financial Settings")
            SettingsItem(
                icon = Icons.Default.CreditCard,
                title = "Payment Methods",
                subtitle = "Manage your cards and bank accounts"
            )
            SettingsItem(
                icon = Icons.Default.Savings,
                title = "Savings Goals",
                subtitle = "Set and track your financial goals"
            )
            SettingsItem(
                icon = Icons.Default.BarChart,
                title = "Budget Categories",
                subtitle = "Customize your spending categories"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Support and Info
            SectionTitle("Support & Info")
            SettingsItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                subtitle = "FAQs and contact information"
            )
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "App version and legal information"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
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
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )

        IconButton(onClick = { /* Handle settings */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextPrimaryColor
            )
        }
    }
}

@Composable
fun ProfileHeaderCard(profile: Profile?, onEditProfileClick: () -> Unit) {
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

            if(profile == null) {
                Text("No Profile Data", color = TextPrimaryColor)
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

                AvatarImage("https://firebasestorage.googleapis.com/v0/b/moneymanagementliveserver.firebasestorage.app/o/user_avatars%2F687baa6b-b371-4a0b-9eb2-d7f8ffc4b21f%2Favatar.jpg?alt=media")
//                Image(
//                    painter = painterResource(id = R.drawable.profile_image),
//                    contentDescription = "Profile picture",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
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

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Stats / Finance Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Budget", "$3,450")
                VerticalDivider()
                StatItem("Savings", "$12,580")
                VerticalDivider()
                StatItem("Spent", "$1,245")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = { onEditProfileClick() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MainColor
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MainColor)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Edit Profile")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondaryColor
            )
        )
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(DividerColor)
    )
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

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MainColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MainColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimaryColor
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryColor
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = TextSecondaryColor
            )
        }
    }
}

@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreenWithEditing()
    }
}