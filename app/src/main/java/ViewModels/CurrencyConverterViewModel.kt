package DI.ViewModels

import DI.CurrencyApi
import Utils.LanguageManager
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

    fun refreshAllData() {
        loadCurrentLanguage()
        loadExchangeRate()
    }

    fun loadCurrentLanguage() {
        val language = LanguageManager.getLanguagePreferenceSync(context)
        _isVND.value = (language == "vi")
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
    }

    fun convert(amount: Double, toVND: Boolean): Double? {
        val rate = _exchangeRate.value ?: return null
        return if (toVND) amount * rate else amount / rate
    }

    fun toVND(amount: Double): Double? {
        return if (_isVND.value) amount else convert(amount, true)
    }
}
