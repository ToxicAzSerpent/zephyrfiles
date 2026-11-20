package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DealershipCar
import com.example.model.InvestmentAsset
import com.example.model.LuxuryAsset
import com.example.model.TycoonTransaction
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.ZephyrCharcoal
import com.example.ui.theme.ZephyrIvoryBg
import com.example.ui.theme.ZephyrTaupe
import com.example.viewmodel.TycoonViewModel

enum class AppScreen {
    HOME,
    TRANSACTIONS,
    INVESTMENTS,
    INSIGHTS,
    PROFILE,
    BUSINESSES,
    LUXURY_ASSETS,
    DEALERSHIP,
    PROPERTY
}

@Composable
fun ZephyrApp(
    viewModel: TycoonViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    // Dialog states
    var selectedTradeAsset by remember { mutableStateOf<InvestmentAsset?>(null) }
    var selectedReceiptTx by remember { mutableStateOf<TycoonTransaction?>(null) }
    var showEarnDialog by remember { mutableStateOf(false) }
    var showWireDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Dealership dialog states
    var selectedDealershipCar by remember { mutableStateOf<DealershipCar?>(null) }
    var editingCar by remember { mutableStateOf<DealershipCar?>(null) }
    var showEditCarDialog by remember { mutableStateOf(false) }
    var editingImageTarget by remember { mutableStateOf<String?>(null) } // "hero" or "showroom"
    var showEditPlayerNameDialog by remember { mutableStateOf(false) }
    var editedPlayerNameText by remember { mutableStateOf("") }
    var editingLuxuryAsset by remember { mutableStateOf<LuxuryAsset?>(null) }
    var showEditLuxuryDialog by remember { mutableStateOf(false) }

    // ViewModel States
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val investments by viewModel.investments.collectAsStateWithLifecycle()
    val selectedStock by viewModel.selectedStock.collectAsStateWithLifecycle()
    val calendar by viewModel.calendar.collectAsStateWithLifecycle()
    val offlineReport by viewModel.offlineReport.collectAsStateWithLifecycle()
    val businesses by viewModel.businesses.collectAsStateWithLifecycle()
    val luxuryAssets by viewModel.luxuryAssets.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsStateWithLifecycle()
    val portfolioValue by viewModel.portfolioValue.collectAsStateWithLifecycle()
    val totalNetWorth by viewModel.totalNetWorth.collectAsStateWithLifecycle()
    val passiveIncomePerHour by viewModel.passiveIncomePerHour.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val dealershipCars by viewModel.dealershipCars.collectAsStateWithLifecycle()
    val dealershipSettings by viewModel.dealershipSettings.collectAsStateWithLifecycle()

    // Handle back button for secondary screens
    if (currentScreen != AppScreen.HOME) {
        BackHandler {
            currentScreen = AppScreen.HOME
        }
    }

    // Map screen to bottom navigation destination
    val navDestination = when (currentScreen) {
        AppScreen.HOME -> NavigationDestination.HOME
        AppScreen.LUXURY_ASSETS -> NavigationDestination.SHOP
        AppScreen.TRANSACTIONS -> NavigationDestination.TRANSACTIONS
        AppScreen.INVESTMENTS -> NavigationDestination.INVESTMENTS
        AppScreen.INSIGHTS -> NavigationDestination.INSIGHTS
        AppScreen.PROFILE -> NavigationDestination.PROFILE
        else -> NavigationDestination.HOME
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg),
        bottomBar = {
            ZephyrBottomNav(
                currentDestination = navDestination,
                onNavigate = { dest ->
                    currentScreen = when (dest) {
                        NavigationDestination.HOME -> AppScreen.HOME
                        NavigationDestination.SHOP -> AppScreen.LUXURY_ASSETS
                        NavigationDestination.TRANSACTIONS -> AppScreen.TRANSACTIONS
                        NavigationDestination.INVESTMENTS -> AppScreen.INVESTMENTS
                        NavigationDestination.INSIGHTS -> AppScreen.INSIGHTS
                        NavigationDestination.PROFILE -> AppScreen.PROFILE
                    }
                },
                onEarnClick = {
                    showEarnDialog = true
                }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        profile = profile,
                        calendar = calendar,
                        offlineReport = offlineReport,
                        transactions = transactions,
                        unreadNotificationsCount = notifications.count { !it.isRead },
                        onDismissOfflineReport = { viewModel.dismissOfflineReport() },
                        onClickToEarn = { viewModel.clickToEarn() },
                        onToggleBalanceVisibility = { viewModel.toggleBalanceVisibility() },
                        onEarnMoneyClick = { showEarnDialog = true },
                        onBusinessesClick = { currentScreen = AppScreen.BUSINESSES },
                        onInvestmentsClick = { currentScreen = AppScreen.INVESTMENTS },
                        onBuyAssetsClick = { currentScreen = AppScreen.LUXURY_ASSETS },
                        onOpenDealership = { currentScreen = AppScreen.DEALERSHIP },
                        onViewAllTransactions = { currentScreen = AppScreen.TRANSACTIONS },
                        onTransactionClick = { tx -> selectedReceiptTx = tx },
                        onNotificationClick = {
                            viewModel.markNotificationsRead()
                            showNotificationsDialog = true
                        }
                    )
                }

                AppScreen.DEALERSHIP -> {
                    DealershipScreen(
                        cars = dealershipCars,
                        settings = dealershipSettings,
                        playerName = profile.name,
                        onBack = { currentScreen = AppScreen.HOME },
                        onToggleEditMode = { viewModel.toggleDealerEditMode() },
                        onEditCar = { car ->
                            editingCar = car
                            showEditCarDialog = true
                        },
                        onAddNewCar = {
                            editingCar = null
                            showEditCarDialog = true
                        },
                        onCarClick = { car ->
                            selectedDealershipCar = car
                        },
                        onToggleFavorite = { carId ->
                            viewModel.toggleCarFavorite(carId)
                        },
                        onEditHeroImage = {
                            editingImageTarget = "hero"
                        },
                        onEditShowroomImage = {
                            editingImageTarget = "showroom"
                        },
                        onEditPlayerName = {
                            editedPlayerNameText = profile.name
                            showEditPlayerNameDialog = true
                        },
                        onRequestConsultation = {
                            viewModel.earnQuickCash(0.0, "Dealership Consultation Booked")
                        }
                    )
                }

                AppScreen.INVESTMENTS -> {
                    InvestmentsScreen(
                        portfolioValue = portfolioValue,
                        timeframe = selectedTimeframe,
                        investments = investments,
                        selectedStock = selectedStock,
                        onSelectStock = { asset -> viewModel.selectStock(asset) },
                        onTimeframeSelected = { tf -> viewModel.setTimeframe(tf) },
                        onBuyShares = { asset, shares -> viewModel.buyInvestment(asset.id, shares) },
                        onSellShares = { asset, shares -> viewModel.sellInvestment(asset.id, shares) },
                        onMenuClick = {
                            currentScreen = AppScreen.INSIGHTS
                        }
                    )
                }

                AppScreen.BUSINESSES -> {
                    BusinessesScreen(
                        businesses = businesses,
                        cashBalance = profile.cashBalance,
                        passiveIncomePerHour = passiveIncomePerHour,
                        onBack = { currentScreen = AppScreen.HOME },
                        onUpgrade = { bizId -> viewModel.upgradeBusiness(bizId) },
                        onHireManager = { bizId -> viewModel.hireManager(bizId) }
                    )
                }

                AppScreen.LUXURY_ASSETS -> {
                    LuxuryAssetsScreen(
                        assets = luxuryAssets,
                        cashBalance = profile.cashBalance,
                        isEditModeActive = dealershipSettings.isEditModeActive,
                        onBack = { currentScreen = AppScreen.HOME },
                        onBuyAsset = { assetId -> viewModel.buyLuxuryAsset(assetId) },
                        onLeaseAsset = { assetId -> viewModel.leaseLuxuryAsset(assetId) },
                        onEditAsset = { asset ->
                            editingLuxuryAsset = asset
                            showEditLuxuryDialog = true
                        }
                    )
                }

                AppScreen.TRANSACTIONS -> {
                    TransactionsScreen(
                        cashBalance = profile.cashBalance,
                        transactions = transactions,
                        onSendWireClick = { showWireDialog = true },
                        onTransactionClick = { tx -> selectedReceiptTx = tx }
                    )
                }

                AppScreen.INSIGHTS -> {
                    InsightsScreen(
                        cashBalance = profile.cashBalance,
                        portfolioValue = portfolioValue,
                        businesses = businesses,
                        luxuryAssets = luxuryAssets,
                        totalNetWorth = totalNetWorth
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        profile = profile,
                        totalNetWorth = totalNetWorth,
                        businessesCount = businesses.size,
                        luxuryCount = luxuryAssets.count { it.isOwned },
                        isEditModeActive = dealershipSettings.isEditModeActive,
                        offlineTaxPercent = dealershipSettings.offlineTaxPercent,
                        onToggleEditMode = { viewModel.toggleDealerEditMode() },
                        onUpdateOfflineTax = { pct -> viewModel.updateOfflineTaxPercent(pct) },
                        onOpenDealership = { currentScreen = AppScreen.DEALERSHIP },
                        onOpenProperty = { currentScreen = AppScreen.PROPERTY },
                        onEditName = { newName -> viewModel.updatePlayerName(newName) },
                        onResetData = { viewModel.resetData() }
                    )
                }

                AppScreen.PROPERTY -> {
                    PropertyScreen(
                        luxuryAssets = luxuryAssets,
                        dealershipCars = dealershipCars,
                        onBack = { currentScreen = AppScreen.HOME },
                        isEditModeActive = dealershipSettings.isEditModeActive,
                        onAddMansionSlot = { viewModel.addMansionSlot() }
                    )
                }
            }
        }
    }

    // Car Details Dialog
    selectedDealershipCar?.let { car ->
        CarDetailDialog(
            car = car,
            onDismiss = { selectedDealershipCar = null },
            onBuy = {
                viewModel.buyDealershipCar(car.id)
            },
            onLease = {
                viewModel.leaseDealershipCar(car.id)
            }
        )
    }

    // Edit Car & Photo Dialog
    if (showEditCarDialog) {
        EditCarDialog(
            car = editingCar,
            onDismiss = { showEditCarDialog = false },
            onSave = { updatedCar ->
                if (editingCar == null) {
                    viewModel.addCar(updatedCar)
                } else {
                    viewModel.updateCar(updatedCar)
                }
                showEditCarDialog = false
            },
            onDelete = { carId ->
                viewModel.deleteCar(carId)
                showEditCarDialog = false
            }
        )
    }

    // Edit Hero or Showroom Image Dialog
    editingImageTarget?.let { target ->
        val currentUri = if (target == "hero") dealershipSettings.heroImageUri else dealershipSettings.showroomImageUri
        val title = if (target == "hero") "Dealership Hero SUV" else "Showroom Floor"
        EditImageDialog(
            title = title,
            currentImageUri = currentUri,
            onDismiss = { editingImageTarget = null },
            onSave = { newUri ->
                if (target == "hero") {
                    viewModel.updateDealershipHeroImage(newUri)
                } else {
                    viewModel.updateDealershipShowroomImage(newUri)
                }
                editingImageTarget = null
            }
        )
    }

    // Direct Player Name Edit Dialog from Dealership
    if (showEditPlayerNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditPlayerNameDialog = false },
            title = {
                Text(
                    text = "Edit Player Name",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ZephyrCharcoal,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = editedPlayerNameText,
                    onValueChange = { editedPlayerNameText = it },
                    label = { Text("Player / Dealer VIP Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dealer_player_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedPlayerNameText.isNotBlank()) {
                            viewModel.updatePlayerName(editedPlayerNameText.trim())
                        }
                        showEditPlayerNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPlayerNameDialog = false }) {
                    Text("Cancel", color = ZephyrTaupe)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Trade Dialog
    selectedTradeAsset?.let { asset ->
        TradeInvestmentDialog(
            asset = asset,
            cashBalance = profile.cashBalance,
            onDismiss = { selectedTradeAsset = null },
            onBuy = { shares -> viewModel.buyInvestment(asset.id, shares) },
            onSell = { shares -> viewModel.sellInvestment(asset.id, shares) }
        )
    }

    // Executive Earn Deals Dialog
    if (showEarnDialog) {
        ExecutiveEarnDialog(
            onDismiss = { showEarnDialog = false },
            onExecuteDeal = { reward, title ->
                viewModel.earnQuickCash(reward, title)
            }
        )
    }

    // Wire Transfer Dialog
    if (showWireDialog) {
        WireTransferDialog(
            cashBalance = profile.cashBalance,
            onDismiss = { showWireDialog = false },
            onSend = { recipient, amount, note ->
                viewModel.sendWireTransfer(recipient, amount, note)
            }
        )
    }

    // Receipt Dialog
    selectedReceiptTx?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            onDismiss = { selectedReceiptTx = null }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotificationsDialog = false },
            onMarkAllRead = { viewModel.markNotificationsRead() }
        )
    }

    // Edit Luxury Asset Dialog
    if (showEditLuxuryDialog && editingLuxuryAsset != null) {
        EditLuxuryAssetDialog(
            asset = editingLuxuryAsset!!,
            onDismiss = { showEditLuxuryDialog = false },
            onSave = { updatedAsset ->
                viewModel.updateLuxuryAsset(updatedAsset)
                showEditLuxuryDialog = false
            }
        )
    }
}
