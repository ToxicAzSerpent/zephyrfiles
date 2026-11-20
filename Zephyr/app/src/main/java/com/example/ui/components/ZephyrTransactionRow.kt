package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TycoonTransaction
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ZephyrTransactionRow(
    transaction: TycoonTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val formattedAmount = (if (transaction.isIncome) "+$" else "-$") +
            String.format(Locale.US, "%,.2f", transaction.amount)

    val iconVector: ImageVector = when {
        transaction.title.contains("Income", ignoreCase = true) || transaction.title.contains("Dribbble", ignoreCase = true) -> Icons.Outlined.WorkOutline
        transaction.title.contains("Coffee", ignoreCase = true) -> Icons.Outlined.LocalCafe
        transaction.title.contains("Spotify", ignoreCase = true) || transaction.title.contains("Subscription", ignoreCase = true) -> Icons.Outlined.GraphicEq
        transaction.title.contains("Dividend", ignoreCase = true) || transaction.title.contains("Holdings", ignoreCase = true) -> Icons.Outlined.AccountBalance
        transaction.title.contains("Acquired", ignoreCase = true) || transaction.title.contains("Buy", ignoreCase = true) -> Icons.Outlined.ShoppingBag
        transaction.title.contains("Transfer", ignoreCase = true) -> Icons.Outlined.SwapHoriz
        else -> Icons.Outlined.ReceiptLong
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("transaction_row_${transaction.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rounded Circle Icon Badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1EDE6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = transaction.category,
                tint = ZephyrCharcoal,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title & Date
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = ZephyrCharcoal
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = transaction.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = ZephyrTaupe
                )
            )
        }

        // Amount
        Text(
            text = formattedAmount,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (transaction.isIncome) ZephyrCharcoal else ZephyrCharcoal
            )
        )
    }
}
