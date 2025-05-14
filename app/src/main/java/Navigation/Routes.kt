package DI.Navigation

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Main = "main"
    const val Auth = "auth"
    const val Calendar = "calendar"
    const val ChatMessage = "chat_message/{otherUserId}"
    const val EditProfile = "edit_profile"

    val all = setOf(Login, Register, Main, Auth, Calendar, ChatMessage)
}