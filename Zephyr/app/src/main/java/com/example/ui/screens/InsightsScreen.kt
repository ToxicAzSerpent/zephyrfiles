package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BusinessEnterprise
import com.example.model.InvestmentAsset
import com.example.model.LuxuryAsset
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun InsightsScreen(
    cashBalance: Double,
    portfolioValue: Double,
    businesses: List<BusinessEnterprise>,
    luxuryAssets: List<LuxuryAsset>,
    totalNetWorth: Double,
    modifier: Modifier = Modifier
) {
    val luxuryValue = luxuryAssets.filter { it.isOwned }.sumOf { it.price }
    val businessValue = businesses.sumOf { it.currentRevenuePerHour * 10.0 }

    val safeNetWorth = totalNetWorth.coerceAtLeast(1.0)
    val cashRatio = (cashBalance / safeNetWorth).toFloat().coerceIn(0f, 1f)
    val portRatio = (portfolioValue / safeNetWorth).toFloat().coerceIn(0f, 1f)
    val luxRatio = (luxuryValue / safeNetWorth).toFloat().coerceIn(0f, 1f)
    val bizRatio = (businessValue / safeNetWorth).toFloat().coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
            .padding(horizontal = 22.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 32.dp)
    ) {
        // Title
        item {
            Column(modifier = Modifier.padding(bottom = 18.dp)) {
                Text(
                    text = "Wealth Insights",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Capital allocation & portfolio run-rate analytics",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        }

        // Net Worth Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.dp, ZephyrCardBorder, RoundedCornerShape(22.dp))
                    .padding(22.dp)
                    .testTag("net_worth_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Consolidated Net Worth",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = ZephyrTaupe
                            )
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ZephyrChampagneLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Tier: Sovereign Mogul",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZephyrChampagneDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$" + String.format(Locale.US, "%,.2f", totalNetWorth),
                        fontFamily = PlayfairFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color = ZephyrCharcoal
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Multi-segment allocation progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        if (cashRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(cashRatio)
                                    .fillMaxHeight()
                                    .background(ZephyrCharcoal)
                            )
                        }
                        if (portRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(portRatio)
                                    .fillMaxHeight()
                                    .background(ZephyrChampagne)
                            )
                        }
                        if (luxRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(luxRatio)
                                    .fillMaxHeight()
                                    .background(Color(0xFF9E8569))
                            )
                        }
                        if (bizRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(bizRatio)
                                    .fillMaxHeight()
                                    .background(Color(0xFF4A4642))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legend
                    AllocationItem("Liquid Vault Cash", cashBalance, cashRatio * 100, ZephyrCharcoal)
                    Spacer(modifier = Modifier.height(8.dp))
                    AllocationItem("Securities & Equities", portfolioValue, portRatio * 100, ZephyrChampagne)
                    Spacer(modifier = Modifier.height(8.dp))
                    AllocationItem("Trophy Assets & Real Estate", luxuryValue, luxRatio * 100, Color(0xFF9E8569))
                    Spacer(modifier = Modifier.height(8.dp))
                    AllocationItem("Operating Enterprises", businessValue, bizRatio * 100, Color(0xFF4A4642))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Projections Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, ZephyrCardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Yield Projections",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val hourly = businesses.filter { it.isAutomated }.sumOf { it.currentRevenuePerHour }
                    val daily = hourly * 24
                    val monthly = daily * 30

                    ProjectionRow("24-Hour Projected Yield", "+$" + String.format(Locale.US, "%,.2f", daily))
                    HorizontalDivider(color = ZephyrCardBorder, modifier = Modifier.padding(vertical = 8.dp))
                    ProjectionRow("30-Day Estimated Return", "+$" + String.format(Locale.US, "%,.2f", monthly))
                    HorizontalDivider(color = ZephyrCardBorder, modifier = Modifier.padding(vertical = 8.dp))
                    ProjectionRow("Simulated Inflation Hedge", "99.4% Capital Preserved")
                }
            }
        }
    }
}

@Composable
private fun AllocationItem(label: String, amount: Double, percent: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = ZephyrTaupe
                )
            )
        }

        Text(
            text = "$" + String.format(Locale.US, "%,.0f", amount) + " (${String.format(Locale.US, "%.1f", percent)}%)",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ZephyrCharcoal
            )
        )
    }
}

@Composable
private fun ProjectionRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = ZephyrTaupe
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZephyrPositiveGreen
            )
        )
    }
}
