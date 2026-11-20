package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InGameCalendar
import com.example.model.OfflineEarningsReport
import com.example.model.PlayerProfile
import com.example.model.TycoonTransaction
import com.example.ui.components.DynamicDealershipImage
import com.example.ui.components.ZephyrQuickActions
import com.example.ui.components.ZephyrTransactionRow
import com.example.ui.components.ZephyrWealthCard
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun HomeScreen(
    profile: PlayerProfile,
    calendar: InGameCalendar,
    offlineReport: OfflineEarningsReport?,
    transactions: List<TycoonTransaction>,
    unreadNotificationsCount: Int,
    onDismissOfflineReport: () -> Unit,
    onClickToEarn: () -> Unit,
    onToggleBalanceVisibility: () -> Unit,
    onEarnMoneyClick: () -> Unit,
    onBusinessesClick: () -> Unit,
    onInvestmentsClick: () -> Unit,
    onBuyAssetsClick: () -> Unit,
    onOpenDealership: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onTransactionClick: (TycoonTransaction) -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
            .padding(horizontal = 22.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 28.dp)
    ) {
        // 1. Top Header: Greeting, Name, Calendar (Day & Year), & Notification Icon
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // In-Game Day and Year Pill (Requirement 9: 1 day = 25s, 1 year = 365 days)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEBE6DC))
                            .border(1.dp, Color(0xFFDDD7CC), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("in_game_calendar_badge")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = ZephyrCharcoal,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Year ${calendar.year} · Day ${calendar.dayOfYear} / 365",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ZephyrCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Progress bar inside day (25s cycle)
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFD4CDC0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = (calendar.secondsIntoDay / 25f).coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(ZephyrChampagneDark)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Good Morning,",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = ZephyrTaupe,
                            fontWeight = FontWeight.Normal
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = PlayfairFontFamily,
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            color = ZephyrCharcoal,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.testTag("player_greeting_name")
                    )
                }

                // Bell Icon with unread badge
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onNotificationClick() }
                        .testTag("notification_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = ZephyrCharcoal,
                        modifier = Modifier.size(24.dp)
                    )

                    if (unreadNotificationsCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-6).dp, y = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ZephyrChampagne)
                        )
                    }
                }
            }
        }

        // 2. Large Total Wealth / Balance Card (Matching left phone)
        item {
            ZephyrWealthCard(
                balance = profile.cashBalance,
                growthPercent = profile.monthlyGrowthPercent,
                isBalanceVisible = profile.isBalanceVisible,
                onToggleVisibility = onToggleBalanceVisibility,
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }

        // 3. Requirement 7 & 8: Click to Earn $100 button directly below the credit card with tax applied!
        item {
            ClickToEarnButton(
                onClick = onClickToEarn,
                modifier = Modifier.padding(bottom = 22.dp)
            )
        }

        // 4. Quick Actions (Matching left phone)
        item {
            ZephyrQuickActions(
                onEarnMoneyClick = onEarnMoneyClick,
                onBusinessesClick = onBusinessesClick,
                onInvestmentsClick = onInvestmentsClick,
                onBuyAssetsClick = onBuyAssetsClick,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        // Dealership Banner matching Luxeon Auto
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, ZephyrCardBorder, RoundedCornerShape(20.dp))
                    .clickable { onOpenDealership() }
                    .padding(16.dp)
                    .testTag("home_dealership_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1EDE6))
                    ) {
                        DynamicDealershipImage(
                            imageIdentifier = "img_hero_suv",
                            contentDescription = "Dealership",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LUXEON AUTO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    color = ZephyrCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFEBE6DC))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("DEALERSHIP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ZephyrCharcoal)
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Browse Motorcars, Leases & Sales",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = ZephyrTaupe
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Open Dealership",
                        tint = ZephyrCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 5. Recent Activity Header & Transactions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = ZephyrCharcoal
                    )
                )

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ZephyrTaupe,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .clickable { onViewAllTransactions() }
                        .testTag("view_all_transactions_btn")
                )
            }
        }

        // Transactions list
        items(transactions.take(5), key = { it.id }) { tx ->
            ZephyrTransactionRow(
                transaction = tx,
                onClick = { onTransactionClick(tx) }
            )
            HorizontalDivider(
                color = ZephyrCardBorder.copy(alpha = 0.5f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 54.dp)
            )
        }
    }

    // Offline Earnings Dialog (Requirement 6: Background progression notification)
    offlineReport?.let { report ->
        AlertDialog(
            onDismissRequest = onDismissOfflineReport,
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = ZephyrPositiveGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Welcome Back, ${profile.name}!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = ZephyrCharcoal
                    )
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "While you were away for ${report.elapsedSeconds} seconds (${report.inGameDaysPassed} in-game days passed), your business empire and investments generated steady passive revenue:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = ZephyrTaupe
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF7F5F0))
                            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Gross Revenue:", fontSize = 12.sp, color = ZephyrTaupe)
                                Text(
                                    text = "+$" + String.format(Locale.US, "%,.2f", report.grossIncome),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZephyrCharcoal
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tax Deducted (15%):", fontSize = 12.sp, color = ZephyrNegativeRed)
                                Text(
                                    text = "-$" + String.format(Locale.US, "%,.2f", report.taxPaid),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZephyrNegativeRed
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = ZephyrCardBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Net Liquid Deposited:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZephyrCharcoal)
                                Text(
                                    text = "+$" + String.format(Locale.US, "%,.2f", report.netIncome),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZephyrPositiveGreen
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissOfflineReport,
                    colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Collect & Continue", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

/**
 * Tactile "Click to Earn" Button placed directly below the credit card.
 * Requirement 7: $100 per click.
 * Requirement 8: Tax applied (15% tax: -$15, Net +$85).
 */
@Composable
private fun ClickToEarnButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1.0f, label = "click_scale")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1C1B1A))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .testTag("click_to_earn_button")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333130)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ElectricBolt,
                        contentDescription = "Earn",
                        tint = ZephyrChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Tap to Earn $100",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Gross: $100 · Tax: -$15 (15%) · Net: +$85",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFC7BAA7),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ZephyrChampagneDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "+$85",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
