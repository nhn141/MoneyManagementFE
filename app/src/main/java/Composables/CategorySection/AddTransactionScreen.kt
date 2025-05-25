package DI.Composables.CategorySection

import DI.ViewModels.TransactionScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import DI.Composables.GeneralTemplate
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.OcrViewModel
import DI.ViewModels.WalletViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionScreenViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    ocrViewModel: OcrViewModel
) {
    var type by remember { mutableStateOf("Expense") }

    GeneralTemplate(
        contentHeader = {
            AddTransactionHeaderSection(
                navController = navController,
                currentType = type,
                onTypeChange = { newType -> type = newType },
                ocrViewModel = ocrViewModel
            )
        },
        contentBody = {
            TransactionForm(
                transactionViewModel,
                navController,
                type,
                onTypeChange = { type = it },
                categoryViewModel = categoryViewModel,
                walletViewModel = walletViewModel,
                ocrViewModel = ocrViewModel
            )
        },
        fraction = 0.14f,
    )
}
