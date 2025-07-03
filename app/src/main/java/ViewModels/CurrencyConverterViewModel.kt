package DI.ViewModels

import DI.CurrencyApi
import Utils.CurrencyManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _exchangeRate = MutableStateFlow<Double?>(null)
    val exchangeRate: StateFlow<Double?> = _exchangeRate

    private val _isVND = MutableStateFlow(true)
    val isVND: StateFlow<Boolean> = _isVND

    init {
        loadCurrencyPreference()
        loadExchangeRate()
    }

    fun refreshAllData() {
        loadCurrencyPreference()
        loadExchangeRate()
    }

    fun loadCurrencyPreference() {
        // Try to get currency preference, fallback to VND if not set
        val pref = CurrencyManager.getCurrencyPreference(context)
        _isVND.value = (pref == null || pref == "vnd")
    }

    fun loadExchangeRate() {
        viewModelScope.launch {
            val rate = CurrencyApi.getUsdToVndRate()
            _exchangeRate.value = rate
            Log.d("CurrencyConverterViewModel", "Exchange rate: ${exchangeRate.value}")
        }
    }


    fun toggleCurrency() {
        _isVND.value = !_isVND.value
        // Save to preferences
        CurrencyManager.setCurrencyPreference(context, if (_isVND.value) "vnd" else "usd")
    }

    fun convert(amount: Double, toVND: Boolean): Double? {
        val rate = _exchangeRate.value ?: return null
        return if (toVND) amount * rate else amount / rate
    }

    fun toVND(amount: Double): Double? {
        return if (_isVND.value) amount else convert(amount, true)
    }
}
