package DI.ViewModels
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UpdatedProfile
import DI.Repositories.ProfileRepository
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uploadAvatarState = MutableSharedFlow<Boolean>()
    val uploadAvatarState = _uploadAvatarState.asSharedFlow()

    private val _profile = MutableStateFlow<Result<Profile>?>(null)
    val profile: StateFlow<Result<Profile>?> = _profile

    private val _updatedProfileState = MutableSharedFlow<Boolean>()
    val updatedProfileState = _updatedProfileState.asSharedFlow()

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            val result = profileRepository.uploadAvatar(file)
            if(result.isSuccess) {
                _uploadAvatarState.emit(true)
            } else {
                _uploadAvatarState.emit(false)
            }
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            val result = profileRepository.getProfile()
            _profile.value = result
            Log.d("GetProfile", "Profile result: $result")
        }
    }

    fun updateProfile(updatedProfile: UpdatedProfile) {
        viewModelScope.launch {
            val result = profileRepository.updateProfile(updatedProfile)
            if(result.isSuccess) {
                _updatedProfileState.emit(true)
            } else {
                _updatedProfileState.emit(false)
            }
        }
    }

}

