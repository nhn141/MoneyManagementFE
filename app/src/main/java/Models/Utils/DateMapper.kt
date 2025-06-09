package DI.Models.Utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateMapper {
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * Convert Date object to API-compatible string format
     */
    fun dateToApiString(date: Date): String {
        return apiDateFormat.format(date)
    }
    
    /**
     * Convert API string format to Date object
     */
    fun apiStringToDate(dateString: String): Date? {
        return try {
            apiDateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convert display string format to Date object
     */
    fun displayStringToDate(dateString: String): Date? {
        return try {
            displayDateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convert Date object to display string format
     */
    fun dateToDisplayString(date: Date): String {
        return displayDateFormat.format(date)
    }
    
    /**
     * Convert API string to display string
     */
    fun apiStringToDisplayString(apiString: String): String {
        val date = apiStringToDate(apiString)
        return if (date != null) {
            dateToDisplayString(date)
        } else {
            apiString // Return original if parsing fails
        }
    }
    
    /**
     * Convert display string to API string
     */
    fun displayStringToApiString(displayString: String): String {
        val date = displayStringToDate(displayString)
        return if (date != null) {
            dateToApiString(date)
        } else {
            displayString // Return original if parsing fails
        }
    }
}
