package DI.Composables.GroupChat

import DI.ViewModels.GroupChatViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GroupScreenTest(navController: NavController, viewModel: GroupChatViewModel, ) {
    val error by viewModel.error.collectAsState()
    val groups by viewModel.groups.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🚀 Group ViewModel Test Screen", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text("❌ Error: $error", color = Color.Red)
        } else if (groups.isNotEmpty()) {
            Text("✅ Loaded groups:")
            groups.forEach { group ->
                Text("- ${group.name}")
            }
        } else {
            Text("ℹ️ Waiting for ViewModel to trigger...")
        }
    }
}
