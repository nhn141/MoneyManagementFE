package DI.Navigation

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Main = "main"
    const val Auth = "auth"
    const val Category = "category"
    const val Wallet = "wallet"
    const val Report = "report"
    const val Friend = "friend"
    const val Calendar = "calendar"
    const val Chat = "chat"
    const val ChatMessage = "chat_message/{friendId}"
    const val EditProfile = "edit_profile"
    const val FriendProfile = "friend_profile/{friendId}"
    const val AddTransaction = "add_transaction"
    const val TransactionDetail = "transaction_detail/{transactionId}"
    const val TransactionEdit = "transaction_edit/{transactionId}"
    const val GroupChatMessage = "group_chat_message/{groupId}"
    const val GroupFund = "group_fund"

    val all = setOf(Login, Register, Main, Auth, Calendar, ChatMessage)
}