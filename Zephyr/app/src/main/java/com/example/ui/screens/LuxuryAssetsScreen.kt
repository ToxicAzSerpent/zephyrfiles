package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
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
import com.example.model.LuxuryAsset
import com.example.ui.components.DynamicDealershipImage
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun LuxuryAssetsScreen(
    assets: List<LuxuryAsset>,
    cashBalance: Double,
    isEditModeActive: Boolean,
    onBack: () -> Unit,
    onBuyAsset: (String) -> Unit,
    onLeaseAsset: (String) -> Unit,
    onEditAsset: (LuxuryAsset) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Mansions", "Private Jets", "Yachts", "Islands", "Hangars", "Motorcycles", "Apparel", "Watches", "Jewelry", "Shoes", "Phones", "Bags")

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val filteredAssets = remember(selectedCategory, assets) {
        if (selectedCategory == "All") assets else assets.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ZephyrIvoryBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(top = 18.dp, bottom = 32.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("luxury_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = ZephyrCharcoal
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Column {
                            Text(
                                text = "Luxeon Boutique & Shop",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp,
                                    color = ZephyrCharcoal
                                )
                            )
                            Text(
                                text = "Mansions, aviation, yachting, islands, hangars & bespoke goods",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                        }
                    }
                }

                // Category Filter Row
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 18.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = {
                                    Text(
                                        text = cat,
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

                // List of Assets
                items(filteredAssets, key = { it.id }) { asset ->
                    LuxuryAssetCard(
                        asset = asset,
                        canAfford = cashBalance >= asset.price,
                        isEditModeActive = isEditModeActive,
                        onBuy = {
                            if (asset.category.equals("Yachts", ignoreCase = true)) {
                                val hasIsland = assets.any { it.category.equals("Islands", ignoreCase = true) && it.isOwned }
                                if (!hasIsland) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Prerequisite: You must purchase a Private Island before buying a boat/yacht.")
                                    }
                                    return@LuxuryAssetCard
                                }
                            }
                            if (asset.category.equals("Private Jets", ignoreCase = true)) {
                                val hasHangar = assets.any { it.category.equals("Hangars", ignoreCase = true) && it.isOwned }
                                if (!hasHangar) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Prerequisite: You must purchase an Aviation Hangar before buying a Private Jet.")
                                    }
                                    return@LuxuryAssetCard
                                }
                            }
                            onBuyAsset(asset.id)
                        },
                        onLease = { onLeaseAsset(asset.id) },
                        onEdit = { onEditAsset(asset) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun LuxuryAssetCard(
    asset: LuxuryAsset,
    canAfford: Boolean,
    isEditModeActive: Boolean,
    onBuy: () -> Unit,
    onLease: () -> Unit,
    onEdit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, ZephyrCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("luxury_card_${asset.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Image (Mansions 736x736 or standard)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1EDE6))
                ) {
                    DynamicDealershipImage(
                        imageIdentifier = asset.imageUri,
                        contentDescription = asset.name,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (asset.isOwned) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x80000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "Owned",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Owned", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = ZephyrCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${asset.category} · Prestige ${asset.prestige}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                        }

                        if (isEditModeActive) {
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("edit_luxury_${asset.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit Asset",
                                    tint = ZephyrCharcoal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = asset.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = ZephyrTaupe
                        ),
                        maxLines = 2
                    )

                    // Details for Mansions / Islands / etc.
                    if (asset.bedrooms != null && asset.baths != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${asset.bedrooms} Bedrooms · ${asset.baths} Baths · ${asset.internet ?: "High-speed Internet"} · Dim: ${asset.dimension ?: "736x736"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZephyrCharcoal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1EDE6), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Price (incl. 8% Tax)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = ZephyrTaupe)
                    )
                    Text(
                        text = "$" + String.format(Locale.US, "%,.0f", asset.price * 1.08),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!asset.isOwned) {
                        OutlinedButton(
                            onClick = onLease,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "Lease ($${String.format(Locale.US, "%,.0f", asset.rentLeaseMonthly)}/mo)",
                                fontSize = 11.sp,
                                color = ZephyrCharcoal
                            )
                        }

                        Button(
                            onClick = onBuy,
                            colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("buy_luxury_${asset.id}")
                        ) {
                            Text(
                                text = "Acquire",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEBE6DC))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (asset.isLeased) "Leased" else "Owned in Vault",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZephyrCharcoal
                            )
                        }
                    }
                }
            }
        }
    }
}
