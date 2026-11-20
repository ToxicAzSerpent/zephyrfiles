package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Timeframe

@Composable
fun ZephyrGraph(
    timeframe: Timeframe,
    customPoints: List<Float>? = null,
    onPointSelected: ((Float?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Generate organic rising trajectory data based on timeframe or use custom stock points
    val dataPoints = remember(timeframe, customPoints) {
        if (customPoints != null && customPoints.size >= 2) {
            customPoints
        } else {
            when (timeframe) {
                Timeframe.ONE_WEEK -> listOf(116200f, 117100f, 116800f, 118400f, 119200f, 118900f, 120560f)
                Timeframe.ONE_MONTH -> listOf(112464f, 113200f, 112900f, 115400f, 114800f, 117600f, 116900f, 118800f, 117900f, 120560f)
                Timeframe.THREE_MONTHS -> listOf(104200f, 106500f, 105800f, 109400f, 108100f, 112000f, 115200f, 120560f)
                Timeframe.SIX_MONTHS -> listOf(98000f, 101200f, 99800f, 104500f, 108000f, 112400f, 115900f, 120560f)
                Timeframe.ONE_YEAR -> listOf(84000f, 89500f, 88200f, 95100f, 99400f, 105000f, 112000f, 120560f)
                Timeframe.ALL -> listOf(45000f, 58000f, 64000f, 79000f, 91000f, 102000f, 111000f, 120560f)
            }
        }
    }

    var activeDragX by remember { mutableStateOf<Float?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("portfolio_graph")
            .pointerInput(dataPoints) {
                detectTapGestures(
                    onPress = { offset ->
                        activeDragX = offset.x
                        tryAwaitRelease()
                        activeDragX = null
                        onPointSelected?.invoke(null)
                    }
                )
            }
            .pointerInput(dataPoints) {
                detectDragGestures(
                    onDragStart = { offset -> activeDragX = offset.x },
                    onDragEnd = {
                        activeDragX = null
                        onPointSelected?.invoke(null)
                    },
                    onDragCancel = {
                        activeDragX = null
                        onPointSelected?.invoke(null)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        activeDragX = change.position.x
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            if (dataPoints.size < 2) return@Canvas

            val minVal = dataPoints.minOrNull() ?: 0f
            val maxVal = dataPoints.maxOrNull() ?: 1f
            val range = (maxVal - minVal).coerceAtLeast(1f)

            // Leave 15% padding top and bottom
            val topPadding = height * 0.12f
            val bottomPadding = height * 0.08f
            val usableHeight = height - topPadding - bottomPadding

            val stepX = width / (dataPoints.size - 1)

            val points = dataPoints.mapIndexed { index, value ->
                val x = index * stepX
                val normalizedY = (value - minVal) / range
                val y = height - bottomPadding - (normalizedY * usableHeight)
                Offset(x, y)
            }

            // Construct smooth cubic bezier path
            val strokePath = Path()
            val fillPath = Path()

            strokePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, height)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX1 = p0.x + (p1.x - p0.x) / 2
                val controlY1 = p0.y
                val controlX2 = p0.x + (p1.x - p0.x) / 2
                val controlY2 = p1.y

                strokePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
            }

            fillPath.lineTo(points.last().x, height)
            fillPath.close()

            // Draw Area Gradient Fill (warm champagne/taupe matching reference)
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBA9E7E).copy(alpha = 0.35f),
                        Color(0xFFD8C7B5).copy(alpha = 0.12f),
                        Color(0xFFF7F5F1).copy(alpha = 0.00f)
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Line Stroke
            drawPath(
                path = strokePath,
                color = Color(0xFF9E8569),
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // If user is scrubbing, draw subtle vertical indicator line and circle
            activeDragX?.let { dragX ->
                val clampedX = dragX.coerceIn(0f, width)
                val fractionalIndex = (clampedX / stepX).coerceIn(0f, (dataPoints.size - 1).toFloat())
                val lowerIndex = fractionalIndex.toInt()
                val upperIndex = (lowerIndex + 1).coerceAtMost(dataPoints.size - 1)
                val ratio = fractionalIndex - lowerIndex
                val interpVal = dataPoints[lowerIndex] + (dataPoints[upperIndex] - dataPoints[lowerIndex]) * ratio
                onPointSelected?.invoke(interpVal)

                val normalizedY = (interpVal - minVal) / range
                val y = height - bottomPadding - (normalizedY * usableHeight)

                // Vertical hairline indicator
                drawLine(
                    color = Color(0x661C1B1A),
                    start = Offset(clampedX, 0f),
                    end = Offset(clampedX, height),
                    strokeWidth = 1.dp.toPx()
                )

                // Point circle
                drawCircle(
                    color = Color(0xFF1C1B1A),
                    radius = 4.5.dp.toPx(),
                    center = Offset(clampedX, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = Offset(clampedX, y)
                )
            }
        }
    }
}
