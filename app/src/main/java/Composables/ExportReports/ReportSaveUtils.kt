package DI.Composables.ExportReports

import android.content.Context
import android.os.Environment
import android.util.Log
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

fun savePdfToDownloads(context: Context, responseBody: ResponseBody, filename: String): Boolean {
    return try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, filename)

        responseBody.byteStream().use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        true
    } catch (e: Exception) {
        Log.e("FileUtils", "Failed to save file", e)
        false
    }
}