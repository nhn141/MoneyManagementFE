package DI.API.DateTimeHandler

import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object ChatTimeFormatter {
    fun formatTimestamp(isoString: String): String {
        return try {
            val messageTime = ZonedDateTime.parse(isoString)
            val now = ZonedDateTime.now(ZoneId.systemDefault())

            val daysBetween = ChronoUnit.DAYS.between(messageTime.toLocalDate(), now.toLocalDate())

            return when {
                daysBetween == 0L -> {
                    // Same day: just show the time
                    messageTime.format(DateTimeFormatter.ofPattern("h:mm a"))
                }
                daysBetween == 1L -> {
                    // Yesterday
                    "Yesterday at ${messageTime.format(DateTimeFormatter.ofPattern("h:mm a"))}"
                }
                messageTime.year == now.year -> {
                    // This year
                    messageTime.format(DateTimeFormatter.ofPattern("MMM d 'at' h:mm a"))
                }
                else -> {
                    // Previous years
                    messageTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))
                }
            }
        } catch (e: Exception) {
            // Handle invalid date format or other errors
            "Invalid date"
        }
    }
}