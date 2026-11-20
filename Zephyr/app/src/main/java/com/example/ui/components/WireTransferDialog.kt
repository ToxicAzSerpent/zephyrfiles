package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun WireTransferDialog(
    cashBalance: Double,
    onDismiss: () -> Unit,
    onSend: (String, Double, String) -> Unit
) {
    var recipient by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val canSend = recipient.isNotBlank() && amount > 0 && amount <= cashBalance

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Initiate Wire Transfer",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "SWIFT / Fedwire sovereign liquidity dispatch",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Beneficiary / Entity") },
                    placeholder = { Text("e.g. Monaco Maritime Ltd") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wire_recipient_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Transfer Amount ($)") },
                    placeholder = { Text("e.g. 5000.00") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wire_amount_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Transaction Reference") },
                    placeholder = { Text("e.g. Escrow deposit") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wire_note_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Available Reserve: $" + String.format(Locale.US, "%,.2f", cashBalance),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = if (amount > cashBalance) ZephyrNegativeRed else ZephyrTaupe
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSend(recipient.trim(), amount, note.trim())
                    onDismiss()
                },
                enabled = canSend,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZephyrCharcoal,
                    disabledContainerColor = Color(0xFFE5E0D7)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("wire_confirm_btn")
            ) {
                Text("Execute Wire", color = Color.White, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ZephyrTaupe)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp)
    )
}
