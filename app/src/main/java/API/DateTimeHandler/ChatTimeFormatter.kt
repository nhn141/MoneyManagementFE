import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.Locale

object ChatTimeFormatter {
    private val vietnameseLocale = Locale("vi", "VN")

    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Vietnamese localized time and date formats
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", vietnameseLocale)     // 24h format, e.g., 10:06
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMM", vietnameseLocale)     // e.g., 16 thg 5
    private val fullDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", vietnameseLocale) // e.g., 16 thg 5 2025

    fun formatTimestamp(sentAt: String): String {
        val sentDateTime = LocalDateTime.parse(sentAt, inputFormatter)
        val now = LocalDateTime.now(ZoneId.systemDefault())

        val sentDate = sentDateTime.toLocalDate()
        val today = now.toLocalDate()
        val yesterday = today.minusDays(1)

        return when {
            sentDate == today -> sentDateTime.format(timeFormatter)   // just time, 24h format
            sentDate == yesterday -> "Hôm qua"                         // Yesterday in Vietnamese
            sentDate.year == today.year -> sentDateTime.format(dateFormatter) // e.g., 16 thg 5
            else -> sentDateTime.format(fullDateFormatter)             // e.g., 16 thg 5 2025
        }
    }
}
