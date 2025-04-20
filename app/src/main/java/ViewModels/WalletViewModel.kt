package DI.ViewModels

import DI.Models.Wallet
import DI.Repositories.WalletRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {
    private val _wallets = MutableStateFlow<Result<List<Wallet>>?>(null)
    val wallets: StateFlow<Result<List<Wallet>>?> = _wallets

    fun fetchWallets() {
        viewModelScope.launch {
            val result = repository.getWallets()
            _wallets.value = result
        }
    }

    private val _walletState = MutableStateFlow<Result<Unit>?>(null)
    val walletState: StateFlow<Result<Unit>?> = _walletState

    fun createWallet(wallet: Wallet) {
        viewModelScope.launch {
            val result = repository.createWallet(wallet)
            _walletState.value = result
        }
    }
}