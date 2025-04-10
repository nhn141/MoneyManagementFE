package DI.Models.Auth

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
)
