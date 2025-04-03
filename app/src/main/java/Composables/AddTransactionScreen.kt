package Composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import Composables.AddTransactionHeaderSection
import Composables.TransactionForm
import Composables.GeneralTemplate
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun AddTransactionScreen(navController: NavController) {
    GeneralTemplate(
        contentHeader = { AddTransactionHeaderSection(navController) },
        contentBody = { TransactionForm() },
        fraction = 0.14f,
    )
}