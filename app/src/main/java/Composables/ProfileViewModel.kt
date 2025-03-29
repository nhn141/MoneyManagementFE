package Composables
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _profile = mutableStateOf(Profile("", "", "", true, false))
    val profile: State<Profile> get() = _profile

    fun updateUsername(username: String) {
        _profile.value = _profile.value.copy(username = username)
    }

    fun updatePhone(phone: String) {
        _profile.value = _profile.value.copy(phone = phone)
    }

    fun updateEmail(email: String) {
        _profile.value = _profile.value.copy(email = email)
    }

    fun togglePushNotifications() {
        _profile.value = _profile.value.copy(pushNotificationsEnabled = !_profile.value.pushNotificationsEnabled)
    }

    fun toggleDarkTheme() {
        _profile.value = _profile.value.copy(darkThemeEnabled = !_profile.value.darkThemeEnabled)
    }
}