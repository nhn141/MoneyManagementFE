package DI.Composables.CategorySection

import android.widget.Toast
import androidx.compose.foundation.Image
import com.example.moneymanagement_frontend.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext


@Composable
fun AddCategoryButton(
    onAddClick: () -> Unit
) {
    Surface(
        onClick = onAddClick,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFF6F9FF),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = "Add category",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Text(
                text = "Add new one",
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
}

@Composable
fun AddCategoryDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    selectedIcon: Int,
    onIconChange: (Int) -> Unit,
    onSave: (String, Int) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "New Category",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = text,
                            onValueChange = {
                                text = it
                                isError = false
                            },
                            placeholder = { Text("Write...") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFDFF7E2),
                                focusedContainerColor = Color(0xFFDFF7E2),
                                unfocusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                                focusedIndicatorColor = if (isError) Color.Red else Color.Transparent
                            ),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconSelector(
                            icons = categoryIcons,
                            selectedIcon = selectedIcon,
                            onIconSelected = { onIconChange(it) }
                        )
                    }
                    if (isError) {
                        Toast.makeText(context, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (text.isBlank()) {
                            isError = true
                        } else {
                            onSave(text, selectedIcon)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF46F2C9)),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Save", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDFF7E2)),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            modifier = Modifier
                .width(340.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconSelector(
    icons: List<Int>,
    selectedIcon: Int,
    onIconSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF0068FF), shape = CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        ) {
            Icon(
                painter = painterResource(id = selectedIcon),
                contentDescription = "Selected Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .width(200.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFDFF7E2))
            ) {
                Text(
                    text = "Select an icon",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }

            FlowRow(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                icons.forEach { icon ->
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "Icon $icon",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable {
                                onIconSelected(icon)
                                expanded = false
                            }
                            .background(
                                if (icon == selectedIcon) Color(0xFFDFF7E2) else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}








