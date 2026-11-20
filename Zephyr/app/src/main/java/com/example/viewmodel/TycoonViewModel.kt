package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TycoonRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class TycoonViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = TycoonRepository(application)

    private val _profile = MutableStateFlow(repo.loadProfile())
    val profile: StateFlow<PlayerProfile> = _profile.asStateFlow()

    private val _investments = MutableStateFlow(repo.getInitialInvestments())
    val investments: StateFlow<List<InvestmentAsset>> = _investments.asStateFlow()

    private val _selectedStock = MutableStateFlow<InvestmentAsset?>(null)
    val selectedStock: StateFlow<InvestmentAsset?> = _selectedStock.asStateFlow()

    private val _businesses = MutableStateFlow(repo.getInitialBusinesses())
    val businesses: StateFlow<List<BusinessEnterprise>> = _businesses.asStateFlow()

    private val _luxuryAssets = MutableStateFlow(repo.loadLuxuryAssets())
    val luxuryAssets: StateFlow<List<LuxuryAsset>> = _luxuryAssets.asStateFlow()

    private val _transactions = MutableStateFlow(repo.getInitialTransactions())
    val transactions: StateFlow<List<TycoonTransaction>> = _transactions.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow(Timeframe.ONE_MONTH)
    val selectedTimeframe: StateFlow<Timeframe> = _selectedTimeframe.asStateFlow()

    private val _calendar = MutableStateFlow(repo.loadCalendar())
    val calendar: StateFlow<InGameCalendar> = _calendar.asStateFlow()

    private val _offlineReport = MutableStateFlow<OfflineEarningsReport?>(null)
    val offlineReport: StateFlow<OfflineEarningsReport?> = _offlineReport.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("n1", "Executive Tax Filing", "15% sovereign tax applied across all liquid earnings and assets.", "Just now"),
            NotificationItem("n2", "Market Opportunity", "Global equities and tech assets are experiencing active trading sessions.", "1h ago"),
            NotificationItem("n3", "M&A Advisory Inbound", "Consortium offering term sheet for Elysian Coffee Atelier expansion.", "4h ago")
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _dealershipCars = MutableStateFlow(repo.loadDealershipCars())
    val dealershipCars: StateFlow<List<DealershipCar>> = _dealershipCars.asStateFlow()

    private val _dealershipSettings = MutableStateFlow(repo.loadDealershipSettings())
    val dealershipSettings: StateFlow<DealershipSettings> = _dealershipSettings.asStateFlow()

    // Total Portfolio Value
    val portfolioValue: StateFlow<Double> = _investments.combine(_selectedTimeframe) { invList, _ ->
        invList.sumOf { it.totalValue }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 120560.50)

    // Total Net Worth (Cash + Portfolio + Businesses + Assets)
    val totalNetWorth: StateFlow<Double> = combine(_profile, _investments, _luxuryAssets) { prof, invs, luxs ->
        val cash = prof.cashBalance
        val port = invs.sumOf { it.totalValue }
        val luxVal = luxs.filter { it.isOwned }.sumOf { it.price }
        cash + port + luxVal
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 203306.10)

    val passiveIncomePerHour: StateFlow<Double> = _businesses.combine(_profile) { bizList, _ ->
        bizList.filter { it.isAutomated }.sumOf { it.currentRevenuePerHour }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 4500.0)

    init {
        processOfflineProgress()
        startInGameCalendarAndMarketTicker()
    }

    private fun processOfflineProgress() {
        val lastActiveMs = repo.getLastActiveTimestamp()
        val nowMs = System.currentTimeMillis()
        val elapsedSec = ((nowMs - lastActiveMs) / 1000L).coerceAtLeast(0L)

        // Only generate offline report if player was away for more than 10 seconds
        if (elapsedSec >= 10L) {
            val maxTrackedSec = elapsedSec.coerceAtMost(86400L * 7) // max 7 days
            val daysPassed = maxTrackedSec / 25L

            // 1. Calculate business passive earnings
            val bizPerSec = _businesses.value.filter { it.isAutomated }.sumOf { it.currentRevenuePerSec }
            val grossBizIncome = bizPerSec * maxTrackedSec

            // 2. Calculate stock dividend yield (approx 0.0001% per sec)
            val currentPortfolioVal = _investments.value.sumOf { it.totalValue }
            val grossDividends = currentPortfolioVal * 0.000001 * maxTrackedSec

            val totalGross = grossBizIncome + grossDividends
            val baseTaxRate = 0.15 // 15% flat tax
            val baseTaxPaid = totalGross * baseTaxRate

            // Check if away for >= 2 hours (7200 seconds) -> apply additional offline inactivity tax
            val isAway2Hours = elapsedSec >= 7200L
            val offlineTaxPercent = _dealershipSettings.value.offlineTaxPercent
            val additionalOfflineTax = if (isAway2Hours) totalGross * (offlineTaxPercent / 100.0) else 0.0

            val totalTaxPaid = baseTaxPaid + additionalOfflineTax
            val netIncome = totalGross - totalTaxPaid

            if (totalGross > 1.0) {
                val currentProf = _profile.value
                val updatedProf = currentProf.copy(cashBalance = currentProf.cashBalance + netIncome)
                _profile.value = updatedProf
                repo.saveProfile(updatedProf)

                // Advance in-game calendar
                val currentCal = _calendar.value
                val updatedCal = currentCal.copy(
                    totalDays = currentCal.totalDays + daysPassed,
                    secondsIntoDay = ((currentCal.secondsIntoDay + (maxTrackedSec % 25)) % 25)
                )
                _calendar.value = updatedCal
                repo.saveCalendar(updatedCal)

                _offlineReport.value = OfflineEarningsReport(
                    elapsedSeconds = elapsedSec,
                    inGameDaysPassed = daysPassed,
                    grossIncome = totalGross,
                    taxPaid = totalTaxPaid,
                    netIncome = netIncome
                )

                val noteStr = StringBuilder().apply {
                    append("Gross: \$${String.format(Locale.US, "%,.2f", totalGross)} · Tax (15%): -\$${String.format(Locale.US, "%,.2f", baseTaxPaid)}")
                    if (additionalOfflineTax > 0.0) {
                        append(" · 2h Inactivity Tax (${offlineTaxPercent}%): -\$${String.format(Locale.US, "%,.2f", additionalOfflineTax)}")
                    }
                }.toString()

                val tx = TycoonTransaction(
                    id = "tx_offline_${System.currentTimeMillis()}",
                    title = "Offline Empire Yield",
                    category = "Passive Revenue",
                    timestamp = "Year ${updatedCal.year}, Day ${updatedCal.dayOfYear}",
                    amount = netIncome,
                    isIncome = true,
                    note = noteStr,
                    taxDeducted = totalTaxPaid
                )
                _transactions.value = listOf(tx) + _transactions.value
            }
        }
        repo.updateLastActiveTimestamp()
    }

    fun dismissOfflineReport() {
        _offlineReport.value = null
    }

    private fun startInGameCalendarAndMarketTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // 1 second loop

                // 1. Progress in-game calendar: 1 day = 25 seconds, 1 year = 365 days
                val curCal = _calendar.value
                var newSecs = curCal.secondsIntoDay + 1f
                var newDays = curCal.totalDays
                var dayRollover = false

                if (newSecs >= 25f) {
                    newSecs = 0f
                    newDays += 1
                    dayRollover = true
                }

                val updatedCal = InGameCalendar(totalDays = newDays, secondsIntoDay = newSecs)
                _calendar.value = updatedCal
                if (dayRollover) {
                    repo.saveCalendar(updatedCal)
                }

                // 2. Passive income per second (applied every second)
                val passiveSum = _businesses.value.filter { it.isAutomated }.sumOf { it.currentRevenuePerSec }
                if (passiveSum > 0) {
                    val gross = passiveSum
                    val tax = gross * 0.15 // 15% income tax applied
                    val net = gross - tax
                    val currentProfile = _profile.value
                    val updated = currentProfile.copy(cashBalance = currentProfile.cashBalance + net)
                    _profile.value = updated
                }

                // 3. Daily Stock Market Update on day transition (or every 3 seconds slightly drift)
                if (dayRollover || (newSecs.toInt() % 5 == 0)) {
                    val updatedInvs = _investments.value.map { asset ->
                        val drift = (Random.nextDouble() - 0.49) * 0.015 // realistic daily drift
                        val newPrice = (asset.currentPrice * (1.0 + drift)).coerceAtLeast(1.0)
                        val baseline = asset.sparkline.firstOrNull() ?: newPrice.toFloat()
                        val newChange = ((newPrice - baseline) / baseline) * 100.0
                        val newSpark = asset.sparkline.toMutableList().apply {
                            if (size >= 12) removeAt(0)
                            add(newPrice.toFloat())
                        }
                        asset.copy(
                            currentPrice = (newPrice * 100.0).toLong() / 100.0,
                            changePercent = (newChange * 100.0).toLong() / 100.0,
                            sparkline = newSpark
                        )
                    }
                    _investments.value = updatedInvs
                    repo.saveInvestments(updatedInvs)

                    // Keep selectedStock in sync if one is selected
                    val curSelected = _selectedStock.value
                    if (curSelected != null) {
                        _selectedStock.value = updatedInvs.find { it.id == curSelected.id }
                    }

                    // Save profile & timestamp periodically
                    repo.saveProfile(_profile.value)
                    repo.updateLastActiveTimestamp()
                }
            }
        }
    }

    fun selectStock(asset: InvestmentAsset?) {
        _selectedStock.value = asset
    }

    fun setTimeframe(tf: Timeframe) {
        _selectedTimeframe.value = tf
    }

    fun toggleBalanceVisibility() {
        val current = _profile.value
        val updated = current.copy(isBalanceVisible = !current.isBalanceVisible)
        _profile.value = updated
        repo.saveProfile(updated)
    }

    /**
     * Requirement 7: Click to earn $100 per click.
     * Requirement 8: Everything comes with tax applied (15% tax).
     * Gross: $100.00, Tax: -$15.00, Net: +$85.00
     */
    fun clickToEarn() {
        val grossAmount = 100.00
        val taxRate = 0.15
        val taxPaid = grossAmount * taxRate // $15.00
        val netEarned = grossAmount - taxPaid // $85.00

        val prof = _profile.value
        val updated = prof.copy(cashBalance = prof.cashBalance + netEarned)
        _profile.value = updated
        repo.saveProfile(updated)

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_click_${System.currentTimeMillis()}",
            title = "Executive Quick Earn",
            category = "Operations",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = netEarned,
            isIncome = true,
            note = "Gross: \$100.00 · Tax: -\$15.00 (15%) · Net: +\$85.00",
            taxDeducted = taxPaid
        )
        _transactions.value = listOf(tx) + _transactions.value.take(49)
    }

    fun earnQuickCash(amount: Double = 1250.0, sourceTitle: String = "Consulting Retainer") {
        val taxRate = 0.15
        val taxPaid = amount * taxRate
        val net = amount - taxPaid

        val prof = _profile.value
        val updated = prof.copy(cashBalance = prof.cashBalance + net)
        _profile.value = updated
        repo.saveProfile(updated)

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = sourceTitle,
            category = "Executive Revenue",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = net,
            isIncome = true,
            note = "Gross: \$${String.format(Locale.US, "%,.2f", amount)} · Tax (15%): -\$${String.format(Locale.US, "%,.2f", taxPaid)}",
            taxDeducted = taxPaid
        )
        _transactions.value = listOf(tx) + _transactions.value
    }

    /**
     * Requirement 4: Explore investments button buys shares
     * Requirement 8: Everything comes with tax applied (1% securities transaction tax)
     */
    fun buyInvestment(assetId: String, sharesToBuy: Double): Boolean {
        val asset = _investments.value.find { it.id == assetId } ?: return false
        val subtotal = sharesToBuy * asset.currentPrice
        val taxDuty = subtotal * 0.01 // 1% stamp tax / fee
        val totalCost = subtotal + taxDuty

        val prof = _profile.value
        if (prof.cashBalance < totalCost) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalCost)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedInvs = _investments.value.map {
            if (it.id == assetId) {
                val newShares = it.sharesOwned + sharesToBuy
                val oldTotalInvested = it.sharesOwned * it.avgBuyPrice
                val newTotalInvested = oldTotalInvested + subtotal
                val newAvgBuyPrice = if (newShares > 0) newTotalInvested / newShares else asset.currentPrice
                it.copy(
                    sharesOwned = newShares,
                    avgBuyPrice = newAvgBuyPrice
                )
            } else it
        }
        _investments.value = updatedInvs
        repo.saveInvestments(updatedInvs)

        // Keep selected stock state in sync
        _selectedStock.value = updatedInvs.find { it.id == assetId }

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Bought ${asset.name} (${asset.symbol})",
            category = "Securities",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalCost,
            isIncome = false,
            note = "Bought ${sharesToBuy.toInt()} shares at \$${String.format(Locale.US, "%.2f", asset.currentPrice)} (Tax: \$${String.format(Locale.US, "%.2f", taxDuty)})",
            taxDeducted = taxDuty
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun sellInvestment(assetId: String, sharesToSell: Double): Boolean {
        val asset = _investments.value.find { it.id == assetId } ?: return false
        if (asset.sharesOwned < sharesToSell) return false

        val grossProceeds = sharesToSell * asset.currentPrice
        val costBasis = sharesToSell * asset.avgBuyPrice
        val profit = (grossProceeds - costBasis).coerceAtLeast(0.0)
        val capitalGainsTax = profit * 0.15 // 15% Capital Gains Tax on profit
        val netProceeds = grossProceeds - capitalGainsTax

        val prof = _profile.value
        val updatedProf = prof.copy(cashBalance = prof.cashBalance + netProceeds)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedInvs = _investments.value.map {
            if (it.id == assetId) {
                val remainingShares = it.sharesOwned - sharesToSell
                it.copy(
                    sharesOwned = remainingShares,
                    avgBuyPrice = if (remainingShares > 0) it.avgBuyPrice else 0.0
                )
            } else it
        }
        _investments.value = updatedInvs
        repo.saveInvestments(updatedInvs)

        _selectedStock.value = updatedInvs.find { it.id == assetId }

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Sold ${asset.name} (${asset.symbol})",
            category = "Securities",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = netProceeds,
            isIncome = true,
            note = "Sold ${sharesToSell.toInt()} shares. Profit: \$${String.format(Locale.US, "%.2f", profit)} · Cap Gains Tax (15%): -\$${String.format(Locale.US, "%.2f", capitalGainsTax)}",
            taxDeducted = capitalGainsTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun upgradeBusiness(bizId: String): Boolean {
        val biz = _businesses.value.find { it.id == bizId } ?: return false
        val baseCost = biz.upgradeCost
        val commercialPermitTax = baseCost * 0.05 // 5% commercial tax
        val totalCost = baseCost + commercialPermitTax

        val prof = _profile.value
        if (prof.cashBalance < totalCost) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalCost)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedBizList = _businesses.value.map {
            if (it.id == bizId) {
                it.copy(
                    level = it.level + 1,
                    upgradeCost = it.upgradeCost * 1.65
                )
            } else it
        }
        _businesses.value = updatedBizList

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "${biz.name} Upgrade (Tier ${biz.level + 1})",
            category = "Enterprise",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalCost,
            isIncome = false,
            note = "Cost: \$${String.format(Locale.US, "%,.0f", baseCost)} · Permit Tax (5%): \$${String.format(Locale.US, "%,.0f", commercialPermitTax)}",
            taxDeducted = commercialPermitTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun hireManager(bizId: String): Boolean {
        val biz = _businesses.value.find { it.id == bizId } ?: return false
        if (biz.isAutomated) return false
        val baseCost = biz.managerCost
        val payrollTax = baseCost * 0.10 // 10% payroll tax
        val totalCost = baseCost + payrollTax

        val prof = _profile.value
        if (prof.cashBalance < totalCost) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalCost)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedBizList = _businesses.value.map {
            if (it.id == bizId) it.copy(isAutomated = true) else it
        }
        _businesses.value = updatedBizList

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Executive CEO Appointed: ${biz.name}",
            category = "Executive",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalCost,
            isIncome = false,
            note = "Fee: \$${String.format(Locale.US, "%,.0f", baseCost)} · Payroll Tax: \$${String.format(Locale.US, "%,.0f", payrollTax)}",
            taxDeducted = payrollTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun buyLuxuryAsset(assetId: String): Boolean {
        val asset = _luxuryAssets.value.find { it.id == assetId } ?: return false
        if (asset.isOwned) return false

        // Check prerequisites
        if (asset.category.equals("Yachts", ignoreCase = true)) {
            val hasIsland = _luxuryAssets.value.any { it.category.equals("Islands", ignoreCase = true) && it.isOwned }
            if (!hasIsland) return false
        }
        if (asset.category.equals("Private Jets", ignoreCase = true)) {
            val hasHangar = _luxuryAssets.value.any { it.category.equals("Hangars", ignoreCase = true) && it.isOwned }
            if (!hasHangar) return false
        }

        val basePrice = asset.price
        val luxuryTax = basePrice * 0.08 // 8% luxury asset tax
        val totalCost = basePrice + luxuryTax

        val prof = _profile.value
        if (prof.cashBalance < totalCost) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalCost)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedAssets = _luxuryAssets.value.map {
            if (it.id == assetId) it.copy(isOwned = true) else it
        }
        _luxuryAssets.value = updatedAssets

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Acquired ${asset.name}",
            category = "Luxury Assets",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalCost,
            isIncome = false,
            note = "Price: \$${String.format(Locale.US, "%,.0f", basePrice)} · Luxury Tax (8%): \$${String.format(Locale.US, "%,.0f", luxuryTax)}",
            taxDeducted = luxuryTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun buyDealershipCar(carId: String): Boolean {
        val car = _dealershipCars.value.find { it.id == carId } ?: return false
        val basePrice = car.priceBuy
        val salesTax = basePrice * 0.08 // 8% automotive tax
        val totalCost = basePrice + salesTax

        val prof = _profile.value
        if (prof.cashBalance < totalCost) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalCost)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedCars = _dealershipCars.value.map {
            if (it.id == carId) it.copy(isBought = true) else it
        }
        _dealershipCars.value = updatedCars
        repo.saveDealershipCars(updatedCars)

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Purchased ${car.name}",
            category = "Automotive Dealership",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalCost,
            isIncome = false,
            note = "Price: \$${String.format(Locale.US, "%,.0f", basePrice)} · Vehicle Tax (8%): \$${String.format(Locale.US, "%,.0f", salesTax)}",
            taxDeducted = salesTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun leaseDealershipCar(carId: String): Boolean {
        val car = _dealershipCars.value.find { it.id == carId } ?: return false
        val baseMonthly = car.priceLeaseMonthly
        val leaseTax = baseMonthly * 0.08 // 8% tax
        val totalMonthly = baseMonthly + leaseTax

        val prof = _profile.value
        if (prof.cashBalance < totalMonthly) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalMonthly)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedCars = _dealershipCars.value.map {
            if (it.id == carId) it.copy(isLeased = true) else it
        }
        _dealershipCars.value = updatedCars
        repo.saveDealershipCars(updatedCars)

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Leased ${car.name}",
            category = "Leasing & Finance",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalMonthly,
            isIncome = false,
            note = "Installment: \$${String.format(Locale.US, "%,.0f", baseMonthly)} · Tax (8%): \$${String.format(Locale.US, "%,.0f", leaseTax)}",
            taxDeducted = leaseTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun sendWireTransfer(recipient: String, amount: Double, note: String): Boolean {
        val wireFeeTax = amount * 0.01 // 1% transfer tax
        val total = amount + wireFeeTax

        val prof = _profile.value
        if (prof.cashBalance < total) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - total)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Wire Transfer to $recipient",
            category = "Transfers",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = total,
            isIncome = false,
            note = "${note.ifBlank { "Offshore liquidity disbursement" }} (Transfer Tax: \$${String.format(Locale.US, "%.2f", wireFeeTax)})",
            taxDeducted = wireFeeTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun updatePlayerName(newName: String) {
        val updated = _profile.value.copy(name = newName)
        _profile.value = updated
        repo.saveProfile(updated)
    }

    fun markNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun toggleDealerEditMode() {
        val current = _dealershipSettings.value
        val updated = current.copy(isEditModeActive = !current.isEditModeActive)
        _dealershipSettings.value = updated
        repo.saveDealershipSettings(updated)
    }

    fun updateDealershipHeroImage(newUri: String) {
        val current = _dealershipSettings.value
        val updated = current.copy(heroImageUri = newUri)
        _dealershipSettings.value = updated
        repo.saveDealershipSettings(updated)
    }

    fun updateDealershipShowroomImage(newUri: String) {
        val current = _dealershipSettings.value
        val updated = current.copy(showroomImageUri = newUri)
        _dealershipSettings.value = updated
        repo.saveDealershipSettings(updated)
    }

    fun updateCar(updatedCar: DealershipCar) {
        val updatedList = _dealershipCars.value.map {
            if (it.id == updatedCar.id) updatedCar else it
        }
        _dealershipCars.value = updatedList
        repo.saveDealershipCars(updatedList)
    }

    fun addCar(newCar: DealershipCar) {
        val updatedList = _dealershipCars.value + newCar
        _dealershipCars.value = updatedList
        repo.saveDealershipCars(updatedList)
    }

    fun deleteCar(carId: String) {
        val updatedList = _dealershipCars.value.filter { it.id != carId }
        _dealershipCars.value = updatedList
        repo.saveDealershipCars(updatedList)
    }

    fun toggleCarFavorite(carId: String) {
        val updatedList = _dealershipCars.value.map {
            if (it.id == carId) it.copy(isFavorite = !it.isFavorite) else it
        }
        _dealershipCars.value = updatedList
        repo.saveDealershipCars(updatedList)
    }

    fun updateLuxuryAsset(updatedAsset: LuxuryAsset) {
        val updatedList = _luxuryAssets.value.map {
            if (it.id == updatedAsset.id) updatedAsset else it
        }
        _luxuryAssets.value = updatedList
        repo.saveLuxuryAssets(updatedList)
    }

    /** Adds a new editable mansion catalog slot without changing existing assets or player progress. */
    fun addMansionSlot() {
        val existingMansions = _luxuryAssets.value.filter { it.category == "Mansions" }
        val slotNumber = existingMansions.size + 1
        val newAsset = LuxuryAsset(
            id = "mansion_slot_${System.currentTimeMillis()}",
            name = "New Mansion Slot $slotNumber",
            category = "Mansions",
            price = 2_500_000.0,
            imageUri = "img_hero_suv",
            prestige = 70,
            isOwned = false,
            yieldPercentage = 4.0,
            description = "New mansion slot. Edit its details and photo before publishing it in the shop.",
            bedrooms = 5,
            baths = 4,
            internet = "High-speed Fiber",
            dimension = "736x736"
        )
        val updatedList = _luxuryAssets.value + newAsset
        _luxuryAssets.value = updatedList
        repo.saveLuxuryAssets(updatedList)
    }

    fun leaseLuxuryAsset(assetId: String): Boolean {
        val asset = _luxuryAssets.value.find { it.id == assetId } ?: return false
        val baseMonthly = asset.rentLeaseMonthly
        val leaseTax = baseMonthly * 0.08 // 8% tax
        val totalMonthly = baseMonthly + leaseTax

        val prof = _profile.value
        if (prof.cashBalance < totalMonthly) return false

        val updatedProf = prof.copy(cashBalance = prof.cashBalance - totalMonthly)
        _profile.value = updatedProf
        repo.saveProfile(updatedProf)

        val updatedAssets = _luxuryAssets.value.map {
            if (it.id == assetId) it.copy(isLeased = true) else it
        }
        _luxuryAssets.value = updatedAssets
        repo.saveLuxuryAssets(updatedAssets)

        val cal = _calendar.value
        val tx = TycoonTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Leased ${asset.name}",
            category = "Leasing & Finance",
            timestamp = "Year ${cal.year}, Day ${cal.dayOfYear}",
            amount = totalMonthly,
            isIncome = false,
            note = "Lease: \$${String.format(Locale.US, "%,.0f", baseMonthly)} · Tax (8%): \$${String.format(Locale.US, "%,.0f", leaseTax)}",
            taxDeducted = leaseTax
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun updateOfflineTaxPercent(percent: Double) {
        val current = _dealershipSettings.value
        val updated = current.copy(offlineTaxPercent = percent)
        _dealershipSettings.value = updated
        repo.saveDealershipSettings(updated)
    }

    fun resetData() {
        val defaultProf = PlayerProfile()
        _profile.value = defaultProf
        repo.saveProfile(defaultProf)
        _investments.value = repo.getInitialInvestments()
        _selectedStock.value = null
        _businesses.value = repo.getInitialBusinesses()
        _luxuryAssets.value = repo.getInitialLuxuryAssets()
        _transactions.value = repo.getInitialTransactions()
        _dealershipCars.value = repo.loadDealershipCars()
        _dealershipSettings.value = DealershipSettings()
        _calendar.value = InGameCalendar()
        repo.saveCalendar(InGameCalendar())
    }
}
