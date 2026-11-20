package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZephyrCardBorder
import com.example.ui.theme.ZephyrCharcoal
import com.example.ui.theme.ZephyrTaupe

@Composable
fun ZephyrQuickActions(
    onEarnMoneyClick: () -> Unit,
    onBusinessesClick: () -> Unit,
    onInvestmentsClick: () -> Unit,
    onBuyAssetsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = ZephyrCharcoal
            ),
            modifier = Modifier.padding(bottom = 14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(
                icon = Icons.Outlined.Add,
                label = "Earn Money",
                testTag = "quick_action_earn",
                onClick = onEarnMoneyClick
            )
            QuickActionButton(
                icon = Icons.Outlined.Domain,
                label = "Businesses",
                testTag = "quick_action_businesses",
                onClick = onBusinessesClick
            )
            QuickActionButton(
                icon = Icons.Outlined.ShowChart,
                label = "Investments",
                testTag = "quick_action_investments",
                onClick = onInvestmentsClick
            )
            QuickActionButton(
                icon = Icons.Outlined.Diamond,
                label = "Buy Assets",
                testTag = "quick_action_buy_assets",
                onClick = onBuyAssetsClick
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(elevation = 2.dp, shape = CircleShape, spotColor = Color(0x14000000))
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, ZephyrCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ZephyrCharcoal,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                color = ZephyrTaupe,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
