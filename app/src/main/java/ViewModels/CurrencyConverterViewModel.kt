package DI.ViewModels

import DI.CurrencyApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyConverterViewModel : ViewModel() {
    private val _exchangeRate = MutableStateFlow<Double?>(null)
    val exchangeRate: StateFlow<Double?> = _exchangeRate

    private val _isVND = MutableStateFlow(true)
    val isVND: StateFlow<Boolean> = _isVND

    init {
        loadExchangeRate()
    }

    private fun loadExchangeRate() {
        viewModelScope.launch {
            val rate = CurrencyApi.getUsdToVndRate()
            _exchangeRate.value = rate
        }
    }

    fun toggleCurrency() {
        _isVND.value = !_isVND.value
    }

    fun convert(amount: Double, toVND: Boolean): Double? {
        val rate = _exchangeRate.value ?: return null
        return if (toVND) amount * rate else amount / rate
    }

    fun toVND(amount: Double): Double? {
        return if (_isVND.value) amount else convert(amount, true)
    }
}
