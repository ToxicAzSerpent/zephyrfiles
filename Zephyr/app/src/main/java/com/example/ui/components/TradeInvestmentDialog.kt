package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
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
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun TradeInvestmentDialog(
    asset: InvestmentAsset,
    cashBalance: Double,
    onDismiss: () -> Unit,
    onBuy: (Double) -> Unit,
    onSell: (Double) -> Unit
) {
    var sharesCount by remember { mutableStateOf(1.0) }
    var tradeMode by remember { mutableStateOf("BUY") } // BUY or SELL

    val totalCost = sharesCount * asset.currentPrice
    val canExecute = if (tradeMode == "BUY") cashBalance >= totalCost else asset.sharesOwned >= sharesCount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "${if (tradeMode == "BUY") "Acquire" else "Liquidate"} ${asset.name}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Current Price: $" + String.format(Locale.US, "%,.2f", asset.currentPrice) + " · Owned: ${asset.sharesOwned.toInt()} Shares",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Buy / Sell Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0EDE6))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tradeMode == "BUY") ZephyrCharcoal else Color.Transparent)
                            .clickable { tradeMode = "BUY" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Buy Shares",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (tradeMode == "BUY") Color.White else ZephyrTaupe
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tradeMode == "SELL") ZephyrCharcoal else Color.Transparent)
                            .clickable { tradeMode = "SELL" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sell Shares",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (tradeMode == "SELL") Color.White else ZephyrTaupe
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quantity Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ZephyrTaupe,
                            fontSize = 13.sp
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (sharesCount > 1) sharesCount -= 1 },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1EDE6))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Remove,
                                contentDescription = "Decrease",
                                modifier = Modifier.size(16.dp),
                                tint = ZephyrCharcoal
                            )
                        }

                        Text(
                            text = "${sharesCount.toInt()}",
                            fontFamily = PlayfairFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZephyrCharcoal,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = { sharesCount += 1 },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1EDE6))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Increase",
                                modifier = Modifier.size(16.dp),
                                tint = ZephyrCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Cost Breakdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ZephyrSurfaceSubtle)
                        .border(1.dp, ZephyrCardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (tradeMode == "BUY") "Total Capital Required" else "Estimated Proceeds",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
                            )
                            Text(
                                text = "$" + String.format(Locale.US, "%,.2f", totalCost),
                                fontFamily = PlayfairFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZephyrCharcoal
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Available Vault Balance",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
                            )
                            Text(
                                text = "$" + String.format(Locale.US, "%,.2f", cashBalance),
                                style = MaterialTheme.typography.labelSmall.copy(color = ZephyrChampagneDark, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tradeMode == "BUY") onBuy(sharesCount) else onSell(sharesCount)
                    onDismiss()
                },
                enabled = canExecute,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZephyrCharcoal,
                    disabledContainerColor = Color(0xFFE2DED6)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_trade_button")
            ) {
                Text(
                    text = if (tradeMode == "BUY") "Confirm Order" else "Liquidate Position",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ZephyrTaupe)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp)
    )
}
