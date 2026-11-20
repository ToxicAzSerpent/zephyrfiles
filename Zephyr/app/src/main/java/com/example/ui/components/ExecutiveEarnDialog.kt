package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.TrendingUp
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
import com.example.ui.theme.*
import java.util.Locale

data class DealOpportunity(
    val id: String,
    val title: String,
    val category: String,
    val reward: Double,
    val description: String
)

@Composable
fun ExecutiveEarnDialog(
    onDismiss: () -> Unit,
    onExecuteDeal: (Double, String) -> Unit
) {
    val deals = listOf(
        DealOpportunity(
            id = "deal_1",
            title = "M&A Strategic Advisory",
            category = "Private Syndicate",
            reward = 3500.0,
            description = "Advise European luxury consortium on high-value asset acquisition."
        ),
        DealOpportunity(
            id = "deal_2",
            title = "Zephyr Technology Licensing",
            category = "Intellectual Property",
            reward = 6200.0,
            description = "Disburse neural algorithm license to Swiss biotech enterprise."
        ),
        DealOpportunity(
            id = "deal_3",
            title = "Sovereign Bond Allocation",
            category = "Treasury Yield",
            reward = 1850.0,
            description = "Collect quarterly premium on institutional debt instruments."
        ),
        DealOpportunity(
            id = "deal_4",
            title = "Angel Investment Syndicate Call",
            category = "Venture Capital",
            reward = 8900.0,
            description = "Finalize Series B exit round for aerospace startup."
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Executive Deal Room",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Authorize contracts to generate immediate liquidity",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                deals.forEach { deal ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZephyrSurfaceSubtle)
                            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                onExecuteDeal(deal.reward, deal.title)
                                onDismiss()
                            }
                            .padding(14.dp)
                            .testTag("deal_item_${deal.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deal.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = ZephyrCharcoal
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = deal.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp,
                                        color = ZephyrTaupe
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "+$" + String.format(Locale.US, "%,.0f", deal.reward),
                                fontFamily = PlayfairFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZephyrPositiveGreen
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ZephyrTaupe)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp)
    )
}
