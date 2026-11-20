package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DealershipCar
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun CarDetailDialog(
    car: DealershipCar,
    onDismiss: () -> Unit,
    onBuy: () -> Unit,
    onLease: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("BUY") } // BUY or LEASE
    var leaseTermMonths by remember { mutableStateOf(24) }
    var actionDoneMessage by remember { mutableStateOf<String?>(null) }

    val monthlyCost = when (leaseTermMonths) {
        12 -> car.priceLeaseMonthly * 1.15
        24 -> car.priceLeaseMonthly
        else -> car.priceLeaseMonthly * 0.90
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = car.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ZephyrCharcoal,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "${car.brand} · ${car.year} · ${car.mileage}",
                    style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 12.sp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Vehicle Photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
                ) {
                    DynamicDealershipImage(
                        imageIdentifier = car.imageUri,
                        contentDescription = car.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (car.isBought || car.isLeased) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ZephyrPositivePillBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (car.isBought) "In Your Collection" else "Active Executive Lease",
                                color = ZephyrPositiveGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Specs Grid: Year | Engine | Power | Mileage
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F5F0))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SpecItem(title = "Year", value = car.year)
                    SpecItem(title = "Engine", value = car.engine)
                    SpecItem(title = "Power", value = car.power)
                    SpecItem(title = "Mileage", value = car.mileage)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Switcher: Purchase / Lease & Rent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFECE6))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == "BUY") ZephyrCharcoal else Color.Transparent)
                            .clickable { selectedTab = "BUY" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Purchase",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTab == "BUY") Color.White else ZephyrTaupe
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == "LEASE") ZephyrCharcoal else Color.Transparent)
                            .clickable { selectedTab = "LEASE" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lease & Rent",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTab == "LEASE") Color.White else ZephyrTaupe
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == "BUY") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZephyrSurfaceSubtle)
                            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            val basePrice = car.priceBuy
                            val luxuryTax = basePrice * 0.08
                            val totalWithTax = basePrice + luxuryTax
                            Text(
                                text = "Acquisition Breakdown:",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.US, "$%,.0f", totalWithTax),
                                fontFamily = PlayfairFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZephyrCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Subtotal: ${String.format(Locale.US, "$%,.0f", basePrice)} · Tax (8%): ${String.format(Locale.US, "$%,.0f", luxuryTax)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "✓ Includes white-glove transport, title registration, 2-year warranty, and ceramic protection.",
                                style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 10.sp, lineHeight = 14.sp)
                            )
                        }
                    }
                } else {
                    Column {
                        // Term chips
                        Text(
                            text = "Lease Term Duration:",
                            style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(12, 24, 36).forEach { term ->
                                val isSelected = leaseTermMonths == term
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) ZephyrCharcoal else Color(0xFFF1EDE6))
                                        .clickable { leaseTermMonths = term }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$term months",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White else ZephyrCharcoal,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ZephyrSurfaceSubtle)
                                .border(1.dp, ZephyrCardBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Installment:",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (car.currency == "$") {
                                        String.format(Locale.US, "$%,.0f / mo", monthlyCost)
                                    } else {
                                        String.format(Locale.US, "%,.0f %s / mo", monthlyCost, car.currency)
                                    },
                                    fontFamily = PlayfairFontFamily,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZephyrCharcoal
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "✓ Institutional underwriting via Prestige Leasing and Global Lease Group.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }

                actionDoneMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ZephyrPositivePillBg)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = msg,
                            color = ZephyrPositiveGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTab == "BUY") {
                        onBuy()
                        actionDoneMessage = "✓ Purchase order successfully confirmed!"
                    } else {
                        onLease()
                        actionDoneMessage = "✓ Lease agreement ($leaseTermMonths mos) activated!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("modal_action_btn")
            ) {
                Icon(
                    imageVector = if (selectedTab == "BUY") Icons.Outlined.ShoppingBag else Icons.Outlined.Key,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (selectedTab == "BUY") "Purchase Vehicle" else "Initiate Lease",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ZephyrTaupe)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
private fun SpecItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ZephyrCharcoal
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = ZephyrTaupe
            )
        )
    }
}
