package DI.Composables.ProfileSection

import DI.ViewModels.ProfileViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = hiltViewModel()) {
    val profile = profileViewModel.profile.value

    ProfileDetail(profile, profileViewModel)
}