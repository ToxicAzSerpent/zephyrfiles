package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Timeframe
import com.example.ui.theme.ZephyrCharcoal
import com.example.ui.theme.ZephyrTaupe

@Composable
fun ZephyrTimeframeSelector(
    selectedTimeframe: Timeframe,
    onTimeframeSelected: (Timeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Timeframe.values().forEach { tf ->
            val isSelected = tf == selectedTimeframe
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) ZephyrCharcoal else Color.Transparent,
                label = "tf_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else ZephyrTaupe,
                label = "tf_text"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .clickable { onTimeframeSelected(tf) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("timeframe_${tf.label}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tf.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
