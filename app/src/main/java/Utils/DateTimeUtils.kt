package DI.Utils

import DI.Composables.TransactionSection.GeneralTransactionItem
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    // Parse the transaction date with multiple possible formats using US locale for consistency
    val possibleFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    val today = Date().toString()

    fun formatDateTime(transaction: GeneralTransactionItem): DateTimeFormatterResult {
        val date = try {
            var parsedDate: Date? = null
            var usedFormat = ""

            for (formatPattern in possibleFormats) {
                try {
                    // Use US locale for parsing server dates (ISO format)
                    val formatter = SimpleDateFormat(formatPattern, Locale.US)
                    formatter.isLenient = false // Strict parsing
                    parsedDate = formatter.parse(transaction.timestamp ?: today)
                    usedFormat = formatPattern
                    Log.d("TransactionScreen", "Successfully parsed date using format: $usedFormat")
                    break
                } catch (e: Exception) {
                    // Try next format
                    continue
                }
            }

            if (parsedDate != null) {
                Log.d("TransactionScreen", "Parsed date result: $parsedDate")
                parsedDate
            } else {
                Log.e(
                    "TransactionScreen",
                    "Failed to parse date with all formats: ${transaction.timestamp}"
                )
                Date() // Fallback to current date
            }
        } catch (e: Exception) {
            Log.e("TransactionScreen", "Error parsing date: ${transaction.timestamp}", e)
            Date()
        }

        // Use user's locale for display formatting (will show in English or Vietnamese based on app language)
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val monthFormatter = SimpleDateFormat("MMMM", Locale.getDefault())

        // More readable date format: "15 December 2024" (EN) or "15 tháng 12, 2024" (VI)
        val dateDisplayFormatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

        val formattedDate = dateDisplayFormatter.format(date)
        val formattedTime = timeFormatter.format(date)
        val dayOfWeek = dayFormatter.format(date)
        val month = monthFormatter.format(date)

        return DateTimeFormatterResult(
            formattedDate = formattedDate,
            formattedTime = formattedTime,
            dayOfWeek = dayOfWeek,
            month = month
        )
    }
}

data class DateTimeFormatterResult(
    val formattedDate: String,
    val formattedTime: String,
    val dayOfWeek: String,
    val month: String
)