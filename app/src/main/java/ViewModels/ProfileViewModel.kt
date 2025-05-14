package DI.ViewModels
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UploadState
import DI.Repositories.ProfileRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    var uploadState by mutableStateOf<UploadState>(UploadState.Idle)
       private set

    private val _profile = MutableStateFlow<Result<Profile>?>(null)
    val profile: StateFlow<Result<Profile>?> = _profile

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            uploadState = UploadState.Loading
            val result = profileRepository.uploadAvatar(file)
            uploadState = if (result.isSuccess) {
                UploadState.Success
            } else {
                UploadState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        uploadState = UploadState.Idle
    }

    fun getProfile() {
        viewModelScope.launch {
            val result = profileRepository.getProfile()
            _profile.value = result
        }
    }

}

