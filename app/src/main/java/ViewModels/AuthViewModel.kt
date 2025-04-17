package ViewModels

import DI.API.TokenHandler.AuthStorage
import Repositories.AuthRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val app : Application
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _loginState = MutableStateFlow<Result<String>?>(null)
    val loginState: StateFlow<Result<String>?> = _loginState

    private val _registerState = MutableStateFlow<Result<String>?>(null)
    val registerState: StateFlow<Result<String>?> = _registerState

    private val _refreshTokenState = MutableStateFlow<Result<String>?>(null)
    val refreshTokenState: StateFlow<Result<String>?> = _refreshTokenState

    init {
        viewModelScope.launch {
            _isAuthenticated.value = AuthStorage.getToken(app) != null
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.onSuccess { token ->
                AuthStorage.storeToken(app, token)
                _isAuthenticated.value = true
            }
            _loginState.value = result
            Log.d("Login", result.toString())
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun register(firstName: String, lastName: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            val result = repository.register(firstName, lastName, email, password, confirmPassword)
            _registerState.value = result
            Log.d("Register", result.toString())
        }
    }

    fun resetRegisterState() {
        _registerState.value = null
    }

    fun refreshToken() {
        viewModelScope.launch {
            val currentToken = AuthStorage.getToken(app) ?: run {
                Log.d("RefreshTokenChecking", AuthStorage.getToken(app).toString())
                _isAuthenticated.value = false
                _refreshTokenState.value = Result.failure(Exception("No token available"))
                return@launch
            }
            val result = repository.refreshToken(currentToken)
            result.onSuccess { refreshToken ->
                AuthStorage.clearToken(app)
                AuthStorage.storeToken(app, refreshToken)
                _isAuthenticated.value = true
            }.onFailure {
                AuthStorage.clearToken(app)
                _isAuthenticated.value = false
            }
            _refreshTokenState.value = result
            Log.d("Refresh Token", result.toString())
        }
    }

    fun logout() {
        viewModelScope.launch {
            AuthStorage.clearToken(app)
            _isAuthenticated.value = false
            _loginState.value = null
            _refreshTokenState.value = null
        }
    }
}