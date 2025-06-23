package DI.ViewModels

import DI.Models.Reports.ReportRequest
import DI.Repositories.ReportRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _reportResult = MutableStateFlow<Result<ResponseBody>?>(null)
    val reportResult: StateFlow<Result<ResponseBody>?> = _reportResult

    fun generateReport(request: ReportRequest) {
        viewModelScope.launch {
            val result = reportRepository.generateReport(request)
            _reportResult.value = result
        }
    }
}
