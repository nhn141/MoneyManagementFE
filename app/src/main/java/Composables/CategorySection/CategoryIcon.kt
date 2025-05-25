package DI.Composables.CategorySection

import android.content.Context

val categoryIcons = listOf(
    "ic_savings",
    "ic_medicine",
    "ic_groceries",
    "ic_rent",
    "ic_transport",
    "ic_food",
    "ic_entertainment",
    "ic_gifts",
    "ic_more"
)

fun getIconResIdByName(context: Context, iconName: String): Int {
    return context.resources.getIdentifier(iconName, "drawable", context.packageName)
}