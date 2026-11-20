package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ZephyrColorScheme = lightColorScheme(
    primary = ZephyrCharcoal,
    onPrimary = ZephyrSurface,
    primaryContainer = ZephyrChampagneLight,
    onPrimaryContainer = ZephyrCharcoal,
    secondary = ZephyrChampagne,
    onSecondary = ZephyrCharcoal,
    secondaryContainer = ZephyrChampagneLight,
    onSecondaryContainer = ZephyrChampagneDark,
    tertiary = ZephyrTaupe,
    onTertiary = ZephyrSurface,
    background = ZephyrIvoryBg,
    onBackground = ZephyrCharcoal,
    surface = ZephyrSurface,
    onSurface = ZephyrCharcoal,
    surfaceVariant = ZephyrSurfaceSubtle,
    onSurfaceVariant = ZephyrTaupe,
    outline = ZephyrCardBorder,
    outlineVariant = ZephyrChampagneBorder
)

@Composable
fun ZephyrTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZephyrColorScheme,
        typography = ZephyrTypography,
        content = content
    )
}
