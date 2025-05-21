package DI.Composables.AnalysisSection

import DI.Composables.GeneralTemplate
import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Analysis.CategoryBreakdownPieData
import DI.Models.Analysis.DateSelection
import DI.ViewModels.AnalysisViewModel
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneymanagement_frontend.R
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(analysisViewModel: AnalysisViewModel) {
    GeneralTemplate(
        contentHeader = { CalendarHeader() },
        contentBody = { CalendarBody(analysisViewModel) },
        fraction = 0.1f
    )
}

@Composable
fun CalendarHeader() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Calendar", fontSize = 24.sp, fontWeight = FontWeight.W600)
    }
}

@Composable
fun CalendarBody(analysisViewModel: AnalysisViewModel) {
    val currentDate = LocalDate.of(2025, 4, 21)
    val startMonth = remember { YearMonth.of(2000, 1) }
    val endMonth = remember { YearMonth.of(2100, 12) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentDate.yearMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val months = (1..12).map { month ->
        LocalDate.of(2025, month, 1).month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    val years = (1950..2100).map { it.toString() }
    var selectedMonth by remember { mutableStateOf(currentDate.month.toString()) }
    var selectedYear by remember { mutableStateOf(currentDate.year.toString()) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.firstVisibleMonth) {
        val visibleMonth = state.firstVisibleMonth.yearMonth
        selectedMonth = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        selectedYear = visibleMonth.year.toString()
    }

    var selection by remember { mutableStateOf(DateSelection()) }
    LaunchedEffect(selection) {
        Log.d("DateSelection", "Start: ${selection.start}, End: ${selection.end}")
        val startDate = selection.start?.let { selection.formatDate(it) }
        val endDate = selection.end?.let { selection.formatDate(it) }
        Log.d("StartDate", "Start Date: $startDate")
        Log.d("EndDate", "End Date: $endDate")
        analysisViewModel.getCategoryBreakdown(startDate.toString(), endDate.toString())
    }

    val categoryBreakdownResult by analysisViewModel.categoryBreakdown.collectAsState()
    val categoryBreakdown = remember { mutableStateListOf<CategoryBreakdown?>(null) }
    LaunchedEffect(categoryBreakdownResult) {
        categoryBreakdownResult?.let { result ->
            result.onSuccess { data ->
                categoryBreakdown.clear()
                categoryBreakdown.addAll(data)
            }.onFailure {
                Log.d("CategoryBreakdown", "Error fetching category breakdown data")
            }
        }
    }
    Log.d("CategoryBreakdownValue", "categoryBreakdown: $categoryBreakdown")

    var statisticsMode by remember { mutableStateOf("Aggregate") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(top = 20.dp, start = 55.dp, end = 55.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Month Dropdown
                Box {
                    TextButton(onClick = { monthExpanded = !monthExpanded }) {
                        Text(selectedMonth, color = Color(0xFF00D09E), fontSize = 20.sp)
                        Icon(Icons.Default.ArrowDropDown, "Select Month", tint = Color(0xFF00D09E))
                    }
                    if (monthExpanded) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(0, 110)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .heightIn(max = 200.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF00D09E), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    months.forEach { month ->
                                        Text(
                                            text = month,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clickable {
                                                    selectedMonth = month
                                                    monthExpanded = false
                                                    val monthIndex = months.indexOf(month) + 1
                                                    coroutineScope.launch {
                                                        state.scrollToMonth(YearMonth.of(selectedYear.toInt(), monthIndex))
                                                    }
                                                },
                                            color = Color(0xFF00D09E),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

                // Year Dropdown
                Box {
                    TextButton(onClick = { yearExpanded = !yearExpanded }) {
                        Text(selectedYear, color = Color(0xFF00D09E), fontSize = 20.sp)
                        Icon(Icons.Default.ArrowDropDown, "Select Year", tint = Color(0xFF00D09E))
                    }
                    if (yearExpanded) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(0, 110)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .heightIn(max = 200.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF00D09E), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    years.forEach { year ->
                                        Text(
                                            text = year,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clickable {
                                                    selectedYear = year
                                                    yearExpanded = false
                                                    val monthIndex = months.indexOf(selectedMonth) + 1
                                                    coroutineScope.launch {
                                                        state.scrollToMonth(YearMonth.of(year.toInt(), monthIndex))
                                                    }
                                                },
                                            color = Color(0xFF00D09E),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Days of Week Header
            DaysOfWeekTitle(daysOfWeek = daysOfWeek)

            // Calendar Grid
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    Day(
                        day = day,
                        selection = selection
                    ) { clickedDay ->
                        selection = handleRangeSelection(clickedDay.date, selection)
                    }
                },

            )
        }
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticsModeButton(
                    mode = "Aggregate",
                    selectedMode = statisticsMode
                ) { selected ->
                    statisticsMode = selected
                }
                StatisticsModeButton(
                    mode = "Pie Charts",
                    selectedMode = statisticsMode
                ) { selected ->
                    statisticsMode = selected
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if(statisticsMode == "Aggregate") {
                CategoryAggregateSection(categoryBreakdown, selection)
            } else {
                CategoryBreakdownPieChart(categoryBreakdown)
            }
        }
    }
}

@Composable
fun StatisticsModeButton(
    mode: String,
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (mode == selectedMode) Color(0xFF00D09E) else Color(0xFFDFF7E2))
            .clickable { onModeSelected(mode) }
    ) {
        Text(
            text = mode,
            fontWeight = FontWeight.W500,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 32.dp),
        )
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = Color(0xFF3299FF),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}

@Composable
fun Day(
    day: CalendarDay,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit
) {
    val isInRange = selection.isDateInRange(day.date)
    val isStart = selection.start?.equals(day.date) == true
    val isEnd = selection.end?.equals(day.date) == true
    val isSelected = isStart || isEnd || isInRange

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                when {
                    isStart -> Modifier
                        .background(Color(0xFF2E7D32), RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                    isEnd -> Modifier
                        .background(Color(0xFF2E7D32), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                    isInRange -> Modifier
                        .background(Color(0xFFA5D6A7))
                    else -> Modifier
                }
            )
            .clickable(
                enabled = true,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when {
                isSelected -> Color.White
                day.position != DayPosition.MonthDate -> Color.Gray
                else -> Color.Black
            },
            fontSize = 16.sp,
            fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun handleRangeSelection(clickedDate: LocalDate, currentSelection: DateSelection): DateSelection {
    return when {
        currentSelection.start == null -> DateSelection(start = clickedDate)
        currentSelection.end == null -> {
            if (clickedDate.isAfter(currentSelection.start) || clickedDate.isEqual(currentSelection.start)) {
                DateSelection(start = currentSelection.start, end = clickedDate)
            } else {
                DateSelection(start = clickedDate)
            }
        }
        else -> DateSelection()
    }
}

@Composable
fun CategoryAggregateSection(categoryBreakdown: List<CategoryBreakdown?>, selection: DateSelection) {
    val selectionDateRange = selection.getSelectionAsYearMonthRange()
    if(categoryBreakdown.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No category breakdown data available", fontWeight = FontWeight.W500, fontSize = 16.sp, color = Color.Black)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(categoryBreakdown.size) { item ->
                val category = categoryBreakdown[item]?.category ?: ""
                val totalIncome = categoryBreakdown[item]?.totalIncome.toString()
                val totalExpense = categoryBreakdown[item]?.totalExpenses.toString()
                AggregateItem(category, selectionDateRange, totalIncome, totalExpense)
            }
        }
    }
}

fun generateColorFromHSV(index: Int, total: Int): Color {
    val hue = (360f / total) * index  // evenly distribute hues
    val saturation = 0.8f
    val value = 0.95f

    val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
    return Color(hsvColor)
}


@Composable
fun CategoryBreakdownPieChart(categoryBreakdown: List<CategoryBreakdown?>) {
    if(categoryBreakdown.isEmpty()) {
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

    val incomePercentageList = categoryBreakdown.map { it?.incomePercentage ?: 0.00 }
    val expensesPercentageList = categoryBreakdown.map { it?.expensePercentage ?: 0.00 }
    val categories = categoryBreakdown.map { it?.category ?: "" }

    val incomeBreakdownList = remember(incomePercentageList, categories) {
        categories.zip(incomePercentageList).mapIndexed { index, (label, percent) ->
            CategoryBreakdownPieData(
                label = label,
                percentage = percent,
                color = generateColorFromHSV(index, categories.size)
            )
        }
    }

    val expenseBreakdownList = remember(expensesPercentageList, categories) {
        categories.zip(expensesPercentageList).mapIndexed { index, (label, percent) ->
            CategoryBreakdownPieData(
                label = label,
                percentage = percent,
                color = generateColorFromHSV(index, categories.size)
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CustomPieChart("Income", incomeBreakdownList)
        CustomPieChart("Expenses", expenseBreakdownList)
    }
}

@Composable
fun CustomPieChart(type: String, breakdownList: List<CategoryBreakdownPieData>) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = type,
            fontWeight = FontWeight.W500,
            fontSize = 20.sp,
            color = if(type == "Income") Color(0xFF00D09E) else Color(0xFFF86058),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
        )
        PieChart(
            modifier = Modifier.size(150.dp),
            data = breakdownList.map { it ->
                Pie(
                    label = it.label,
                    data = it.percentage,
                    color = it.color,
                    selectedColor = Color(0xFF00D09E)
                )
            },
            selectedScale = 1.2f,
            scaleAnimEnterSpec = spring<Float>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            colorAnimEnterSpec = tween(300),
            colorAnimExitSpec = tween(300),
            scaleAnimExitSpec = tween(300),
            spaceDegreeAnimExitSpec = tween(300),
            style = Pie.Style.Fill
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Notes
        breakdownList.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(it.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${it.label}: ${it.percentage}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                )
            }
        }
    }
}

@Composable
fun AggregateItem(category: String, date: String, income: String, expense: String) {
    Box(
        modifier = Modifier.fillMaxWidth()

            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF6DB6FE), RoundedCornerShape(16.dp))
            .background(Color(0xFFF3FFFC))
            .padding(32.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF3299FF))
                        .align(Alignment.CenterVertically)
                        .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gifts),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Header and date
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = category,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = date,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        color = Color(0xFF0068FF)
                    )
                }
            }

            // Slider
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        color = Color(0xFF6DB6FE),
                        shape = RectangleShape
                    )
                    .height(1.dp)
            )

            // Item
            StatisticItem(type = "Income", amount = income)
            StatisticItem(type = "Expense", amount = expense)

        }
    }
}

@Composable
private fun StatisticItem(type: String, amount: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if(type == "Income") Color(0xFF00D09E) else Color(0xFFF86058),
                RoundedCornerShape(8.dp))
            .background(if(type == "Income") Color(0xFFCFFFF3) else Color(0xFFFFE8E7))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Vertical Slider
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if(type == "Income") Color(0xFF00D09E) else Color(0xFFF86058))
                )
                Text(
                    text = type,
                    color = if(type == "Income") Color(0xFF0A3D2D) else Color(0xFF5E1410),
                    fontWeight = FontWeight.W500
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFFFFF))
                        .padding(8.dp)
                ) {
                    Text(
                        text = amount,
                        color = if(type == "Income") Color(0xFF17A482) else Color(0xFFF86058),
                        fontWeight = FontWeight.W600,
                    )
                }
            }
        }

    }
}