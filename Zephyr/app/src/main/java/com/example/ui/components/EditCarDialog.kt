package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DealershipCar
import com.example.ui.theme.*

@Composable
fun EditCarDialog(
    car: DealershipCar?,
    onDismiss: () -> Unit,
    onSave: (DealershipCar) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val isCreatingNew = car == null

    var name by remember { mutableStateOf(car?.name ?: "Audi RS Q8 Performance") }
    var brand by remember { mutableStateOf(car?.brand ?: "Audi") }
    var year by remember { mutableStateOf(car?.year ?: "2024") }
    var engine by remember { mutableStateOf(car?.engine ?: "4.0L V8") }
    var power by remember { mutableStateOf(car?.power ?: "600 HP") }
    var mileage by remember { mutableStateOf(car?.mileage ?: "5,000 mi") }
    var buyPriceText by remember { mutableStateOf(car?.priceBuy?.toLong()?.toString() ?: "145000") }
    var leasePriceText by remember { mutableStateOf(car?.priceLeaseMonthly?.toLong()?.toString() ?: "2100") }
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedImageUri by remember { mutableStateOf(car?.imageUri ?: "img_bmw_x5") }
    var currency by remember { mutableStateOf(car?.currency ?: "$") }

    // Android zero-permission Photo Picker with permanent disk persistence
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                val permanentPath = com.example.util.ImageStorageUtil.persistImageToInternalStorage(context, uri)
                selectedImageUri = permanentPath
            }
        }
    )

    val presetImages = listOf(
        Pair("img_bmw_x5", "BMW X5"),
        Pair("img_mercedes_gle", "Mercedes GLE"),
        Pair("img_porsche_cayenne", "Porsche Cayenne"),
        Pair("img_hero_suv", "Luxeon SUV"),
        Pair("img_dealership_showroom", "Showroom")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCreatingNew) "Add Vehicle to Dealership" else "Edit Vehicle & Pricing",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ZephyrCharcoal
                    )
                )

                if (!isCreatingNew && onDelete != null) {
                    IconButton(
                        onClick = {
                            car?.id?.let { onDelete(it) }
                            onDismiss()
                        },
                        modifier = Modifier.testTag("delete_car_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Car",
                            tint = ZephyrNegativeRed
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Preview & Photo Action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, ZephyrCardBorder, RoundedCornerShape(14.dp))
                ) {
                    DynamicDealershipImage(
                        imageIdentifier = selectedImageUri,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay button to pick from device
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1C1B1A)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .height(34.dp)
                            .testTag("pick_device_photo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddPhotoAlternate,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Photo", color = Color.White, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset selector pills
                Text(
                    text = "Or choose gallery preset:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ZephyrTaupe)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presetImages) { (resId, label) ->
                        val isSelected = selectedImageUri == resId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ZephyrCharcoal else Color(0xFFF1EDE6))
                                .clickable { selectedImageUri = resId }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White else ZephyrCharcoal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Car Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Car Model & Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_car_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Brand
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand / Manufacturer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Specs: Year & Engine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = engine,
                        onValueChange = { engine = it },
                        label = { Text("Engine") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Specs: Power & Mileage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = power,
                        onValueChange = { power = it },
                        label = { Text("Power (HP)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = mileage,
                        onValueChange = { mileage = it },
                        label = { Text("Mileage") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Purchase Price
                OutlinedTextField(
                    value = buyPriceText,
                    onValueChange = { buyPriceText = it.filter { char -> char.isDigit() } },
                    label = { Text("Purchase Price ($currency)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_car_price_buy")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rent / Lease Price
                OutlinedTextField(
                    value = leasePriceText,
                    onValueChange = { leasePriceText = it.filter { char -> char.isDigit() } },
                    label = { Text("Rent / Lease Price per Month ($currency/mo)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_car_price_lease")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Currency toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Currency Symbol:",
                        style = MaterialTheme.typography.bodySmall.copy(color = ZephyrTaupe)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("₽", "$", "€").forEach { curr ->
                            val isSelected = currency == curr
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ZephyrCharcoal else Color(0xFFF1EDE6))
                                    .clickable { currency = curr }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = curr,
                                    color = if (isSelected) Color.White else ZephyrCharcoal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalBuy = buyPriceText.toDoubleOrNull() ?: 9850000.0
                    val finalLease = leasePriceText.toDoubleOrNull() ?: 145000.0
                    val updated = (car ?: DealershipCar(
                        id = "car_${System.currentTimeMillis()}",
                        name = name,
                        brand = brand,
                        imageUri = selectedImageUri
                    )).copy(
                        name = name.ifBlank { "Exclusive Luxury Vehicle" },
                        brand = brand.ifBlank { "Prestige" },
                        year = year.ifBlank { "2024" },
                        engine = engine.ifBlank { "3.0L Turbo" },
                        power = power.ifBlank { "360 HP" },
                        mileage = mileage.ifBlank { "0 mi" },
                        priceBuy = finalBuy,
                        priceLeaseMonthly = finalLease,
                        currency = currency,
                        imageUri = selectedImageUri
                    )
                    onSave(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_car_btn")
            ) {
                Text("Save Changes", color = Color.White)
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
