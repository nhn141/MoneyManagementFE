package Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileDetail(profile: Profile, profileViewModel: ProfileViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
        Text(
            text = profile.username.ifEmpty { "John Smith" },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "ID: 25030024",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Account Settings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Username TextField
        TextFieldSection(
            label = "Username",
            value = profile.username,
            onValueChange = { profileViewModel.updateUsername(it) },
            placeholder = "John Smith"
        )
        // Phone TextField
        TextFieldSection(
            label = "Phone",
            value = profile.phone,
            onValueChange = { profileViewModel.updatePhone(it) },
            placeholder = "+44 555 5555 55"
        )
        // Email TextField
        TextFieldSection(
            label = "Email Address",
            value = profile.email,
            onValueChange = { profileViewModel.updateEmail(it) },
            placeholder = "example@example.com"
        )

        // Push Notifications Switch
        SettingsSwitch(
            label = "Push Notifications",
            isChecked = profile.pushNotificationsEnabled,
            onCheckedChange = { profileViewModel.togglePushNotifications() }
        )

        // Dark Theme Switch
        SettingsSwitch(
            label = "Turn Dark Theme",
            isChecked = profile.darkThemeEnabled,
            onCheckedChange = { profileViewModel.toggleDarkTheme() }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { /* Handle update profile action */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D09E))
        ) {
            Text("Update Profile", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TextFieldSection(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.fillMaxWidth()
    )
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0XFFDFF7E2))
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun SettingsSwitch(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50),
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}
