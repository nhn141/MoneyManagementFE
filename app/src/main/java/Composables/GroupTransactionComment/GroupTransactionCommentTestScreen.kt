package DI.Composables.GroupTransactionComment

import DI.ViewModels.GroupTransactionCommentViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GroupTransactionCommentTestScreen(navController: NavController, viewModel: GroupTransactionCommentViewModel, groupTransactionId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { viewModel.testFetchComments() }) {
            Text("Test Fetch Comments")
        }
        Button(onClick = { viewModel.testAddComment() }) {
            Text("Test Add Comment")
        }
        Button(onClick = { viewModel.testUpdateComment() }) {
            Text("Test Update Comment")
        }
        Button(onClick = { viewModel.testDeleteComment() }) {
            Text("Test Delete Comment")
        }
    }
}
