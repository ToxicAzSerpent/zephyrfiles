package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun ZephyrAssetRow(
    asset: InvestmentAsset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = asset.changePercent >= 0
    val changeText = (if (isPositive) "▲ " else "▼ ") +
            String.format(Locale.US, "%.2f", Math.abs(asset.changePercent)) + "%"
    val changeColor = if (isPositive) ZephyrPositiveGreen else ZephyrNegativeRed

    // Custom distinct high-end luxury monograms and colors for every stock
    val (badgeBg, monogramText, monogramColor) = when (asset.id) {
        "aapl" -> Triple(Color(0xFF000000), "", Color(0xFFFFFFFF))
        "tsla" -> Triple(Color(0xFFE82127), "T", Color(0xFFFFFFFF))
        "goog" -> Triple(Color(0xFFF1F3F4), "G", Color(0xFF4285F4))
        "msft" -> Triple(Color(0xFF00A4EF), "⊞", Color(0xFFFFFFFF))
        "amzn" -> Triple(Color(0xFFFF9900), "a", Color(0xFF000000))
        "nvda" -> Triple(Color(0xFF76B900), "N", Color(0xFF000000))
        "meta" -> Triple(Color(0xFF0081FB), "M", Color(0xFFFFFFFF))
        "brkb" -> Triple(Color(0xFF003366), "B", Color(0xFFFFFFFF))
        "jnj" -> Triple(Color(0xFFD51900), "J", Color(0xFFFFFFFF))
        "lly" -> Triple(Color(0xFF002D62), "L", Color(0xFFFFFFFF))
        "jpm" -> Triple(Color(0xFF1C39BB), "JPM", Color(0xFFFFFFFF))
        "v" -> Triple(Color(0xFF1A1F71), "V", Color(0xFFF7B600))
        "cvx" -> Triple(Color(0xFF002C6C), "CVX", Color(0xFFFFFFFF))
        "xom" -> Triple(Color(0xFFED1B2D), "XOM", Color(0xFFFFFFFF))
        "sony" -> Triple(Color(0xFF000000), "SONY", Color(0xFFFFFFFF))
        "ntdoy" -> Triple(Color(0xFFE60012), "NT", Color(0xFFFFFFFF))
        "asml" -> Triple(Color(0xFF003399), "ASML", Color(0xFFFFFFFF))
        "arm" -> Triple(Color(0xFF0093DD), "ARM", Color(0xFFFFFFFF))
        "lmt" -> Triple(Color(0xFF003366), "LMT", Color(0xFFFFFFFF))
        "rolls" -> Triple(Color(0xFF5D2E8C), "RR", Color(0xFFFFFFFF))
        "zephyr_capital" -> Triple(Color(0xFF1C1B1A), "Z", ZephyrChampagne)
        "gold" -> Triple(Color(0xFFFFD700), "Au", Color(0xFF1C1B1A))
        "silver" -> Triple(Color(0xFFC0C0C0), "Ag", Color(0xFF1C1B1A))
        "dubai_re" -> Triple(Color(0xFFB8860B), "D", Color(0xFFFFFFFF))
        "manhattan_re" -> Triple(Color(0xFF333333), "M", Color(0xFFFFFFFF))
        "swiss_bank" -> Triple(Color(0xFFDA291C), "GPBG", Color(0xFFFFFFFF))
        else -> Triple(Color(0xFFEFECE4), "◆", ZephyrCharcoal)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 11.dp, horizontal = 4.dp)
            .testTag("asset_row_${asset.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Monogram / Logo badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(badgeBg)
                .border(0.5.dp, Color(0x1A000000), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = monogramText,
                fontSize = if (monogramText.length > 2) 11.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = monogramColor
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Name & Symbol/Shares
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = ZephyrCharcoal
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = asset.symbol,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = ZephyrTaupe
                )
            )
        }

        // Value & Percentage
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$" + String.format(Locale.US, "%,.2f", asset.totalValue),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ZephyrCharcoal
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = changeText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = changeColor
                )
            )
        }
    }
}
