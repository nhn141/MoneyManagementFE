package DI.Models

data class Wallet(
    val walletID: String,
    val userID: String? = null,
    val walletName: String,
    val balance: Double
)