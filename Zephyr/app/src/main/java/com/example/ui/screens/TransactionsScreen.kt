package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Send
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
import com.example.model.TycoonTransaction
import com.example.ui.components.ZephyrTransactionRow
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun TransactionsScreen(
    cashBalance: Double,
    transactions: List<TycoonTransaction>,
    onSendWireClick: () -> Unit,
    onTransactionClick: (TycoonTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Income", "Expenses", "Securities", "Transfers")

    val filteredTransactions = remember(selectedFilter, transactions) {
        when (selectedFilter) {
            "Income" -> transactions.filter { it.isIncome }
            "Expenses" -> transactions.filter { !it.isIncome }
            "Securities" -> transactions.filter { it.category == "Securities" || it.category == "Dividends" }
            "Transfers" -> transactions.filter { it.category == "Transfers" }
            else -> transactions
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
            .padding(horizontal = 22.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 32.dp)
    ) {
        // Title
        item {
            Column(modifier = Modifier.padding(bottom = 18.dp)) {
                Text(
                    text = "Activity & Transfers",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Verified financial ledger & wire disbursements",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        }

        // Vault Balance & Wire Transfer card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Liquid Vault Reserve",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$" + String.format(Locale.US, "%,.2f", cashBalance),
                                fontFamily = PlayfairFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZephyrCharcoal
                            )
                        }

                        Button(
                            onClick = onSendWireClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ZephyrCharcoal
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("initiate_wire_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = "Transfer",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Send Wire",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(filters) { f ->
                    val isSelected = f == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = f },
                        label = {
                            Text(
                                text = f,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZephyrCharcoal,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = ZephyrTaupe
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ZephyrCharcoal else ZephyrCardBorder
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        }

        // Transactions list
        items(filteredTransactions, key = { it.id }) { tx ->
            ZephyrTransactionRow(
                transaction = tx,
                onClick = { onTransactionClick(tx) }
            )
            HorizontalDivider(
                color = ZephyrCardBorder.copy(alpha = 0.6f),
                thickness = 0.6.dp,
                modifier = Modifier.padding(start = 58.dp)
            )
        }
    }
}
