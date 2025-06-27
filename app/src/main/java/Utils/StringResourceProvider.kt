package Utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(resourceId: Int): String {
        return context.getString(resourceId)
    }

    fun getString(resourceId: Int, vararg args: Any): String {
        return context.getString(resourceId, *args)
    }
} 