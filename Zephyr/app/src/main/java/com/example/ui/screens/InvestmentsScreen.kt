package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InvestmentAsset
import com.example.model.Timeframe
import com.example.ui.components.ZephyrAssetRow
import com.example.ui.components.ZephyrGraph
import com.example.ui.components.ZephyrTimeframeSelector
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun InvestmentsScreen(
    portfolioValue: Double,
    timeframe: Timeframe,
    investments: List<InvestmentAsset>,
    selectedStock: InvestmentAsset?,
    onSelectStock: (InvestmentAsset?) -> Unit,
    onTimeframeSelected: (Timeframe) -> Unit,
    onBuyShares: (InvestmentAsset, Double) -> Unit,
    onSellShares: (InvestmentAsset, Double) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scrubbedValue by remember { mutableStateOf<Float?>(null) }

    // Displayed value depends on whether a stock is selected
    val displayValue = if (selectedStock != null) {
        scrubbedValue?.toDouble() ?: selectedStock.currentPrice
    } else {
        scrubbedValue?.toDouble() ?: portfolioValue
    }

    val formattedValue = "$" + String.format(Locale.US, "%,.2f", displayValue)
    val ownedInvestments = investments.filter { it.sharesOwned > 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // 1. Top Bar: "Investments" or Active Stock Name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedStock != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${selectedStock.name} (${selectedStock.symbol})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        ),
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEDE9E2))
                        .clickable { onSelectStock(null) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("clear_stock_selection_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Clear",
                            tint = ZephyrCharcoal,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Portfolio", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ZephyrCharcoal)
                    }
                }
            } else {
                Text(
                    text = "Investments & Portfolio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = ZephyrCharcoal
                    )
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onMenuClick() }
                        .testTag("investments_menu_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Options",
                        tint = ZephyrCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Total Portfolio Value or Selected Stock Share Price
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (selectedStock != null) "Current Market Price" else "Total Portfolio Value",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = ZephyrTaupe
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formattedValue,
                fontFamily = PlayfairFontFamily,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Normal,
                color = ZephyrCharcoal,
                modifier = Modifier.testTag("portfolio_total_value_text")
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (selectedStock != null) {
                val isPos = selectedStock.changePercent >= 0
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isPos) "▲ +" else "▼ ",
                        fontSize = 10.sp,
                        color = if (isPos) ZephyrPositiveGreen else ZephyrNegativeRed
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.2f", selectedStock.changePercent)}% live trading session",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isPos) ZephyrPositiveGreen else ZephyrNegativeRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (selectedStock.sharesOwned > 0) {
                        Text(
                            text = " · Owned: ${selectedStock.sharesOwned.toInt()} shares",
                            style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "▲",
                        fontSize = 9.sp,
                        color = ZephyrPositiveGreen,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "+7.2% overall portfolio growth this month",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ZephyrTaupe,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Line/Area Graph (STAYS VISIBLE AT TOP WHILE SCROLLING)
        ZephyrGraph(
            timeframe = timeframe,
            customPoints = selectedStock?.sparkline,
            onPointSelected = { pt -> scrubbedValue = pt },
            modifier = Modifier.height(140.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Timeframe Selectors
        ZephyrTimeframeSelector(
            selectedTimeframe = timeframe,
            onTimeframeSelected = onTimeframeSelected,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // 5. Explore Investments Button (Buys shares)
        val targetStock = selectedStock ?: investments.firstOrNull()
        if (targetStock != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onBuyShares(targetStock, 1.0)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1C1B1A),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("buy_shares_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Buy 1 Share • $${String.format(Locale.US, "%,.2f", targetStock.currentPrice)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (targetStock.sharesOwned > 0) {
                    OutlinedButton(
                        onClick = {
                            onSellShares(targetStock, 1.0)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("sell_shares_button")
                    ) {
                        Text("Sell 1", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZephyrCharcoal)
                    }
                }
            }
        }

        HorizontalDivider(color = ZephyrCardBorder, thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

        // 6. SCROLLABLE AREA FOR STOCKS AND HOLDINGS (Graph remains visible above!)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Market Securities & Equities (${investments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Text(
                        text = "Tap to view trend",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ZephyrTaupe,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            items(investments, key = { it.id }) { asset ->
                val isSelected = selectedStock?.id == asset.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFFEBE6DC) else Color.Transparent)
                ) {
                    ZephyrAssetRow(
                        asset = asset,
                        onClick = {
                            onSelectStock(if (isSelected) null else asset)
                        }
                    )
                }
                HorizontalDivider(
                    color = ZephyrCardBorder.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 54.dp)
                )
            }

            // 7. Section: "Your Active Holdings"
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Your Purchased Holdings (${ownedInvestments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ZephyrCharcoal
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (ownedInvestments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No active stock holdings yet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZephyrCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Select any stock above and tap 'Buy Shares' to start generating portfolio returns.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ZephyrTaupe,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            } else {
                items(ownedInvestments, key = { "holding_${it.id}" }) { holding ->
                    HoldingCard(
                        holding = holding,
                        onBuyMore = { onBuyShares(holding, 1.0) },
                        onSell = { onSellShares(holding, 1.0) },
                        onInspect = { onSelectStock(holding) },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HoldingCard(
    holding: InvestmentAsset,
    onBuyMore: () -> Unit,
    onSell: () -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isProfit = holding.profitLoss >= 0
    val profitDollarStr = (if (isProfit) "+$" else "-$") + String.format(Locale.US, "%,.2f", kotlin.math.abs(holding.profitLoss))
    val profitPercentStr = (if (isProfit) "+" else "") + String.format(Locale.US, "%,.2f", holding.profitLossPercent) + "%"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
            .clickable { onInspect() }
            .padding(14.dp)
            .testTag("holding_card_${holding.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = holding.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Text(
                        text = "${holding.symbol} · ${holding.sharesOwned.toInt()} Shares Owned",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = ZephyrTaupe
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$" + String.format(Locale.US, "%,.2f", holding.totalValue),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Text(
                        text = "$profitDollarStr ($profitPercentStr)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isProfit) ZephyrPositiveGreen else ZephyrNegativeRed
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1EDE6), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Avg Buy", fontSize = 10.sp, color = ZephyrTaupe)
                    Text(
                        text = "$" + String.format(Locale.US, "%.2f", holding.avgBuyPrice),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                }

                Column {
                    Text("Current", fontSize = 10.sp, color = ZephyrTaupe)
                    Text(
                        text = "$" + String.format(Locale.US, "%.2f", holding.currentPrice),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                }

                Column {
                    Text("Invested", fontSize = 10.sp, color = ZephyrTaupe)
                    Text(
                        text = "$" + String.format(Locale.US, "%,.2f", holding.totalInvested),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onBuyMore,
                        colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("+ Buy", fontSize = 10.sp)
                    }

                    OutlinedButton(
                        onClick = onSell,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Sell", fontSize = 10.sp, color = ZephyrCharcoal)
                    }
                }
            }
        }
    }
}
