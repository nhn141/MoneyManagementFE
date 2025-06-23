package DI.Composables.ExportReports

import DI.Composables.TransactionSection.TransactionTextField
import DI.Models.Reports.ReportRequest
import DI.ViewModels.ReportViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReportScreen(viewModel: ReportViewModel) {
    val reportResult by viewModel.reportResult.collectAsState()
    var startDate by remember { mutableStateOf(LocalDateTime.now()) }
    var endDate by remember { mutableStateOf(LocalDateTime.now()) }
    val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    val typeOptions = listOf(
        "Báo cáo thu chi" to "cash-flow",
        "Phân loại chi tiêu" to "category-breakdown",
        "Tổng kết ngày" to "daily-summary",
        "Tổng kết tuần" to "weekly-summary",
        "Tổng kết tháng" to "monthly-summary",
        "Tổng kết năm" to "yearly-summary"
    )

    val currencyOptions = listOf(
        "VND" to "VND",
        "USD" to "USD"
    )

    var selectedType by remember { mutableStateOf(typeOptions.first().second) }
    var selectedCurrency by remember { mutableStateOf(currencyOptions.first().second) }

    val format = "pdf"
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    // Primary colors
    val primaryGreen = Color(0xFF00D09E)
    val lightGreen = Color(0xFF4DE6C7)
    val darkGreen = Color(0xFF00B890)
    val backgroundColor = Color(0xFFF8FFFE)
    val cardBackground = Color.White

    // Handle report result
    LaunchedEffect(reportResult, isLoading) {
        if (isLoading && reportResult != null) {
            isLoading = false
            reportResult?.let { result ->
                if (result.isSuccess) {
                    val responseBody = result.getOrNull()
                    responseBody?.let {
                        val saved = savePdfToDownloads(
                            context = context,
                            responseBody = it,
                            filename = "report_${System.currentTimeMillis()}.pdf"
                        )
                        Toast.makeText(
                            context,
                            if (saved) "Đã lưu báo cáo vào thư mục Downloads" else "Lưu báo cáo thất bại",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Tạo báo cáo thất bại",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        Color(0xFFF0FFFE)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            HeaderSection()

            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = primaryGreen.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Date Section
                    SectionHeader(
                        title = "Thời gian báo cáo",
                        icon = Icons.Default.CalendarToday,
                        color = primaryGreen
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ModernDateTimePicker(
                                label = "Ngày bắt đầu",
                                dateTime = startDate,
                                onDateTimeChange = { startDate = it },
                                primaryColor = primaryGreen
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ModernDateTimePicker(
                                label = "Ngày kết thúc",
                                dateTime = endDate,
                                onDateTimeChange = { endDate = it },
                                primaryColor = primaryGreen
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = primaryGreen.copy(alpha = 0.1f)
                    )

                    // Report Configuration Section
                    SectionHeader(
                        title = "Cấu hình báo cáo",
                        icon = Icons.Default.Assessment,
                        color = primaryGreen
                    )

                    ModernDropdownSelector(
                        label = "Loại báo cáo",
                        options = typeOptions,
                        selected = selectedType,
                        onSelectedChange = { selectedType = it },
                        primaryColor = primaryGreen
                    )

                    ModernDropdownSelector(
                        label = "Tiền tệ",
                        options = currencyOptions,
                        selected = selectedCurrency,
                        onSelectedChange = { selectedCurrency = it },
                        primaryColor = primaryGreen
                    )
                }
            }

            // Generate Button
            ModernGenerateButton(
                isLoading = isLoading,
                primaryColor = primaryGreen,
                lightColor = lightGreen,
                onClick = {
                    isLoading = true
                    val request = ReportRequest(
                        startDate = startDate.format(dateFormatter),
                        endDate = endDate.format(dateFormatter),
                        type = selectedType,
                        format = format,
                        currency = selectedCurrency
                    )
                    viewModel.generateReport(request)
                    Log.d("ReportScreen", "Start Date: ${startDate.format(dateFormatter)}, End Date: ${endDate.format(dateFormatter)}, Type: $selectedType, Format: $format, Currency: $selectedCurrency")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    val primaryGreen = Color(0xFF00D09E)
    val lightGreen = Color(0xFF4DE6C7)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = primaryGreen.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(primaryGreen, lightGreen)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "Tạo báo cáo tài chính",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Xuất báo cáo chi tiết và phân tích dữ liệu",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDateTimePicker(
    label: String,
    dateTime: LocalDateTime,
    onDateTimeChange: (LocalDateTime) -> Unit,
    primaryColor: Color
) {
    val displayDate = remember(dateTime) {
        SimpleDateFormat("dd/MM/yyyy\nHH:mm", Locale.getDefault()).format(
            Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant())
        )
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    var tempDateTime by remember { mutableStateOf(dateTime) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { dateDialogState.show() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            primaryColor.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2D3748)
                )
            )
        }
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("OK")
            negativeButton("Hủy")
        }
    ) {
        datepicker(
            initialDate = LocalDate.from(dateTime),
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = primaryColor,
                dateActiveBackgroundColor = primaryColor
            )
        ) { date ->
            tempDateTime = tempDateTime.withYear(date.year)
                .withMonth(date.monthValue)
                .withDayOfMonth(date.dayOfMonth)
            timeDialogState.show()
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("OK") {
                onDateTimeChange(tempDateTime)
            }
            negativeButton("Hủy")
        }
    ) {
        timepicker(
            initialTime = LocalTime.from(dateTime),
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = primaryColor,
                selectorColor = primaryColor
            )
        ) { time ->
            tempDateTime = tempDateTime.withHour(time.hour).withMinute(time.minute)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDropdownSelector(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    primaryColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.second == selected }?.first ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    label,
                    color = primaryColor,
                    fontWeight = FontWeight.Medium
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = primaryColor.copy(alpha = 0.3f),
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = primaryColor.copy(alpha = 0.7f),
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                Color.White,
                RoundedCornerShape(8.dp)
            )
        ) {
            options.forEach { (labelText, value) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            labelText,
                            color = if (value == selected) primaryColor else Color(0xFF2D3748),
                            fontWeight = if (value == selected) FontWeight.Medium else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelectedChange(value)
                        expanded = false
                    },
                    modifier = if (value == selected) {
                        Modifier.background(primaryColor.copy(alpha = 0.1f))
                    } else {
                        Modifier
                    }
                )
            }
        }
    }
}

@Composable
private fun ModernGenerateButton(
    isLoading: Boolean,
    primaryColor: Color,
    lightColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = primaryColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(primaryColor, lightColor)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Text(
                        "Đang tạo báo cáo...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Tải báo cáo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}