package DI.Models.Category

import android.content.Context
import com.example.moneymanagement_frontend.R

class CategoryIconStorage(context: Context) {
    private val prefs = context.getSharedPreferences("category_icons", Context.MODE_PRIVATE)

    fun saveIcon(categoryId: String, iconResId: Int) {
        prefs.edit().putInt(categoryId, iconResId).apply()
    }

    fun getIcon(categoryId: String): Int {
        return prefs.getInt(categoryId, R.drawable.ic_more) // fallback nếu không có
    }

    fun removeIcon(categoryId: String) {
        prefs.edit().remove(categoryId).apply()
    }
}
