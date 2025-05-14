package DI.Composables.ProfileSection

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import java.io.File

// Define your color
val MainColor = Color(0xFF53dba9)
val BackgroundColor = Color(0xFF53dba9)
val CardColor = Color.White
val TextPrimaryColor = Color(0xFF333333)
val TextSecondaryColor = Color(0xFF757575)
val DividerColor = Color(0xFFEEEEEE)

fun uriToFile(uri: Uri, context: Context): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File.createTempFile("avatar", null, context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}

@Composable
fun AvatarImage(url: String) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.DISABLED) // Disable memory cache
            .diskCachePolicy(CachePolicy.DISABLED)   // Disable disk cache
            .listener(
                onError = { _, result ->
                    Log.e("AvatarImage", "Image load failed: ${result.throwable.toString()}")
                    // Optionally, log the cause if it exists
                    result.throwable.cause?.printStackTrace()
                },
                onSuccess = { _, _ ->
                    Log.d("AvatarImage", "Image loaded successfully")
                }
            )
            .build(),
        contentDescription = "User Avatar",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        contentScale = ContentScale.Crop
    )
}
