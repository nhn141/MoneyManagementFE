package DI.ViewModels

import DI.Models.Reports.ReportRequest
import DI.Models.UiEvent.UiEvent
import DI.Repositories.ReportRepository
import Utils.StringResourceProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    private val _reportResult = MutableStateFlow<Result<ResponseBody>?>(null)
    val reportResult: StateFlow<Result<ResponseBody>?> = _reportResult.asStateFlow()

    private val _reportEvent = MutableSharedFlow<UiEvent>()
    val reportEvent: SharedFlow<UiEvent> = _reportEvent.asSharedFlow()

    fun generateReport(request: ReportRequest) {
        viewModelScope.launch {
            val result = reportRepository.generateReport(request)
            _reportResult.value = result
            when {
                result.isSuccess -> {
                    result.getOrNull()?.let {
                        _reportEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.report_generated_success)))
                    }
                }
                else -> {
                    _reportEvent.emit(
                        UiEvent.ShowMessage(
                            stringProvider.getString(
                                R.string.report_generation_failed,
                                result.exceptionOrNull()?.message
                                    ?: stringProvider.getString(R.string.unknown_error)
                            )
                        )
                    )
                }
            }
        }
    }

    fun onReportSaved(saved: Boolean) {
        viewModelScope.launch {
            _reportEvent.emit(
                UiEvent.ShowMessage(
                    stringProvider.getString(
                        if (saved) R.string.report_saved_success else R.string.report_save_failed
                    )
                )
            )
        }
    }
}