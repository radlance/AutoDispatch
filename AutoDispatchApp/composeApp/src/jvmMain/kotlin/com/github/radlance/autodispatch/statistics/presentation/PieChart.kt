package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.sqrt

data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimatedPieChart(
    modifier: Modifier = Modifier,
    data: List<PieChartData>,
    innerRadiusRatio: Float = 0f,
    maxItemsInEachRow: Int = 2
) {
    val totalSum = remember(data) { data.sumOf { it.value.toDouble() }.toFloat() }

    val entryAnimation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entryAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
            )
        )
    }

    val scaleAnimatables = remember(data) {
        data.map { Animatable(1f) }
    }

    var mousePosition by remember { mutableStateOf(Offset.Zero) }
    var chartCenter by remember { mutableStateOf(Offset.Zero) }
    var chartRadius by remember { mutableStateOf(0f) }

    val hoveredIndex =
        remember(mousePosition, chartCenter, chartRadius, data, entryAnimation.value) {
            if (entryAnimation.value < 1f) return@remember null

            val dx = mousePosition.x - chartCenter.x
            val dy = mousePosition.y - chartCenter.y
            val distance = sqrt(dx * dx + dy * dy)

            val maxSliceScale = 1.1f
            if (distance > chartRadius * maxSliceScale || distance < chartRadius * innerRadiusRatio) {
                return@remember null
            }

            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
            if (angle < 0) angle += 360f

            var normalizedAngle = angle + 90f
            if (normalizedAngle >= 360f) normalizedAngle -= 360f

            var currentAngle = 0f
            data.indexOfFirst { slice ->
                val sweep = (slice.value / totalSum) * 360f
                val contains =
                    normalizedAngle >= currentAngle && normalizedAngle < (currentAngle + sweep)
                currentAngle += sweep
                contains
            }
        }

    val scope = rememberCoroutineScope()
    LaunchedEffect(hoveredIndex) {
        scaleAnimatables.forEachIndexed { index, animatable ->
            scope.launch {
                val targetScale = if (index == hoveredIndex) 1.1f else 1.0f
                animatable.animateTo(
                    targetValue = targetScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Column {
        Box(
            modifier = modifier
                .onPointerEvent(PointerEventType.Move) { event ->
                    mousePosition = event.changes.first().position
                }
                .onPointerEvent(PointerEventType.Exit) {
                    mousePosition = Offset.Zero
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                chartCenter = center
                val maxSliceScale = 1.1f
                chartRadius = (minOf(size.width, size.height) / 2f) / maxSliceScale

                var startAngle = -90f

                data.forEachIndexed { index, slice ->
                    val sweepAngle = (slice.value / totalSum) * 360f * entryAnimation.value

                    val scale = scaleAnimatables[index].value
                    val currentRadius = chartRadius * scale

                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = center - Offset(currentRadius, currentRadius),
                        size = Size(currentRadius * 2, currentRadius * 2),
                        style = Fill
                    )

                    startAngle += sweepAngle
                }

                if (innerRadiusRatio > 0f) {
                    drawCircle(
                        color = Color.Black,
                        radius = chartRadius * innerRadiusRatio,
                        blendMode = BlendMode.Clear
                    )
                }
            }

            if (hoveredIndex != null && hoveredIndex != -1) {
                val item = data[hoveredIndex]
                Popup(
                    offset = IntOffset(mousePosition.x.toInt() + 20, mousePosition.y.toInt() + 20),
                    properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        Text(
                            text = "${item.label}: ${item.value.toInt()}",
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    PieChartLegend(maxItemsInEachRow, data)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PieChartLegend(maxItemsInEachRow: Int, data: List<PieChartData>) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = maxItemsInEachRow
    ) {
        data.sortedByDescending { it.value }.forEach { item ->
            LegendItem(item)
        }
    }
}

@Composable
fun LegendItem(item: PieChartData) {
    Surface(
        color = item.color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(item.color, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${item.label} : ${item.value.toInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}