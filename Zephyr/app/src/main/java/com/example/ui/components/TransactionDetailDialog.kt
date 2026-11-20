package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.TycoonTransaction
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun TransactionDetailDialog(
    transaction: TycoonTransaction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Executive Receipt",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Transaction ID: #${transaction.id.takeLast(8).uppercase()}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZephyrSurfaceSubtle)
                        .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = (if (transaction.isIncome) "+$" else "-$") +
                                    String.format(Locale.US, "%,.2f", transaction.amount),
                            fontFamily = PlayfairFontFamily,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (transaction.isIncome) ZephyrPositiveGreen else ZephyrCharcoal
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = transaction.title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = ZephyrCharcoal
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZephyrCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        ReceiptField("Category", transaction.category)
                        ReceiptField("Date & Time", transaction.timestamp)
                        ReceiptField("Status", "Settled · Instant Clearing")
                        if (transaction.taxDeducted > 0) {
                            ReceiptField("Tax Applied", "-$" + String.format(Locale.US, "%,.2f", transaction.taxDeducted))
                        }
                        if (transaction.note.isNotBlank()) {
                            ReceiptField("Memo", transaction.note)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_receipt_btn")
            ) {
                Text("Done", color = ZephyrCharcoal)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
private fun ReceiptField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe, fontSize = 11.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = ZephyrCharcoal,
                fontSize = 11.sp
            )
        )
    }
}
