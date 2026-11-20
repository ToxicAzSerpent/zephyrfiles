package com.example.model

enum class Timeframe(val label: String) {
    ONE_WEEK("1W"),
    ONE_MONTH("1M"),
    THREE_MONTHS("3M"),
    SIX_MONTHS("6M"),
    ONE_YEAR("1Y"),
    ALL("ALL")
}

data class PlayerProfile(
    val name: String = "Sophia Moore",
    val title: String = "Executive Chairman & Founder",
    val cashBalance: Double = 82745.60,
    val monthlyGrowthPercent: Double = 4.6,
    val isBalanceVisible: Boolean = true
)

enum class AssetCategory {
    TECH, LUXURY, REAL_ESTATE, COMMODITY, AEROSPACE, AUTOMOTIVE, FINANCE, ENERGY, HEALTHCARE
}

data class InvestmentAsset(
    val id: String,
    val name: String,
    val symbol: String,
    val category: AssetCategory,
    val sharesOwned: Double = 0.0,
    val avgBuyPrice: Double = 0.0,
    val currentPrice: Double,
    val changePercent: Double,
    val sparkline: List<Float>
) {
    val totalValue: Double get() = sharesOwned * currentPrice
    val totalInvested: Double get() = sharesOwned * avgBuyPrice
    val profitLoss: Double get() = if (sharesOwned > 0 && avgBuyPrice > 0) totalValue - totalInvested else 0.0
    val profitLossPercent: Double get() = if (sharesOwned > 0 && totalInvested > 0) ((totalValue - totalInvested) / totalInvested) * 100.0 else 0.0
}

data class BusinessEnterprise(
    val id: String,
    val name: String,
    val category: String,
    val level: Int,
    val baseRevenuePerSec: Double,
    val upgradeCost: Double,
    val isAutomated: Boolean,
    val managerCost: Double,
    val description: String
) {
    val currentRevenuePerSec: Double get() = baseRevenuePerSec * level * (if (isAutomated) 1.5 else 1.0)
    val currentRevenuePerHour: Double get() = currentRevenuePerSec * 3600
}

data class LuxuryAsset(
    val id: String,
    val name: String,
    val category: String, // Mansions, Private Jets, Yachts, Motorcycles, Apparel, Watches, Jewelry, Shoes, Phones, Bags
    val price: Double,
    val rentLeaseMonthly: Double = 1500.0,
    val currency: String = "$",
    val imageUri: String = "img_hero_suv",
    val prestige: Int,
    val isOwned: Boolean = false,
    val isLeased: Boolean = false,
    val yieldPercentage: Double = 4.0,
    val description: String,
    val bedrooms: Int? = null,
    val baths: Int? = null,
    val internet: String? = null,
    val dimension: String? = null // e.g., "736x736" for Mansions
)

data class TycoonTransaction(
    val id: String,
    val title: String,
    val category: String,
    val timestamp: String,
    val amount: Double,
    val isIncome: Boolean,
    val note: String = "",
    val taxDeducted: Double = 0.0
)

data class DealershipCar(
    val id: String,
    val name: String,
    val brand: String,
    val imageUri: String, // "img_bmw_x5", "img_mercedes_gle", "img_porsche_cayenne", or local file
    val year: String = "2024",
    val engine: String = "3.0L Turbo",
    val power: String = "360 HP",
    val mileage: String = "12,500 mi",
    val priceBuy: Double = 98500.0,
    val priceLeaseMonthly: Double = 1450.0,
    val currency: String = "$",
    val isFavorite: Boolean = false,
    val isLeased: Boolean = false,
    val isBought: Boolean = false
)

data class DealershipSettings(
    val isEditModeActive: Boolean = false,
    val heroImageUri: String = "img_hero_suv",
    val showroomImageUri: String = "img_dealership_showroom",
    val preferredCurrency: String = "$",
    val offlineTaxPercent: Double = 5.0
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

data class InGameCalendar(
    val totalDays: Long = 1,
    val secondsIntoDay: Float = 0f
) {
    val year: Long get() = ((totalDays - 1) / 365) + 1
    val dayOfYear: Long get() = ((totalDays - 1) % 365) + 1
}

data class OfflineEarningsReport(
    val elapsedSeconds: Long,
    val inGameDaysPassed: Long,
    val grossIncome: Double,
    val taxPaid: Double,
    val netIncome: Double
)
