package DI.ViewModels

import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.Models.UiEvent.UiEvent
import DI.Repositories.FriendRepository
import Utils.StringResourceProvider
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    private val _friends = MutableStateFlow<Result<List<Friend>>?>(null)
    val friends: StateFlow<Result<List<Friend>>?> = _friends

    private val _addFriendEvent = MutableSharedFlow<UiEvent>()
    val addFriendEvent = _addFriendEvent.asSharedFlow()

    private val _friendRequests = MutableStateFlow<Result<List<FriendRequest>>?>(null)
    val friendRequests: StateFlow<Result<List<FriendRequest>>?> = _friendRequests

    private val _acceptFriendRequestEvent = MutableSharedFlow<UiEvent>()
    val acceptFriendRequestEvent = _acceptFriendRequestEvent.asSharedFlow()

    private val _rejectFriendRequestEvent = MutableSharedFlow<UiEvent>()
    val rejectFriendRequestEvent = _rejectFriendRequestEvent.asSharedFlow()

    private val _deleteFriendEvent = MutableSharedFlow<UiEvent>()
    val deleteFriendEvent = _deleteFriendEvent.asSharedFlow()

    fun refreshAllData() {
        getAllFriends()
        getFriendRequests()
    }

    fun getAllFriends() {
        Log.d("FriendViewModel", "getAllFriends called")
        viewModelScope.launch {
            val result = friendRepository.getAllFriends()
            _friends.value = result
            Log.d("FriendViewModel", "Friends: $result")
        }
    }

    fun addFriend(request: AddFriendRequest) {
        viewModelScope.launch {
            val result = friendRepository.addFriend(request)
            if (result.isSuccess) {
                _addFriendEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.friend_request_sent)))
            } else {
                _addFriendEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: R.string.unknown_error
                        )
                    )
                )
            }
        }
    }

    fun getFriendRequests() {
        viewModelScope.launch {
            val result = friendRepository.getFriendRequests()
            _friendRequests.value = result
        }
    }

    fun acceptFriendRequest(friendId: String) {
        viewModelScope.launch {
            val result = friendRepository.acceptFriendRequest(friendId)
            if (result.isSuccess) {
                _acceptFriendRequestEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.friend_request_accepted)))
                getAllFriends()
                getFriendRequests()
            } else {
                _acceptFriendRequestEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: R.string.unknown_error
                        )
                    )
                )
            }

        }
    }

    fun rejectFriendRequest(friendId: String) {
        viewModelScope.launch {
            val result = friendRepository.rejectFriendRequest(friendId)
            if (result.isSuccess) {
                _rejectFriendRequestEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.friend_request_rejected)))
                getFriendRequests()
            } else {
                _rejectFriendRequestEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: R.string.unknown_error
                        )
                    )
                )
            }

        }
    }

    fun deleteFriend(friendId: String) {
        viewModelScope.launch {
            val result = friendRepository.deleteFriend(friendId)
            if (result.isSuccess) {
                _deleteFriendEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.friend_deleted)))
                // Refresh the friends list after deletion
                getAllFriends()
            } else {
                _deleteFriendEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: R.string.unknown_error
                        )
                    )
                )

            }
        }
    }
}