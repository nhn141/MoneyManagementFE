package ViewModels

import Models.User
import Repositories.AuthRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<String>?>(null)
    val loginState: StateFlow<Result<String>?> = _loginState

    private val _registerState = MutableStateFlow<Result<String>?>(null)
    val registerState: StateFlow<Result<String>?> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = result
            Log.d("Login", result.toString());
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.register(username, email, password)
            _registerState.value = result
            Log.d("Register", result.toString());
        }
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}