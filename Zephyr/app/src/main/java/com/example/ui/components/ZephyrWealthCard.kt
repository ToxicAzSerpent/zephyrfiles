package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ZephyrWealthCard(
    balance: Double,
    growthPercent: Double,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val formattedBalance = if (isBalanceVisible) {
        currencyFormatter.format(balance)
    } else {
        "••••••••"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(24.dp))
            .testTag("wealth_card")
    ) {
        // Background Silk Waves Texture
        Image(
            painter = painterResource(id = R.drawable.bg_gold_card),
            contentDescription = "Silk gold texture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay to guarantee perfect high-contrast luxury readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x331C1B1A),
                            Color(0x550F0E0D)
                        )
                    )
                )
        )

        // Card Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: "Total Balance" & Eye Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFAF7F2).copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onToggleVisibility() }
                        .testTag("toggle_balance_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Toggle Balance Visibility",
                        tint = Color(0xFFFAF7F2).copy(alpha = 0.75f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Middle: Big Luxury Editorial Serif Balance
            Text(
                text = formattedBalance,
                fontFamily = PlayfairFontFamily,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.testTag("total_balance_text")
            )

            // Bottom: Monthly Growth Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x3D1C1B1A))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "▲",
                    fontSize = 8.sp,
                    color = Color(0xFF86E39A),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", growthPercent)}% this month",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFFAF7F2),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
