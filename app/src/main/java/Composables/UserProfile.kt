package Composables

data class Profile(
    val username: String,
    val phone: String,
    val email: String,
    val pushNotificationsEnabled: Boolean,
    val darkThemeEnabled: Boolean
)