package Composables

import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    val profile = profileViewModel.profile.value

    ProfileDetail(profile, profileViewModel)
}