package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VolumeUp
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
import com.example.model.PlayerProfile
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ProfileScreen(
    profile: PlayerProfile,
    totalNetWorth: Double,
    businessesCount: Int,
    luxuryCount: Int,
    isEditModeActive: Boolean = false,
    offlineTaxPercent: Double = 5.0,
    onToggleEditMode: () -> Unit = {},
    onUpdateOfflineTax: (Double) -> Unit = {},
    onOpenDealership: () -> Unit = {},
    onOpenProperty: () -> Unit = {},
    onEditName: (String) -> Unit,
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(profile.name) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var offlineTaxInput by remember(offlineTaxPercent) { mutableStateOf(offlineTaxPercent.toString()) }

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
                    text = "Executive Profile",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = ZephyrCharcoal
                    )
                )
                Text(
                    text = "Bespoke executive credentials and simulation settings",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ZephyrTaupe
                    )
                )
            }
        }

        // Profile Dossier Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.dp, ZephyrCardBorder, RoundedCornerShape(22.dp))
                    .padding(22.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    // Monogram Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ZephyrCharcoal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase(),
                            fontFamily = PlayfairFontFamily,
                            fontSize = 26.sp,
                            color = ZephyrChampagne
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = profile.name,
                            fontFamily = PlayfairFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = ZephyrCharcoal
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = {
                                editedName = profile.name
                                showEditDialog = true
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("edit_profile_name_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit name",
                                tint = ZephyrTaupe,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = profile.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = ZephyrTaupe
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = ZephyrCardBorder)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem("Net Worth", "$" + String.format(Locale.US, "%,.0f", totalNetWorth))
                        ProfileStatItem("Enterprises", "$businessesCount Active")
                        ProfileStatItem("Trophy Assets", "$luxuryCount Owned")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Settings / Preferences Card
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
                    Text(
                        text = "System & Simulation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = ZephyrCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dealership & Photo Edit Mode",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZephyrCharcoal
                                )
                            )
                            Text(
                                text = "When activated: edit photo, name, price, and rent/lease on every car & image",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                        }

                        Switch(
                            checked = isEditModeActive,
                            onCheckedChange = { onToggleEditMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZephyrCharcoal
                            ),
                            modifier = Modifier.testTag("setting_dealer_edit_mode_switch")
                        )
                    }

                    HorizontalDivider(color = ZephyrCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDealership() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Open Luxeon Auto Dealership & Shop",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = ZephyrCharcoal
                                )
                            )
                            Text(
                                text = "Browse luxury showroom and vehicle listings",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = ZephyrCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    HorizontalDivider(color = ZephyrCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProperty() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "View Property Vault & Garage",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = ZephyrCharcoal
                                )
                            )
                            Text(
                                text = "Houses, garage vehicles, hangars, private jets, and islands",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = ZephyrCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Offline Inactivity Tax (After 2 Hours)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZephyrCharcoal
                            )
                        )
                        Text(
                            text = "Additional tax applied when returning after 2 hours absence",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = ZephyrTaupe
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = offlineTaxInput,
                                onValueChange = { offlineTaxInput = it },
                                label = { Text("Tax %") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = {
                                    val pct = offlineTaxInput.toDoubleOrNull() ?: offlineTaxPercent
                                    onUpdateOfflineTax(pct)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ZephyrCharcoal),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save", color = Color.White)
                            }
                        }
                    }

                    HorizontalDivider(color = ZephyrCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Haptic & Audio Feedback",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = ZephyrCharcoal
                                )
                            )
                            Text(
                                text = "Tactile clicks during transactions",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = ZephyrTaupe
                                )
                            )
                        }

                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = { hapticsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZephyrCharcoal
                            )
                        )
                    }

                    HorizontalDivider(color = ZephyrCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(vertical = 6.dp)
                            .testTag("reset_simulation_data_btn"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.RestartAlt,
                                contentDescription = "Reset",
                                tint = ZephyrNegativeRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Reset Simulation Career",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = ZephyrNegativeRed,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Name Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Executive Identity",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ZephyrCharcoal,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Player Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            onEditName(editedName.trim())
                        }
                        showEditDialog = false
                    },
                    modifier = Modifier.testTag("confirm_edit_name_btn")
                ) {
                    Text("Save", color = ZephyrCharcoal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = ZephyrTaupe)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Reset Data Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Tycoon Career?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ZephyrNegativeRed,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            text = {
                Text(
                    text = "This will restore initial balance (\$82,745.60) and default enterprises. All acquired assets and transactions will be reset.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ZephyrTaupe,
                        fontSize = 13.sp
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZephyrNegativeRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_reset_data_btn")
                ) {
                    Text("Confirm Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = ZephyrTaupe)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ProfileStatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZephyrCharcoal
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                color = ZephyrTaupe
            )
        )
    }
}
