//import DI.Models.GroupFund.GroupFundDto
//import DI.Models.UiEvent.UiEvent
//import DI.ViewModels.GroupFundViewModel
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import java.time.LocalDateTime
//import java.util.*
//
//class FakeGroupFundViewModel : ViewModel() {
//    private val fakeData = listOf(
//        GroupFundDto(
//            groupFundID = UUID.randomUUID().toString(),
//            groupID = UUID.randomUUID().toString(),
//            totalFundsIn = 1000.0,
//            totalFundsOut = 200.0,
//            balance = 800.0,
//            createdAt = LocalDateTime.now().toString(),
//            updatedAt = LocalDateTime.now().toString(),
//        ),
//        GroupFundDto(
//            groupFundID = UUID.randomUUID().toString(),
//            groupID = UUID.randomUUID().toString(),
//            totalFundsIn = 500.0,
//            totalFundsOut = 100.0,
//            balance = 400.0,
//            createdAt = LocalDateTime.now().toString(),
//            updatedAt = LocalDateTime.now().toString(),
//        )
//    )
//
//    override fun onCleared() {} // Optional
//
//    val groupFunds = MutableStateFlow<Result<List<GroupFundDto>>?>(Result.success(fakeData))
//
//    val addGroupFundEvent = MutableSharedFlow<UiEvent?>()
//    val updateGroupFundEvent = MutableSharedFlow<UiEvent?>()
//    val deleteGroupFundEvent = MutableSharedFlow<UiEvent?>()
//
//    fun fetchGroupFunds(groupId: String) {}
//    fun createGroupFund(dto: Any) {}
//    fun deleteGroupFund(id: String, groupId: String) {}
//}
