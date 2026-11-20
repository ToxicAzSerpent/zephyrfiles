package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LuxuryAsset
import com.example.ui.theme.*
import com.example.util.ImageStorageUtil

@Composable
fun EditLuxuryAssetDialog(
    asset: LuxuryAsset,
    onDismiss: () -> Unit,
    onSave: (LuxuryAsset) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(asset.name) }
    var priceStr by remember { mutableStateOf(asset.price.toString()) }
    var leaseStr by remember { mutableStateOf(asset.rentLeaseMonthly.toString()) }
    var currency by remember { mutableStateOf(asset.currency) }
    var imageUri by remember { mutableStateOf(asset.imageUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                val permanentPath = ImageStorageUtil.persistImageToInternalStorage(context, uri)
                imageUri = permanentPath
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Edit Luxury Item",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ZephyrCharcoal
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Photo picker preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZephyrIvoryBg)
                        .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    DynamicDealershipImage(
                        imageIdentifier = imageUri,
                        contentDescription = asset.name,
                        modifier = Modifier.fillMaxSize()
                    )

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Add / Change Photo",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Purchase Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = leaseStr,
                        onValueChange = { leaseStr = it },
                        label = { Text("Monthly Lease") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Currency Symbol") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: asset.price
                    val l = leaseStr.toDoubleOrNull() ?: asset.rentLeaseMonthly
                    onSave(
                        asset.copy(
                            name = name,
                            price = p,
                            rentLeaseMonthly = l,
                            currency = currency,
                            imageUri = imageUri
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ZephyrTaupe)
            }
        }
    )
}
