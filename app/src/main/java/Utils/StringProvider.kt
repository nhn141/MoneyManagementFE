package DI.Utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moneymanagement_frontend.R

@Composable
fun rememberAppStrings(): AppStrings {
    return AppStrings(
        // Calendar Analysis
        aggregate = stringResource(R.string.aggregate),
        pieCharts = stringResource(R.string.pie_charts),
        noDataAvailable = stringResource(R.string.no_data_available),
        selectDateRangeViewAnalytics = stringResource(R.string.select_date_range),
    )
}

data class AppStrings(
    // Calendar Analysis
    val aggregate: String = "",
    val pieCharts: String = "",
    val noDataAvailable: String = "",
    val selectDateRangeViewAnalytics: String = "",
) {

}