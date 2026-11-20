package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import com.example.model.LuxuryAsset
import com.example.ui.components.DynamicDealershipImage
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun PropertyScreen(
    luxuryAssets: List<LuxuryAsset>,
    dealershipCars: List<DealershipCar>,
    onBack: () -> Unit,
    isEditModeActive: Boolean = false,
    onAddMansionSlot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Garage, 1: Hangar, 2: Islands & Boats, 3: All

    val ownedMansions = luxuryAssets.filter { it.category == "Mansions" && it.isOwned }
    val ownedCars = dealershipCars.filter { it.isBought }
    val ownedJets = luxuryAssets.filter { it.category == "Private Jets" && it.isOwned }
    val ownedHangars = luxuryAssets.filter { it.category == "Hangars" && it.isOwned }
    val ownedIslands = luxuryAssets.filter { it.category == "Islands" && it.isOwned }
    val ownedBoats = luxuryAssets.filter { it.category == "Yachts" && it.isOwned }
    val otherOwned = luxuryAssets.filter { it.isOwned && it.category !in listOf("Mansions", "Private Jets", "Hangars", "Islands", "Yachts") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEDE9E2))
                    .testTag("property_back_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = ZephyrCharcoal
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Estate & Property Vault",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Garage, Hangars, Islands & Private Holdings",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        }

        // Sub-tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = ZephyrCharcoal,
            edgePadding = 0.dp,
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Garage (${ownedMansions.size + ownedCars.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Private Hangar (${ownedJets.size + ownedHangars.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Islands & Boats (${ownedIslands.size + ownedBoats.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("All Possessions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Garage: Mansions & Vehicles (excluding boats and jets)
                    if (ownedMansions.isEmpty() && ownedCars.isEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Owned Mansions & Estates", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                                    Text("${luxuryAssets.count { it.category == "Mansions" }} mansion slots available", fontSize = 11.sp, color = ZephyrTaupe)
                                }
                                if (isEditModeActive) {
                                    Button(
                                        onClick = onAddMansionSlot,
                                        colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp).testTag("increase_mansion_slots_btn")
                                    ) { Text("+ Add Slot", fontSize = 11.sp, color = Color.White) }
                                }
                            }
                        }
                        item {
                            EmptyStateCard("Your garage is empty. Purchase luxury mansions or supercars in the Shop.")
                        }
                    } else {
                        if (ownedMansions.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Owned Mansions & Estates", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                                        Text("${ownedMansions.size} mansion slots", fontSize = 11.sp, color = ZephyrTaupe)
                                    }
                                    if (isEditModeActive) {
                                        Button(
                                            onClick = onAddMansionSlot,
                                            colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                                            shape = RoundedCornerShape(16.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(36.dp).testTag("increase_mansion_slots_btn")
                                        ) {
                                            Text("+ Add Slot", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                            items(ownedMansions, key = { "mansion_${it.id}" }) { mansion ->
                                LuxuryPropertyCard(asset = mansion)
                            }
                        }
                        if (ownedCars.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Garage Supercars & Vehicles", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                            }
                            items(ownedCars, key = { "car_${it.id}" }) { car ->
                                GarageCarCard(car = car)
                            }
                        }
                    }
                }

                1 -> {
                    // Private Hangar: Jets and Hangars
                    if (ownedJets.isEmpty() && ownedHangars.isEmpty()) {
                        item {
                            EmptyStateCard("No private hangars or jets owned. Purchase a Hangar first, then a Private Jet.")
                        }
                    } else {
                        if (ownedHangars.isNotEmpty()) {
                            item {
                                Text("Owned Aviation Hangars", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                            }
                            items(ownedHangars, key = { "hangar_${it.id}" }) { hangar ->
                                LuxuryPropertyCard(asset = hangar)
                            }
                        }
                        if (ownedJets.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Private Jet Fleet", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                            }
                            items(ownedJets, key = { "jet_${it.id}" }) { jet ->
                                LuxuryPropertyCard(asset = jet)
                            }
                        }
                    }
                }

                2 -> {
                    // Islands & Boats
                    if (ownedIslands.isEmpty() && ownedBoats.isEmpty()) {
                        item {
                            EmptyStateCard("No islands or superyachts owned. Purchase an Island before buying a boat.")
                        }
                    } else {
                        if (ownedIslands.isNotEmpty()) {
                            item {
                                Text("Private Islands", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                            }
                            items(ownedIslands, key = { "island_${it.id}" }) { island ->
                                LuxuryPropertyCard(asset = island)
                            }
                        }
                        if (ownedBoats.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Superyachts & Boats", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ZephyrCharcoal)
                            }
                            items(ownedBoats, key = { "boat_${it.id}" }) { boat ->
                                LuxuryPropertyCard(asset = boat)
                            }
                        }
                    }
                }

                3 -> {
                    val allOwned = ownedMansions + ownedCars + ownedJets + ownedHangars + ownedIslands + ownedBoats + otherOwned
                    if (allOwned.isEmpty()) {
                        item {
                            EmptyStateCard("You haven't acquired any luxury assets yet.")
                        }
                    } else {
                        items(ownedMansions) { LuxuryPropertyCard(asset = it) }
                        items(ownedCars) { GarageCarCard(car = it) }
                        items(ownedJets) { LuxuryPropertyCard(asset = it) }
                        items(ownedHangars) { LuxuryPropertyCard(asset = it) }
                        items(ownedIslands) { LuxuryPropertyCard(asset = it) }
                        items(ownedBoats) { LuxuryPropertyCard(asset = it) }
                        items(otherOwned) { LuxuryPropertyCard(asset = it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ZephyrTaupe,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.testTag("empty_property_text")
        )
    }
}

@Composable
private fun LuxuryPropertyCard(asset: LuxuryAsset) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("property_card_${asset.id}")
    ) {
        Column {
            // Match the large, full-width image used on the Shop car cards.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFBF9F6))
            ) {
                DynamicDealershipImage(
                    imageIdentifier = asset.imageUri,
                    contentDescription = asset.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = asset.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ZephyrCharcoal
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${asset.category} · Prestige ${asset.prestige}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ZephyrTaupe)
            )
            if (asset.bedrooms != null && asset.baths != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${asset.bedrooms} Beds · ${asset.baths} Baths · ${asset.internet ?: "High-speed Fiber"}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ZephyrTaupe)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Valued at $" + String.format(Locale.US, "%,.0f", asset.price),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = ZephyrCharcoal
                )
            )
        }
    }
}

@Composable
private fun GarageCarCard(car: DealershipCar) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DynamicDealershipImage(
                imageIdentifier = car.imageUri,
                contentDescription = car.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = car.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ZephyrCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${car.brand} · ${car.power} · ${car.engine}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Acquired for $" + String.format(Locale.US, "%,.0f", car.priceBuy),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = ZephyrCharcoal
                    )
                )
            }
        }
    }
}
