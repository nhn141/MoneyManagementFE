package DI.Composables.WalletSection

import DI.Composables.CategorySection.ModernColors
import DI.Models.UiEvent.UiEvent
import DI.Models.Wallet.AddWalletRequest
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyUtils
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.WalletViewModel
import Utils.CurrencyInputTextField
import Utils.USDInputPreview
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.launch

// Color scheme
object WalletColors {
    val Primary = Color(0xFF10B981) // Emerald-500
    val PrimaryVariant = Color(0xFF059669) // Emerald-600
    val Secondary = Color(0xFF34D399) // Emerald-400
    val Background = Color(0xFFF0FDF4) // Green-50
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color.White
    val OnSurface = Color(0xFF1F2937) // Gray-800
    val OnSurfaceVariant = Color(0xFF6B7280) // Gray-500
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
}

@Composable
fun WalletScreen(
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    navController: NavController
) {
    val walletsState by walletViewModel.wallets.collectAsStateWithLifecycle()
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRate.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showPlusDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<Wallet?>(null) }
    var plusWallet by remember { mutableStateOf<Wallet?>(null) }

    LaunchedEffect(Unit) {
        walletViewModel.getWallets()
    }

    LaunchedEffect(Unit) {
        // Collect events for add, update, and delete actions
        launch {
            walletViewModel.addWalletEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        launch {
            walletViewModel.updateWalletEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        launch {
            walletViewModel.deleteWalletEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WalletColors.Background,
                        Color(0xFFECFDF5) // Emerald-50
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            WalletHeader(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on state
            val walletsResult = walletsState
            when {
                walletsResult == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WalletColors.Primary)
                    }
                }

                walletsResult.isSuccess -> {
                    val wallets = walletsResult.getOrNull() ?: emptyList()
                    WalletContent(
                        wallets = wallets,
                        isVND = isVND,
                        exchangeRates = exchangeRates,
                        onAddWallet = { showAddDialog = true },
                        onPlusWallet = { wallet ->
                            plusWallet = wallet
                            showPlusDialog = true
                        },
                        onEditWallet = { wallet ->
                            editingWallet = wallet
                            showEditDialog = true
                        },
                        onDeleteWallet = { walletId ->
                            walletViewModel.deleteWallet(walletId)
                        }
                    )
                }

                walletsResult.isFailure -> {
                    ErrorMessage(
                        message = walletsResult.exceptionOrNull()?.message
                            ?: stringResource(R.string.unknown_error),
                        onRetry = { walletViewModel.getWallets() }
                    )
                }
            }
        }

        // Dialogs
        if (showAddDialog) {
            AddWalletDialog(
                isVND = isVND,
                exchangeRates = exchangeRates,
                onDismiss = { showAddDialog = false },
                onConfirm = { request ->
                    walletViewModel.addWallet(request)
                    showAddDialog = false
                }
            )
        }

        if (showPlusDialog && plusWallet != null) {
            PLusWalletDialog(
                wallet = plusWallet!!,
                isVND = isVND,
                exchangeRates = exchangeRates,
                onDismiss = {
                    showPlusDialog = false
                    plusWallet = null
                },
                onConfirm = { updatedWallet ->
                    walletViewModel.updateWallet(updatedWallet)
                    showPlusDialog = false
                    plusWallet = null
                }
            )
        }

        if (showEditDialog && editingWallet != null) {
            EditWalletDialog(
                wallet = editingWallet!!,
                onDismiss = {
                    showEditDialog = false
                    editingWallet = null
                },
                onConfirm = { wallet ->
                    walletViewModel.updateWallet(wallet)
                    showEditDialog = false
                    editingWallet = null
                })
        }
    }
}

@Composable
private fun WalletHeader(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.my_wallets),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = WalletColors.OnSurface
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = WalletColors.Primary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .clickable(onClick = { navController.popBackStack() }),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = WalletColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WalletContent(
    wallets: List<Wallet>,
    isVND: Boolean,
    exchangeRates: Double?,
    onAddWallet: () -> Unit,
    onPlusWallet: (Wallet) -> Unit,
    onEditWallet: (Wallet) -> Unit,
    onDeleteWallet: (String) -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Track which wallet is being deleted, not just if a dialog is shown
    var walletToDelete by remember { mutableStateOf<Wallet?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total balance card
        TotalBalanceCard(
            totalBalance = wallets.sumOf { it.balance },
            isVND = isVND,
            exchangeRates = exchangeRates
        )

        // Add wallet button
        AddWalletButton(onClick = onAddWallet)

        // Wallet cards
        wallets.forEach { wallet ->
            WalletCard(
                wallet = wallet,
                isVND = isVND,
                exchangeRates = exchangeRates,
                onPlus = { onPlusWallet(wallet) },
                onEdit = { onEditWallet(wallet) },
                onDelete = {
                    walletToDelete = wallet
                    showDeleteConfirmation = true
                }
            )
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(2.dp))
    }

    // Delete confirmation dialog - outside of LazyColumn
    walletToDelete?.let { wallet ->
        AlertDialog(
            onDismissRequest = { walletToDelete = null },
            title = {
                Text(
                    stringResource(R.string.delete_wallet),
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.OnSurface
                )
            },
            text = {
                Text(
                    stringResource(R.string.delete_wallet_confirm, wallet.walletName),
                    color = ModernColors.OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWallet(wallet.walletID)
                        walletToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ModernColors.Error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.delete), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { walletToDelete = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        color = ModernColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun TotalBalanceCard(
    totalBalance: Double,
    isVND: Boolean,
    exchangeRates: Double?
) {
    // Convert total balance based on currency preference
    val convertedBalance = if (isVND) {
        totalBalance
    } else {
        exchangeRates?.let { rates ->
            CurrencyUtils.vndToUsd(totalBalance, rates)
        } ?: totalBalance
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(WalletColors.Primary, WalletColors.PrimaryVariant)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.total_balance),
                        color = WalletColors.OnPrimary.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = CurrencyUtils.formatAmount(convertedBalance, isVND),
                        color = WalletColors.OnPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = WalletColors.OnPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddWalletButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = WalletColors.Primary
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.add_new_wallet),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun WalletCard(
    wallet: Wallet,
    isVND: Boolean,
    exchangeRates: Double?,
    onPlus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Convert wallet balance based on currency preference
    val convertedBalance = if (isVND) {
        wallet.balance
    } else {
        exchangeRates?.let { rates ->
            CurrencyUtils.vndToUsd(wallet.balance, rates)
        } ?: wallet.balance
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WalletColors.Surface
        ),
        border = BorderStroke(1.dp, WalletColors.Primary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            WalletColors.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = WalletColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onPlus,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = stringResource(R.string.add),
                            tint = WalletColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = WalletColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = WalletColors.Error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet name
            Text(
                text = wallet.walletName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WalletColors.OnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Balance
            Text(
                text = CurrencyUtils.formatAmount(convertedBalance, isVND),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WalletColors.Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer with meaningful information
            HorizontalDivider(color = WalletColors.Primary.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isVND) "VND" else "USD",
                    fontSize = 12.sp,
                    color = WalletColors.OnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = stringResource(R.string.wallet_type_personal),
                    fontSize = 12.sp,
                    color = WalletColors.Success,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AddWalletDialog(
    isVND: Boolean,
    exchangeRates: Double?,
    onDismiss: () -> Unit,
    onConfirm: (AddWalletRequest) -> Unit
) {
    var walletName by remember { mutableStateOf("") }
    var amountTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var parsedAmount by remember { mutableDoubleStateOf(0.0) }
    var showUSDPreview by remember { mutableStateOf(false) }
    var isAmountFieldFocused by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WalletColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_new_wallet),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletColors.OnSurface
                )

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it },
                    label = { Text(stringResource(R.string.wallet_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WalletColors.Primary,
                        focusedLabelColor = WalletColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input with Currency Support
                Column {
                    CurrencyInputTextField(
                        value = amountTextFieldValue,
                        onValueChange = { newValue -> amountTextFieldValue = newValue },
                        isVND = isVND,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isAmountFieldFocused = focusState.isFocused
                            },
                        placeholder = stringResource(R.string.initial_balance),
                        onFormatted = { _, amount ->
                            parsedAmount = amount ?: 0.0
                        }
                    )

                    // Track focus state for USD preview
                    LaunchedEffect(amountTextFieldValue.text, isVND, isAmountFieldFocused) {
                        showUSDPreview =
                            !isVND && isAmountFieldFocused && amountTextFieldValue.text.isNotEmpty()
                    }

                    if (showUSDPreview) {
                        USDInputPreview(
                            inputText = amountTextFieldValue.text,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            // Convert the amount to VND if currently in USD mode
                            val balanceInVND = if (isVND) {
                                parsedAmount
                            } else {
                                exchangeRates?.let { rates ->
                                    CurrencyUtils.usdToVnd(parsedAmount, rates)
                                } ?: parsedAmount
                            }

                            val request = AddWalletRequest(
                                walletName = walletName.trim(),
                                balance = balanceInVND
                            )
                            onConfirm(request)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WalletColors.Primary
                        ),
                        enabled = walletName.isNotBlank() && parsedAmount > 0
                    ) {
                        Text(stringResource(R.string.wallet_form_add))
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.8f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WalletColors.OnSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            WalletColors.OnSurfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
private fun PLusWalletDialog(
    wallet: Wallet,
    isVND: Boolean,
    exchangeRates: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Wallet) -> Unit
) {
    var amountTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var parsedAmount by remember { mutableDoubleStateOf(0.0) }
    var showUSDPreview by remember { mutableStateOf(false) }
    var isAmountFieldFocused by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WalletColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.enter_amount),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletColors.OnSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input with Currency Support
                Column {
                    CurrencyInputTextField(
                        value = amountTextFieldValue,
                        onValueChange = { newValue -> amountTextFieldValue = newValue },
                        isVND = isVND,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isAmountFieldFocused = focusState.isFocused
                            },
                        placeholder = stringResource(R.string.plus_wallet),
                        onFormatted = { _, amount ->
                            parsedAmount = amount ?: 0.0
                        }
                    )

                    // Track focus state for USD preview
                    LaunchedEffect(amountTextFieldValue.text, isVND, isAmountFieldFocused) {
                        showUSDPreview =
                            !isVND && isAmountFieldFocused && amountTextFieldValue.text.isNotEmpty()
                    }

                    if (showUSDPreview) {
                        USDInputPreview(
                            inputText = amountTextFieldValue.text,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            // Convert the amount to VND if currently in USD mode
                            val balanceInVND = if (isVND) {
                                parsedAmount
                            } else {
                                exchangeRates?.let { rates ->
                                    CurrencyUtils.usdToVnd(parsedAmount, rates)
                                } ?: parsedAmount
                            }

                            val updatedWallet = wallet.copy(
                                walletName = wallet.walletName,
                                balance = balanceInVND
                            )
                            onConfirm(updatedWallet)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WalletColors.Primary
                        ),
                        enabled = parsedAmount > 0
                    ) {
                        Text(stringResource(R.string.plus_wallet))
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.8f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WalletColors.OnSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            WalletColors.OnSurfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
private fun EditWalletDialog(
    wallet: Wallet,
    onDismiss: () -> Unit,
    onConfirm: (Wallet) -> Unit
) {
    var walletName by remember { mutableStateOf(wallet.walletName) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WalletColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.wallet_form_edit_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletColors.OnSurface
                )

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it },
                    label = { Text(stringResource(R.string.wallet_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WalletColors.Primary,
                        focusedLabelColor = WalletColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val updatedWallet = wallet.copy(
                                walletName = walletName.trim(),
                                balance = 0.0
                            )
                            onConfirm(updatedWallet)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WalletColors.Primary
                        ),
                        enabled = walletName.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WalletColors.OnSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            WalletColors.OnSurfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = WalletColors.Error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.something_went_wrong),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WalletColors.OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = WalletColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = WalletColors.Primary
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.try_again))
        }
    }
}