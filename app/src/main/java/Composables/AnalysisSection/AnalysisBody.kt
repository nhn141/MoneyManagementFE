package DI.Composables.AnalysisSection

import DI.Navigation.Routes
import DI.ViewModels.AnalysisViewModel
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelProperties

@Composable
fun AnalysisBody(
    navController: NavController,
    analysisViewModel: AnalysisViewModel = hiltViewModel()
) {
    val periodGraphResult = analysisViewModel.periodGraph.collectAsState()

    val periods = listOf("Daily", "Weekly", "Monthly", "Yearly")
    var selectedPeriod by remember { mutableStateOf(0) }

    val selectedPeriodLabel = periods[selectedPeriod]
    val selectedData = periodGraphResult.let { result ->
        if(result.value?.isSuccess == true) {
            result.value?.getOrNull()?.dataByPeriod?.get(selectedPeriodLabel)
        } else {
            null
        }
    }

    val labels = selectedData?.labels ?: emptyList()
    val incomeList = selectedData?.income ?: emptyList()
    val expenseList = selectedData?.expenses ?: emptyList()
    val totalIncome = selectedData?.totalIncome?.let { "$${String.format("%,.2f", it)}" } ?: "--"
    val totalExpenses = selectedData?.totalExpenses?.let { "$${String.format("%,.2f", it)}" } ?: "--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, end = 25.dp, top = 25.dp, bottom = 10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Period selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFDFF7E2))
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            periods.forEachIndexed { index, period ->
                Box(
                    modifier = Modifier.clickable { selectedPeriod = index }
                        .clip(RoundedCornerShape(16.dp))
                        .background(if(selectedPeriod == index) Color(0xFF53dba9) else Color.Transparent)
                        .padding(16.dp)

                ) {
                    Text(
                        text = period, fontSize = 16.sp,
                        color = if(selectedPeriod == index) Color.White else Color.Black,
                        fontWeight = if(selectedPeriod == index) FontWeight.W500 else FontWeight.W400,
                    )
                }
            }
        }

        // Bar chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFDFF7E2))
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Income & Expenses", fontSize = 16.sp ,fontWeight = FontWeight.W500)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF53dba9))
                                .padding(5.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = null,
                                tint = Color(0xFF093030)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF53dba9))
                                .padding(5.dp)
                                .clickable {
                                    navController.navigate(Routes.Calendar)
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = null,
                                tint = Color(0xFF093030),
                            )
                        }
                    }
                }
                // Box container to set fixed height for the chart (solved scrollable column bug)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    IncomeExpensesBarChart(
                        incomeValues = incomeList,
                        expenseValues = expenseList,
                        labels = labels
                    )
                }
            }
        }

        // Summary values
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PeriodTotalTransaction(
                icon = R.drawable.ic_income,
                tintColor = Color(0xFF53dba9),
                title = "Income",
                total = totalIncome
            )
            PeriodTotalTransaction(
                icon = R.drawable.ic_expense,
                tintColor = Color(0xFF0068FF),
                title = "Expenses",
                total = totalExpenses
            )
        }
        Text("My Targets")
    }
}

@Composable
fun PeriodTotalTransaction(
    icon: Int,
    tintColor: Color,
    title: String,
    total: String
) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(40.dp)
        )
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W500
        )
        Text(
            total,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun IncomeExpensesBarChart(
    incomeValues: List<Double>,
    expenseValues: List<Double>,
    labels: List<String>
) {
    val size = listOf(incomeValues.size, expenseValues.size, labels.size).minOrNull() ?: 0

    // 🔒 Guard against empty lists
    if (size == 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No data to display", color = Color.Gray)
        }
        return
    }

    val chartData = remember(incomeValues, expenseValues, labels) {
        (0 until size).map { index ->
            Bars(
                label = labels[index],
                values = listOf(
                    Bars.Data(
                        label = "Income",
                        value = incomeValues[index],
                        color = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                        )
                    ),
                    Bars.Data(
                        label = "Expense",
                        value = expenseValues[index],
                        color = SolidColor(Color(0xFF0068FF))
                    )
                )
            )
        }
    }

    ColumnChart(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 18.dp),
        data = chartData,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topLeft = 6.dp, topRight = 6.dp),
            spacing = 4.dp,
            thickness = 8.dp
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        labelProperties = LabelProperties(
            enabled = true,
            rotation = LabelProperties.Rotation(degree = 0f)
        )
    )
}
