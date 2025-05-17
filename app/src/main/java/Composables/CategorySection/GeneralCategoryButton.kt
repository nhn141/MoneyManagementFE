package DI.Composables.CategorySection

import DI.Models.Category.Category
import DI.Models.Category.CategoryIconStorage
import DI.ViewModels.CategoryViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneymanagement_frontend.R

@Composable
fun GeneralCategoryButton(
    category: Category,
    onClick: () -> Unit,
    onDelete : (Category) -> Unit
) {
    val context = LocalContext.current
    val iconStorage = remember { CategoryIconStorage(context) }
    val iconRes = iconStorage.getIcon(category.categoryID)
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<CategoryViewModel>()
    var currentIcon by remember { mutableIntStateOf(iconRes) }

    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50)
                }
                showDialog = true
            },
            onTap = {
                onClick()
            }
        )
    }


    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFF6F9FF),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(gestureModifier)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0068FF)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = category.name,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category: ${category.name}",
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .size(25.dp)
                            .background(Color.LightGray, shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit"
                        )
                    }
                }
            },
            text = {
                Column {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("ID: ")
                        }
                        append(category.categoryID)
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Create Date: ")
                        }
                        append(category.createdAt)
                    })
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        showConfirmDeleteDialog = true
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            }
        )
    }

    if (showEditDialog) {
        EditCategoryDialog(
            category = category,
            selectedIcon = currentIcon,
            onIconChange = { selected -> currentIcon = selected },
            onDismiss = { showEditDialog = false },
            onSave = { newName, newIconResId ->
                val updatedCategory = category.copy(name = newName)
                viewModel.updateCategory(updatedCategory)
                iconStorage.saveIcon(category.categoryID, newIconResId)
                showEditDialog = false
            }
        )
    }


    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = {
                Text(text = "Confirm Delete", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete category \"${category.name}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(category)
                        showConfirmDeleteDialog = false
                    }
                ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDeleteDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun EditCategoryDialog(
    category: Category,
    selectedIcon: Int,
    onIconChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var text by remember { mutableStateOf(category.name) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Category", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        isError = false
                    },
                    placeholder = { Text("Category name") },
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )

                IconSelector(
                    icons = categoryIcons,
                    selectedIcon = selectedIcon,
                    onIconSelected = onIconChange
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (text.isBlank()) {
                    isError = true
                } else {
                    onSave(text, selectedIcon)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}






