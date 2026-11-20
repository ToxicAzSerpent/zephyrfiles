package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Upgrade
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
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun BusinessesScreen(
    businesses: List<BusinessEnterprise>,
    cashBalance: Double,
    passiveIncomePerHour: Double,
    onBack: () -> Unit,
    onUpgrade: (String) -> Unit,
    onHireManager: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
            .padding(horizontal = 22.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 32.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("businesses_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ZephyrCharcoal
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "Enterprises & Ventures",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Text(
                        text = "Global operations generating passive yield",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = ZephyrTaupe
                        )
                    )
                }
            }
        }

        // Run-rate summary card
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
                        text = "Automated Hourly Run-Rate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = ZephyrTaupe
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+$" + String.format(Locale.US, "%,.2f", passiveIncomePerHour) + " / hr",
                        fontFamily = PlayfairFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZephyrCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Available Cash: $" + String.format(Locale.US, "%,.2f", cashBalance),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ZephyrChampagneDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // List of enterprises
        items(businesses, key = { it.id }) { biz ->
            BusinessCard(
                biz = biz,
                canAffordUpgrade = cashBalance >= biz.upgradeCost,
                canAffordManager = cashBalance >= biz.managerCost && !biz.isAutomated,
                onUpgrade = { onUpgrade(biz.id) },
                onHireManager = { onHireManager(biz.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BusinessCard(
    biz: BusinessEnterprise,
    canAffordUpgrade: Boolean,
    canAffordManager: Boolean,
    onUpgrade: () -> Unit,
    onHireManager: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
            .testTag("business_card_${biz.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = biz.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${biz.category} · Tier ${biz.level}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = ZephyrTaupe
                        )
                    )
                }

                // Automation status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (biz.isAutomated) ZephyrPositivePillBg else Color(0xFFF2EFE8))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (biz.isAutomated) "Automated CEO" else "Manual",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (biz.isAutomated) ZephyrPositiveGreen else ZephyrTaupe
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = biz.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = ZephyrTaupe
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Output rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yield: +$" + String.format(Locale.US, "%,.2f", biz.currentRevenuePerHour) + "/hr",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ZephyrCharcoal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Upgrade Button
                Button(
                    onClick = onUpgrade,
                    enabled = canAffordUpgrade,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZephyrCharcoal,
                        disabledContainerColor = Color(0xFFE2DED6)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("upgrade_btn_${biz.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Upgrade,
                        contentDescription = "Upgrade",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Upgrade ($" + String.format(Locale.US, "%,.0f", biz.upgradeCost) + ")",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Hire Manager / Automated
                if (!biz.isAutomated) {
                    OutlinedButton(
                        onClick = onHireManager,
                        enabled = canAffordManager,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("manager_btn_${biz.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = "Hire CEO",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hire CEO ($" + String.format(Locale.US, "%,.0f", biz.managerCost) + ")",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
