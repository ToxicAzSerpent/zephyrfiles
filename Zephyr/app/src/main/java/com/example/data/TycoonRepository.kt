package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class TycoonRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("zephyr_tycoon_prefs", Context.MODE_PRIVATE)

    fun loadProfile(): PlayerProfile {
        val name = prefs.getString("player_name", "Sophia Moore") ?: "Sophia Moore"
        val title = prefs.getString("player_title", "Executive Chairman") ?: "Executive Chairman"
        val cash = prefs.getFloat("player_cash", 82745.60f).toDouble()
        val growth = prefs.getFloat("player_growth", 4.6f).toDouble()
        val visible = prefs.getBoolean("balance_visible", true)
        return PlayerProfile(
            name = name,
            title = title,
            cashBalance = cash,
            monthlyGrowthPercent = growth,
            isBalanceVisible = visible
        )
    }

    fun saveProfile(profile: PlayerProfile) {
        prefs.edit()
            .putString("player_name", profile.name)
            .putString("player_title", profile.title)
            .putFloat("player_cash", profile.cashBalance.toFloat())
            .putFloat("player_growth", profile.monthlyGrowthPercent.toFloat())
            .putBoolean("balance_visible", profile.isBalanceVisible)
            .apply()
    }

    fun loadCalendar(): InGameCalendar {
        val days = prefs.getLong("calendar_total_days", 1L)
        val secs = prefs.getFloat("calendar_seconds_into_day", 0f)
        return InGameCalendar(totalDays = days.coerceAtLeast(1L), secondsIntoDay = secs)
    }

    fun saveCalendar(calendar: InGameCalendar) {
        prefs.edit()
            .putLong("calendar_total_days", calendar.totalDays)
            .putFloat("calendar_seconds_into_day", calendar.secondsIntoDay)
            .apply()
    }

    fun getLastActiveTimestamp(): Long {
        return prefs.getLong("last_active_timestamp", System.currentTimeMillis())
    }

    fun updateLastActiveTimestamp() {
        prefs.edit().putLong("last_active_timestamp", System.currentTimeMillis()).apply()
    }

    fun getInitialInvestments(): List<InvestmentAsset> {
        val jsonStr = prefs.getString("investments_json_v2", null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<InvestmentAsset>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val sparkArray = obj.getJSONArray("sparkline")
                    val spark = mutableListOf<Float>()
                    for (j in 0 until sparkArray.length()) {
                        spark.add(sparkArray.getDouble(j).toFloat())
                    }
                    val catStr = obj.optString("category", "TECH")
                    val cat = try { AssetCategory.valueOf(catStr) } catch (e: Exception) { AssetCategory.TECH }
                    list.add(
                        InvestmentAsset(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            symbol = obj.getString("symbol"),
                            category = cat,
                            sharesOwned = obj.optDouble("sharesOwned", 0.0),
                            avgBuyPrice = obj.optDouble("avgBuyPrice", 0.0),
                            currentPrice = obj.getDouble("currentPrice"),
                            changePercent = obj.optDouble("changePercent", 0.0),
                            sparkline = spark
                        )
                    )
                }
                if (list.size >= 25) return list
            } catch (e: Exception) {
                // fallback to default
            }
        }

        return createDefaultStockList()
    }

    fun saveInvestments(list: List<InvestmentAsset>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("symbol", item.symbol)
                put("category", item.category.name)
                put("sharesOwned", item.sharesOwned)
                put("avgBuyPrice", item.avgBuyPrice)
                put("currentPrice", item.currentPrice)
                put("changePercent", item.changePercent)
                val sparkArray = JSONArray()
                item.sparkline.forEach { sparkArray.put(it.toDouble()) }
                put("sparkline", sparkArray)
            }
            array.put(obj)
        }
        prefs.edit().putString("investments_json_v2", array.toString()).apply()
    }

    private fun createDefaultStockList(): List<InvestmentAsset> {
        return listOf(
            InvestmentAsset(
                id = "aapl",
                name = "Apple Inc.",
                symbol = "AAPL",
                category = AssetCategory.TECH,
                sharesOwned = 10.0,
                avgBuyPrice = 168.50,
                currentPrice = 189.45,
                changePercent = 2.45,
                sparkline = listOf(175f, 178f, 182f, 180f, 185f, 189.45f)
            ),
            InvestmentAsset(
                id = "nvda",
                name = "NVIDIA Corporation",
                symbol = "NVDA",
                category = AssetCategory.TECH,
                sharesOwned = 15.0,
                avgBuyPrice = 110.00,
                currentPrice = 138.25,
                changePercent = 4.80,
                sparkline = listOf(115f, 122f, 126f, 131f, 135f, 138.25f)
            ),
            InvestmentAsset(
                id = "msft",
                name = "Microsoft Corporation",
                symbol = "MSFT",
                category = AssetCategory.TECH,
                sharesOwned = 8.0,
                avgBuyPrice = 405.00,
                currentPrice = 428.10,
                changePercent = 1.35,
                sparkline = listOf(410f, 415f, 412f, 420f, 425f, 428.10f)
            ),
            InvestmentAsset(
                id = "tsla",
                name = "Tesla Inc.",
                symbol = "TSLA",
                category = AssetCategory.AUTOMOTIVE,
                sharesOwned = 6.0,
                avgBuyPrice = 220.00,
                currentPrice = 248.80,
                changePercent = 3.60,
                sparkline = listOf(225f, 230f, 228f, 240f, 245f, 248.80f)
            ),
            InvestmentAsset(
                id = "googl",
                name = "Alphabet Inc.",
                symbol = "GOOGL",
                category = AssetCategory.TECH,
                sharesOwned = 12.0,
                avgBuyPrice = 155.00,
                currentPrice = 168.90,
                changePercent = -0.75,
                sparkline = listOf(172f, 170f, 169f, 171f, 167f, 168.90f)
            ),
            InvestmentAsset(
                id = "amzn",
                name = "Amazon.com Inc.",
                symbol = "AMZN",
                category = AssetCategory.TECH,
                sharesOwned = 5.0,
                avgBuyPrice = 175.00,
                currentPrice = 186.40,
                changePercent = 1.90,
                sparkline = listOf(178f, 180f, 182f, 181f, 184f, 186.40f)
            ),
            InvestmentAsset(
                id = "meta",
                name = "Meta Platforms Inc.",
                symbol = "META",
                category = AssetCategory.TECH,
                sharesOwned = 4.0,
                avgBuyPrice = 480.00,
                currentPrice = 582.15,
                changePercent = 2.85,
                sparkline = listOf(540f, 555f, 560f, 570f, 578f, 582.15f)
            ),
            InvestmentAsset(
                id = "brk",
                name = "Berkshire Hathaway",
                symbol = "BRK.B",
                category = AssetCategory.FINANCE,
                sharesOwned = 3.0,
                avgBuyPrice = 430.00,
                currentPrice = 452.80,
                changePercent = 0.65,
                sparkline = listOf(440f, 442f, 445f, 448f, 450f, 452.80f)
            ),
            InvestmentAsset(
                id = "tsmc",
                name = "Taiwan Semiconductor",
                symbol = "TSM",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 192.50,
                changePercent = 3.20,
                sparkline = listOf(180f, 183f, 186f, 188f, 190f, 192.50f)
            ),
            InvestmentAsset(
                id = "lvmh",
                name = "LVMH Moët Hennessy",
                symbol = "MC",
                category = AssetCategory.LUXURY,
                sharesOwned = 2.0,
                avgBuyPrice = 690.00,
                currentPrice = 724.00,
                changePercent = 1.40,
                sparkline = listOf(700f, 705f, 712f, 710f, 718f, 724.00f)
            ),
            InvestmentAsset(
                id = "hermes",
                name = "Hermès International",
                symbol = "RMS",
                category = AssetCategory.LUXURY,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 2140.00,
                changePercent = 0.95,
                sparkline = listOf(2080f, 2100f, 2110f, 2125f, 2130f, 2140.00f)
            ),
            InvestmentAsset(
                id = "ferrari",
                name = "Ferrari N.V.",
                symbol = "RACE",
                category = AssetCategory.AUTOMOTIVE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 435.60,
                changePercent = 2.10,
                sparkline = listOf(415f, 420f, 425f, 428f, 432f, 435.60f)
            ),
            InvestmentAsset(
                id = "porsche",
                name = "Porsche AG",
                symbol = "P911",
                category = AssetCategory.AUTOMOTIVE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 82.40,
                changePercent = -0.50,
                sparkline = listOf(85f, 84f, 83.5f, 83f, 82.8f, 82.40f)
            ),
            InvestmentAsset(
                id = "rolex",
                name = "Rolex Watchmaking Consortium",
                symbol = "RLX",
                category = AssetCategory.LUXURY,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 1450.00,
                changePercent = 1.80,
                sparkline = listOf(1400f, 1410f, 1425f, 1430f, 1442f, 1450.00f)
            ),
            InvestmentAsset(
                id = "spacex",
                name = "SpaceX Equity Trust",
                symbol = "SPACEX",
                category = AssetCategory.AEROSPACE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 210.00,
                changePercent = 6.20,
                sparkline = listOf(185f, 190f, 195f, 200f, 205f, 210.00f)
            ),
            InvestmentAsset(
                id = "openai",
                name = "OpenAI Global Holdings",
                symbol = "OPENAI",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 350.00,
                changePercent = 7.40,
                sparkline = listOf(310f, 320f, 330f, 338f, 345f, 350.00f)
            ),
            InvestmentAsset(
                id = "pltr",
                name = "Palantir Technologies",
                symbol = "PLTR",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 43.80,
                changePercent = 4.15,
                sparkline = listOf(38f, 39.5f, 41f, 42f, 43f, 43.80f)
            ),
            InvestmentAsset(
                id = "nvo",
                name = "Novo Nordisk A/S",
                symbol = "NVO",
                category = AssetCategory.HEALTHCARE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 124.60,
                changePercent = 1.25,
                sparkline = listOf(120f, 121f, 122.5f, 123f, 124f, 124.60f)
            ),
            InvestmentAsset(
                id = "lly",
                name = "Eli Lilly and Company",
                symbol = "LLY",
                category = AssetCategory.HEALTHCARE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 890.30,
                changePercent = 2.50,
                sparkline = listOf(860f, 870f, 875f, 882f, 888f, 890.30f)
            ),
            InvestmentAsset(
                id = "jpm",
                name = "JPMorgan Chase & Co.",
                symbol = "JPM",
                category = AssetCategory.FINANCE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 222.15,
                changePercent = 0.80,
                sparkline = listOf(216f, 218f, 219f, 220f, 221f, 222.15f)
            ),
            InvestmentAsset(
                id = "v",
                name = "Visa Inc.",
                symbol = "V",
                category = AssetCategory.FINANCE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 284.90,
                changePercent = 1.10,
                sparkline = listOf(278f, 280f, 281f, 283f, 284f, 284.90f)
            ),
            InvestmentAsset(
                id = "cvx",
                name = "Chevron Corporation",
                symbol = "CVX",
                category = AssetCategory.ENERGY,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 152.40,
                changePercent = -0.30,
                sparkline = listOf(154f, 153.5f, 153f, 152.8f, 152.6f, 152.40f)
            ),
            InvestmentAsset(
                id = "xom",
                name = "Exxon Mobil Corp.",
                symbol = "XOM",
                category = AssetCategory.ENERGY,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 120.75,
                changePercent = 0.45,
                sparkline = listOf(118f, 119f, 119.5f, 120f, 120.2f, 120.75f)
            ),
            InvestmentAsset(
                id = "sony",
                name = "Sony Group Corporation",
                symbol = "SONY",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 96.20,
                changePercent = 1.65,
                sparkline = listOf(92f, 93.5f, 94f, 95f, 95.8f, 96.20f)
            ),
            InvestmentAsset(
                id = "ntdoy",
                name = "Nintendo Co. Ltd.",
                symbol = "NTDOY",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 14.80,
                changePercent = 2.10,
                sparkline = listOf(14.0f, 14.2f, 14.3f, 14.5f, 14.7f, 14.80f)
            ),
            InvestmentAsset(
                id = "asml",
                name = "ASML Holding N.V.",
                symbol = "ASML",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 760.00,
                changePercent = 3.50,
                sparkline = listOf(720f, 730f, 740f, 748f, 755f, 760.00f)
            ),
            InvestmentAsset(
                id = "arm",
                name = "Arm Holdings plc",
                symbol = "ARM",
                category = AssetCategory.TECH,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 142.30,
                changePercent = 5.10,
                sparkline = listOf(130f, 133f, 136f, 139f, 141f, 142.30f)
            ),
            InvestmentAsset(
                id = "lmt",
                name = "Lockheed Martin Corp.",
                symbol = "LMT",
                category = AssetCategory.AEROSPACE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 585.00,
                changePercent = 0.90,
                sparkline = listOf(572f, 576f, 578f, 581f, 583f, 585.00f)
            ),
            InvestmentAsset(
                id = "rolls",
                name = "Rolls-Royce Holdings",
                symbol = "RR",
                category = AssetCategory.AEROSPACE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 6.95,
                changePercent = 4.20,
                sparkline = listOf(6.2f, 6.4f, 6.55f, 6.7f, 6.85f, 6.95f)
            ),
            InvestmentAsset(
                id = "zephyr_capital",
                name = "Zephyr Sovereign Capital",
                symbol = "ZPHR",
                category = AssetCategory.FINANCE,
                sharesOwned = 100.0,
                avgBuyPrice = 380.00,
                currentPrice = 432.20,
                changePercent = 5.42,
                sparkline = listOf(400f, 412f, 420f, 425f, 428f, 432.20f)
            ),
            InvestmentAsset(
                id = "gold",
                name = "Physical Gold Bullion",
                symbol = "XAU",
                category = AssetCategory.COMMODITY,
                sharesOwned = 2.0,
                avgBuyPrice = 2450.00,
                currentPrice = 2685.00,
                changePercent = 1.85,
                sparkline = listOf(2550f, 2580f, 2620f, 2640f, 2665f, 2685.00f)
            ),
            InvestmentAsset(
                id = "silver",
                name = "Sovereign Silver Vault",
                symbol = "XAG",
                category = AssetCategory.COMMODITY,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 31.80,
                changePercent = 2.40,
                sparkline = listOf(29.5f, 30.1f, 30.8f, 31.2f, 31.5f, 31.80f)
            ),
            InvestmentAsset(
                id = "dubai_re",
                name = "Dubai Marina Luxury REIT",
                symbol = "DREIT",
                category = AssetCategory.REAL_ESTATE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 125.00,
                changePercent = 3.10,
                sparkline = listOf(118f, 120f, 121.5f, 123f, 124f, 125.00f)
            ),
            InvestmentAsset(
                id = "manhattan_re",
                name = "Manhattan Penthouse Fund",
                symbol = "MPHF",
                category = AssetCategory.REAL_ESTATE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 250.00,
                changePercent = 1.75,
                sparkline = listOf(240f, 242f, 245f, 247f, 248f, 250.00f)
            ),
            InvestmentAsset(
                id = "swiss_bank",
                name = "Geneva Private Banking Group",
                symbol = "GPBG",
                category = AssetCategory.FINANCE,
                sharesOwned = 0.0,
                avgBuyPrice = 0.0,
                currentPrice = 480.00,
                changePercent = 1.20,
                sparkline = listOf(465f, 470f, 472f, 475f, 478f, 480.00f)
            )
        )
    }

    fun getInitialBusinesses(): List<BusinessEnterprise> {
        return listOf(
            BusinessEnterprise(
                id = "biz_coffee",
                name = "Elysian Coffee Atelier",
                category = "Luxury Hospitality",
                level = 3,
                baseRevenuePerSec = 1.25,
                upgradeCost = 4500.0,
                isAutomated = true,
                managerCost = 8500.0,
                description = "Artisanal specialty café roasting single-origin beans for Mayfair executives."
            ),
            BusinessEnterprise(
                id = "biz_private_equity",
                name = "Aethelgard Venture Partners",
                category = "Private Equity",
                level = 2,
                baseRevenuePerSec = 4.80,
                upgradeCost = 18500.0,
                isAutomated = true,
                managerCost = 35000.0,
                description = "Boutique seed-stage fund allocating growth capital to generative tech founders."
            ),
            BusinessEnterprise(
                id = "biz_aero_charter",
                name = "Zephyr Air Charters",
                category = "Aviation & Logistics",
                level = 1,
                baseRevenuePerSec = 12.50,
                upgradeCost = 65000.0,
                isAutomated = false,
                managerCost = 120000.0,
                description = "On-demand private aviation fleet connecting European capitals with Swiss ski resorts."
            ),
            BusinessEnterprise(
                id = "biz_watch_atelier",
                name = "Chronos Horology Geneva",
                category = "Haute Horlogerie",
                level = 1,
                baseRevenuePerSec = 28.00,
                upgradeCost = 150000.0,
                isAutomated = false,
                managerCost = 280000.0,
                description = "Handcrafted tourbillon timepieces with 18-month waitlists for high net worth collectors."
            )
        )
    }

    fun getInitialTransactions(): List<TycoonTransaction> {
        return listOf(
            TycoonTransaction(
                id = "tx_1",
                title = "Executive Consulting Fee",
                category = "Royalties",
                timestamp = "Year 1, Day 1",
                amount = 1250.00,
                isIncome = true,
                note = "Design system license distribution",
                taxDeducted = 187.50
            ),
            TycoonTransaction(
                id = "tx_2",
                title = "Coffee Atelier Dividend",
                category = "Dividends",
                timestamp = "Year 1, Day 1",
                amount = 450.00,
                isIncome = true,
                note = "Mayfair private roastery morning revenue",
                taxDeducted = 67.50
            ),
            TycoonTransaction(
                id = "tx_3",
                title = "Zephyr Holdings Dividend",
                category = "Dividends",
                timestamp = "Year 1, Day 1",
                amount = 8450.00,
                isIncome = true,
                note = "Quarterly sovereign equity allocation",
                taxDeducted = 1267.50
            )
        )
    }

    fun getInitialLuxuryAssets(): List<LuxuryAsset> {
        return listOf(
            // Mansions
            LuxuryAsset(
                id = "mansion_bh",
                name = "Beverly Hills Modern Mansion",
                category = "Mansions",
                price = 25000000.0,
                rentLeaseMonthly = 95000.0,
                currency = "$",
                imageUri = "img_hero_suv",
                prestige = 95,
                isOwned = false,
                yieldPercentage = 4.5,
                description = "Ultra-luxury modern estate with panoramic canyon views, infinity pool, and subterranean garage.",
                bedrooms = 6,
                baths = 8,
                internet = "10 Gbps Dedicated Fiber",
                dimension = "736x736"
            ),
            LuxuryAsset(
                id = "mansion_monaco",
                name = "Monaco Cliffside Villa",
                category = "Mansions",
                price = 38000000.0,
                rentLeaseMonthly = 140000.0,
                currency = "$",
                imageUri = "img_dealership_showroom",
                prestige = 98,
                isOwned = false,
                yieldPercentage = 4.8,
                description = "Mediterranean cliffside masterpiece overlooking the Prince's Palace and private yacht harbor.",
                bedrooms = 7,
                baths = 9,
                internet = "5 Gbps Starlink Ultra",
                dimension = "736x736"
            ),
            LuxuryAsset(
                id = "mansion_aspen",
                name = "Aspen Snowmass Alpine Estate",
                category = "Mansions",
                price = 19000000.0,
                rentLeaseMonthly = 75000.0,
                currency = "$",
                imageUri = "img_porsche_cayenne",
                prestige = 90,
                isOwned = true,
                yieldPercentage = 5.0,
                description = "Ski-in ski-out timber and glass chalet with heated outdoor spa and wine cellar.",
                bedrooms = 5,
                baths = 6,
                internet = "2 Gbps Fiber",
                dimension = "736x736"
            ),

            // Private Jets
            LuxuryAsset(
                id = "jet_g700",
                name = "Gulfstream G700 Executive",
                category = "Private Jets",
                price = 75000000.0,
                rentLeaseMonthly = 250000.0,
                currency = "$",
                imageUri = "img_hero_suv",
                prestige = 100,
                yieldPercentage = 6.0,
                description = "Intercontinental flagship private jet with ultra-galley, master suite, and 5 cabin zones."
            ),
            LuxuryAsset(
                id = "jet_global7500",
                name = "Bombardier Global 7500",
                category = "Private Jets",
                price = 72000000.0,
                rentLeaseMonthly = 240000.0,
                currency = "$",
                imageUri = "img_dealership_showroom",
                prestige = 100,
                yieldPercentage = 6.0,
                description = "Longest range business jet capable of non-stop London to Tokyo transit."
            ),

            // Yachts
            LuxuryAsset(
                id = "yacht_super",
                name = "Zephyr 68M Superyacht",
                category = "Yachts",
                price = 48000000.0,
                rentLeaseMonthly = 180000.0,
                currency = "$",
                imageUri = "img_mercedes_gle",
                prestige = 99,
                yieldPercentage = 3.5,
                description = "Custom hybrid exploration yacht with helipad, beach club, and twin cinemas."
            ),
            LuxuryAsset(
                id = "yacht_riva",
                name = "Riva 110 Dolcevita",
                category = "Yachts",
                price = 16500000.0,
                rentLeaseMonthly = 65000.0,
                currency = "$",
                imageUri = "img_bmw_x5",
                prestige = 92,
                yieldPercentage = 4.0,
                description = "Flybridge fiberglass yacht combining aerodynamic design with luxurious Italian craftsmanship."
            ),

            // Motorcycles
            LuxuryAsset(
                id = "moto_ducati",
                name = "Ducati Panigale V4 R Speciale",
                category = "Motorcycles",
                price = 45000.0,
                rentLeaseMonthly = 950.0,
                currency = "$",
                imageUri = "img_porsche_cayenne",
                prestige = 75,
                yieldPercentage = 2.0,
                description = "Track-derived homologation superbike with dry clutch and carbon aerodynamic wings."
            ),
            LuxuryAsset(
                id = "moto_bmw",
                name = "BMW M 1000 RR Carbon",
                category = "Motorcycles",
                price = 38500.0,
                rentLeaseMonthly = 820.0,
                currency = "$",
                imageUri = "img_bmw_x5",
                prestige = 72,
                yieldPercentage = 2.0,
                description = "High-performance racing motorcycle with M GPS lap trigger and carbon chassis."
            ),

            // Apparel
            LuxuryAsset(
                id = "apparel_tomford",
                name = "Tom Ford Custom Vicuna Tuxedo",
                category = "Apparel",
                price = 18500.0,
                rentLeaseMonthly = 400.0,
                currency = "$",
                imageUri = "img_hero_suv",
                prestige = 68,
                yieldPercentage = 1.5,
                description = "Bespoke formalwear tailored from ultra-rare Andean vicuña wool."
            ),
            LuxuryAsset(
                id = "apparel_brioni",
                name = "Brioni Cashmere Overcoat",
                category = "Apparel",
                price = 12000.0,
                rentLeaseMonthly = 280.0,
                currency = "$",
                imageUri = "img_mercedes_gle",
                prestige = 65,
                yieldPercentage = 1.5,
                description = "Hand-stitched pure cashmere overcoat crafted in Rome."
            ),

            // Watches
            LuxuryAsset(
                id = "watch_patek",
                name = "Patek Philippe Grandmaster Chime",
                category = "Watches",
                price = 2500000.0,
                rentLeaseMonthly = 15000.0,
                currency = "$",
                imageUri = "img_dealership_showroom",
                prestige = 96,
                yieldPercentage = 7.0,
                description = "Most complicated timepiece featuring 20 complications and reversible case."
            ),
            LuxuryAsset(
                id = "watch_rolex",
                name = "Rolex Daytona Rainbow Platinum",
                category = "Watches",
                price = 450000.0,
                rentLeaseMonthly = 3500.0,
                currency = "$",
                imageUri = "img_porsche_cayenne",
                prestige = 88,
                yieldPercentage = 6.5,
                description = "Exclusive chronograph set with baguette-cut multicolored sapphires."
            ),

            // Jewelry
            LuxuryAsset(
                id = "jewel_cartier",
                name = "Cartier Panthère Diamond Necklace",
                category = "Jewelry",
                price = 3200000.0,
                rentLeaseMonthly = 20000.0,
                currency = "$",
                imageUri = "img_hero_suv",
                prestige = 97,
                yieldPercentage = 5.5,
                description = "Articulated platinum panther set with brilliant-cut diamonds and emerald eyes."
            ),
            LuxuryAsset(
                id = "jewel_winston",
                name = "Harry Winston Sapphire Ring",
                category = "Jewelry",
                price = 1800000.0,
                rentLeaseMonthly = 12000.0,
                currency = "$",
                imageUri = "img_bmw_x5",
                prestige = 94,
                yieldPercentage = 5.0,
                description = "15-carat Kashmir cushion-cut sapphire flanked by trillion-dollar diamond side stones."
            ),

            // Shoes
            LuxuryAsset(
                id = "shoe_tomford",
                name = "Tom Ford Alligator Dress Loafers",
                category = "Shoes",
                price = 14500.0,
                rentLeaseMonthly = 320.0,
                currency = "$",
                imageUri = "img_mercedes_gle",
                prestige = 64,
                yieldPercentage = 1.0,
                description = "Hand-crafted matte alligator leather loafers with signature metal hardware."
            ),
            LuxuryAsset(
                id = "shoe_berluti",
                name = "Berluti Bespoke Venezia Oxford",
                category = "Shoes",
                price = 8900.0,
                rentLeaseMonthly = 210.0,
                currency = "$",
                imageUri = "img_dealership_showroom",
                prestige = 62,
                yieldPercentage = 1.0,
                description = "Patina leather wholecut oxford with Scritto calligraphy script."
            ),

            // Phones
            LuxuryAsset(
                id = "phone_caviar",
                name = "Caviar iPhone 15 Pro Diamond",
                category = "Phones",
                price = 75000.0,
                rentLeaseMonthly = 1500.0,
                currency = "$",
                imageUri = "img_hero_suv",
                prestige = 78,
                yieldPercentage = 2.5,
                description = "Customized titanium chassis inlaid with 570 certified diamonds and 18k gold."
            ),
            LuxuryAsset(
                id = "phone_vertu",
                name = "Vertu MetaVertu Web3 Gold",
                category = "Phones",
                price = 42000.0,
                rentLeaseMonthly = 900.0,
                currency = "$",
                imageUri = "img_porsche_cayenne",
                prestige = 74,
                yieldPercentage = 2.5,
                description = "Encrypted Web3 smartphone with alligator leather back and ceramic frame."
            ),

            // Islands
            LuxuryAsset(
                id = "island_bahamas",
                name = "Private Bahamas Atoll",
                category = "Islands",
                price = 85000000.0,
                rentLeaseMonthly = 350000.0,
                currency = "$",
                imageUri = "img_hero_suv",
                prestige = 100,
                yieldPercentage = 3.8,
                description = "Pristine private tropical island with white sand beaches, deep-water marina for superyachts, and private airstrip."
            ),
            LuxuryAsset(
                id = "island_maldives",
                name = "Maldivian Coral Lagoon Island",
                category = "Islands",
                price = 65000000.0,
                rentLeaseMonthly = 280000.0,
                currency = "$",
                imageUri = "img_dealership_showroom",
                prestige = 99,
                yieldPercentage = 4.0,
                description = "Exotic Indian Ocean sanctuary with overwater villa complex and protected marine reserve."
            ),

            // Hangars
            LuxuryAsset(
                id = "hangar_geneva",
                name = "Geneva International Private Hangar",
                category = "Hangars",
                price = 12000000.0,
                rentLeaseMonthly = 45000.0,
                currency = "$",
                imageUri = "img_porsche_cayenne",
                prestige = 92,
                yieldPercentage = 5.2,
                description = "Climate-controlled private aviation hangar with dedicated maintenance crew, fueling depot, and executive lounge."
            ),
            LuxuryAsset(
                id = "hangar_van",
                name = "Van Nuys Executive Aviation Hangar",
                category = "Hangars",
                price = 15000000.0,
                rentLeaseMonthly = 55000.0,
                currency = "$",
                imageUri = "img_bmw_x5",
                prestige = 95,
                yieldPercentage = 5.0,
                description = "State-of-the-art LA private jet facility with secure customs clearance and ramp access."
            ),

            // Bags
            LuxuryAsset(
                id = "bag_hermes",
                name = "Hermès Birkin Himalaya Crocodile",
                category = "Bags",
                price = 450000.0,
                rentLeaseMonthly = 4000.0,
                currency = "$",
                imageUri = "img_dealership_showroom",
                prestige = 95,
                yieldPercentage = 8.0,
                description = "The holy grail of handbags crafted from Niloticus crocodile with white gold and diamond hardware."
            ),
            LuxuryAsset(
                id = "bag_lv",
                name = "Louis Vuitton Steamer Crocodile Trunk",
                category = "Bags",
                price = 125000.0,
                rentLeaseMonthly = 12000.0,
                currency = "$",
                imageUri = "img_bmw_x5",
                prestige = 86,
                yieldPercentage = 5.0,
                description = "Héritage trunk in matte black alligator with reinforced brass corners."
            )
        )
    }

    fun loadLuxuryAssets(): List<LuxuryAsset> {
        val jsonStr = prefs.getString("luxury_assets_json_v3", null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<LuxuryAsset>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        LuxuryAsset(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            category = obj.getString("category"),
                            price = obj.getDouble("price"),
                            rentLeaseMonthly = obj.optDouble("rentLeaseMonthly", 1500.0),
                            currency = obj.optString("currency", "$"),
                            imageUri = obj.optString("imageUri", "img_hero_suv"),
                            prestige = obj.getInt("prestige"),
                            isOwned = obj.optBoolean("isOwned", false),
                            isLeased = obj.optBoolean("isLeased", false),
                            yieldPercentage = obj.optDouble("yieldPercentage", 4.0),
                            description = obj.getString("description"),
                            bedrooms = if (obj.has("bedrooms")) obj.getInt("bedrooms") else null,
                            baths = if (obj.has("baths")) obj.getInt("baths") else null,
                            internet = if (obj.has("internet")) obj.getString("internet") else null,
                            dimension = if (obj.has("dimension")) obj.getString("dimension") else null
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                // fallback
            }
        }
        return getInitialLuxuryAssets()
    }

    fun saveLuxuryAssets(assets: List<LuxuryAsset>) {
        val array = JSONArray()
        for (a in assets) {
            val obj = JSONObject().apply {
                put("id", a.id)
                put("name", a.name)
                put("category", a.category)
                put("price", a.price)
                put("rentLeaseMonthly", a.rentLeaseMonthly)
                put("currency", a.currency)
                put("imageUri", a.imageUri)
                put("prestige", a.prestige)
                put("isOwned", a.isOwned)
                put("isLeased", a.isLeased)
                put("yieldPercentage", a.yieldPercentage)
                put("description", a.description)
                a.bedrooms?.let { put("bedrooms", it) }
                a.baths?.let { put("baths", it) }
                a.internet?.let { put("internet", it) }
                a.dimension?.let { put("dimension", it) }
            }
            array.put(obj)
        }
        prefs.edit().putString("luxury_assets_json_v3", array.toString()).apply()
    }

    fun loadDealershipSettings(): DealershipSettings {
        val isEdit = prefs.getBoolean("dealer_edit_mode", false)
        val hero = prefs.getString("dealer_hero_image", "img_hero_suv") ?: "img_hero_suv"
        val showroom = prefs.getString("dealer_showroom_image", "img_dealership_showroom") ?: "img_dealership_showroom"
        val curr = prefs.getString("dealer_currency", "$") ?: "$"
        val offlineTax = prefs.getFloat("offline_tax_percent", 5.0f).toDouble()
        return DealershipSettings(
            isEditModeActive = isEdit,
            heroImageUri = hero,
            showroomImageUri = showroom,
            preferredCurrency = curr,
            offlineTaxPercent = offlineTax
        )
    }

    fun saveDealershipSettings(settings: DealershipSettings) {
        prefs.edit()
            .putBoolean("dealer_edit_mode", settings.isEditModeActive)
            .putString("dealer_hero_image", settings.heroImageUri)
            .putString("dealer_showroom_image", settings.showroomImageUri)
            .putString("dealer_currency", settings.preferredCurrency)
            .putFloat("offline_tax_percent", settings.offlineTaxPercent.toFloat())
            .apply()
    }

    fun loadDealershipCars(): List<DealershipCar> {
        val jsonStr = prefs.getString("dealership_cars_json_v2", null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<DealershipCar>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        DealershipCar(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            brand = obj.getString("brand"),
                            imageUri = obj.getString("imageUri"),
                            year = obj.optString("year", "2024"),
                            engine = obj.optString("engine", "3.0L Turbo"),
                            power = obj.optString("power", "360 HP"),
                            mileage = obj.optString("mileage", "12,500 mi"),
                            priceBuy = obj.optDouble("priceBuy", 98500.0),
                            priceLeaseMonthly = obj.optDouble("priceLeaseMonthly", 1450.0),
                            currency = obj.optString("currency", "$"),
                            isFavorite = obj.optBoolean("isFavorite", false),
                            isLeased = obj.optBoolean("isLeased", false),
                            isBought = obj.optBoolean("isBought", false)
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                // fallback to default
            }
        }

        return listOf(
            DealershipCar(
                id = "car_bmw_x5",
                name = "BMW X5 xDrive40i M Sport",
                brand = "BMW",
                imageUri = "img_bmw_x5",
                year = "2024",
                engine = "3.0L Inline-6 Turbo",
                power = "375 HP",
                mileage = "12,500 mi",
                priceBuy = 98500.0,
                priceLeaseMonthly = 1450.0,
                currency = "$"
            ),
            DealershipCar(
                id = "car_mercedes_gle",
                name = "Mercedes-Benz GLE 450 4MATIC AMG",
                brand = "Mercedes-Benz",
                imageUri = "img_mercedes_gle",
                year = "2024",
                engine = "3.0L Turbo with EQ Boost",
                power = "381 HP",
                mileage = "8,900 mi",
                priceBuy = 104500.0,
                priceLeaseMonthly = 1650.0,
                currency = "$"
            ),
            DealershipCar(
                id = "car_porsche_cayenne",
                name = "Porsche Cayenne S Platinum",
                brand = "Porsche",
                imageUri = "img_porsche_cayenne",
                year = "2024",
                engine = "4.0L Twin-Turbo V8",
                power = "468 HP",
                mileage = "6,200 mi",
                priceBuy = 124000.0,
                priceLeaseMonthly = 1890.0,
                currency = "$"
            )
        )
    }

    fun saveDealershipCars(cars: List<DealershipCar>) {
        val array = JSONArray()
        for (car in cars) {
            val obj = JSONObject().apply {
                put("id", car.id)
                put("name", car.name)
                put("brand", car.brand)
                put("imageUri", car.imageUri)
                put("year", car.year)
                put("engine", car.engine)
                put("power", car.power)
                put("mileage", car.mileage)
                put("priceBuy", car.priceBuy)
                put("priceLeaseMonthly", car.priceLeaseMonthly)
                put("currency", car.currency)
                put("isFavorite", car.isFavorite)
                put("isLeased", car.isLeased)
                put("isBought", car.isBought)
            }
            array.put(obj)
        }
        prefs.edit().putString("dealership_cars_json_v2", array.toString()).apply()
    }
}
