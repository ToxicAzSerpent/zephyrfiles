package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
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
import com.example.model.DealershipSettings
import com.example.ui.components.DynamicDealershipImage
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun DealershipScreen(
    cars: List<DealershipCar>,
    settings: DealershipSettings,
    playerName: String,
    onBack: () -> Unit,
    onToggleEditMode: () -> Unit,
    onEditCar: (DealershipCar) -> Unit,
    onAddNewCar: () -> Unit,
    onCarClick: (DealershipCar) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onEditHeroImage: () -> Unit,
    onEditShowroomImage: () -> Unit,
    onEditPlayerName: () -> Unit,
    onRequestConsultation: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F2)),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // 1. Top Bar: Clean header with Back arrow, LUXEON AUTO logo, and Edit Mode toggle
        // (Navigation links and "Submit Request" button removed per requirement 3)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = ZephyrCharcoal
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LUXEON",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp,
                                color = ZephyrCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AUTO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Light,
                                fontSize = 18.sp,
                                letterSpacing = 1.5.sp,
                                color = ZephyrTaupe
                            )
                        )
                    }
                }

                // Quick Edit Mode Toggle button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (settings.isEditModeActive) ZephyrChampagneDark else Color(0xFFF0EDE6))
                        .clickable { onToggleEditMode() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("dealer_toggle_edit_mode_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Mode",
                            tint = if (settings.isEditModeActive) Color.White else ZephyrCharcoal,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (settings.isEditModeActive) "Edit ON" else "Edit Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.isEditModeActive) Color.White else ZephyrCharcoal
                        )
                    }
                }
            }
        }

        // Active Edit Mode Banner
        if (settings.isEditModeActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3D6))
                        .border(1.dp, Color(0xFFE8CA82))
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "✏️ Dealership Edit Mode Active",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B4B0A),
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Tap any car to edit photo, name, buy price, or lease rate. Photos stay permanently saved across app restarts.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = Color(0xFF856417)
                                )
                            )
                        }

                        TextButton(onClick = onEditPlayerName) {
                            Text("Edit Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZephyrCharcoal)
                        }
                    }
                }
            }
        }

        // 2. Showcase Hero SUV Image (Header text block removed per requirement 3)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, ZephyrCardBorder, RoundedCornerShape(20.dp))
                    .testTag("dealer_hero_image_box")
            ) {
                DynamicDealershipImage(
                    imageIdentifier = settings.heroImageUri,
                    contentDescription = "Luxeon Auto Flagship Showroom",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (settings.isEditModeActive) {
                    Button(
                        onClick = onEditHeroImage,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1C1B1A)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .height(36.dp)
                            .testTag("edit_hero_image_btn")
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Hero Photo", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // 3. Featured Motorcars Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Featured Motorcars",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Text(
                        text = "${cars.size} car slots",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = ZephyrTaupe
                        )
                    )
                }

                if (settings.isEditModeActive) {
                    Button(
                        onClick = onAddNewCar,
                        colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("increase_car_slots_btn")
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Increase Slots", fontSize = 11.sp)
                    }
                }
            }
        }

        // 4. List of Car Cards with persistent photos and clean English specs
        items(cars, key = { it.id }) { car ->
            DealershipCarCard(
                car = car,
                isEditMode = settings.isEditModeActive,
                onEdit = { onEditCar(car) },
                onClick = { onCarClick(car) },
                onToggleFavorite = { onToggleFavorite(car.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // 5. Requirement 2: "from the wording concierge services and excellence, remove everything below"
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1EDE6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VerifiedUser,
                            contentDescription = null,
                            tint = ZephyrCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Concierge Services & Excellence",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ZephyrCharcoal
                            )
                        )
                        Text(
                            text = "Every vehicle certified with multi-point provenance inspection.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = ZephyrTaupe
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DealershipCarCard(
    car: DealershipCar,
    isEditMode: Boolean,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("car_card_${car.id}")
    ) {
        Column {
            // Image Box + Heart + Edit Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFBF9F6))
                    .clickable { onClick() }
            ) {
                DynamicDealershipImage(
                    imageIdentifier = car.imageUri,
                    contentDescription = car.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite button (top right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (car.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (car.isFavorite) ZephyrNegativeRed else ZephyrTaupe,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // If Edit Mode is active, show prominent Edit Car Button overlay
                if (isEditMode) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .height(34.dp)
                            .testTag("edit_car_btn_${car.id}")
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Car, Pricing & Photo", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Car Title
            Text(
                text = car.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ZephyrCharcoal
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Row: Year | Engine | Power | Mileage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecBadge(car.year, "Year")
                SpecBadge(car.engine, "Engine")
                SpecBadge(car.power, "Power")
                SpecBadge(car.mileage, "Mileage")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Row: Price & Details button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = String.format(Locale.US, "$%,.0f", car.priceBuy),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = ZephyrCharcoal
                        )
                    )
                    Text(
                        text = "Lease: " + String.format(Locale.US, "$%,.0f", car.priceLeaseMonthly) + "/mo (+8% tax)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ZephyrTaupe,
                            fontSize = 10.sp
                        )
                    )
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Details", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecBadge(value: String, label: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = ZephyrCharcoal
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = ZephyrTaupe
            )
        )
    }
}
