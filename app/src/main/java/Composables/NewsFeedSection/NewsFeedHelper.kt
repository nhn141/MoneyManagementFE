package DI.Composables.NewsFeedSection

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsFeedHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun createMultipartFromUri(uri: Uri): MultipartBody.Part? {
        return try {
            val tempFile = createTempFileFromUri(uri)
            tempFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", it.name, requestFile)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(uri) ?: "temp_image_${System.currentTimeMillis()}.jpg"
            val tempFile = File(context.cacheDir, fileName)

            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        result = it.getString(columnIndex)
                    }
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }

        return result
    }

    fun createTextRequestBody(text: String): okhttp3.RequestBody {
        return text.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun cleanupTempFiles() {
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("temp_image_")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}