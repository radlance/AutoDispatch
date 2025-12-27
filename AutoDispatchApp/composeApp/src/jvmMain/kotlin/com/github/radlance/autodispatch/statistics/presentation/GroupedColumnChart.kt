package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GroupedColumnChart(
    groups: List<ChartGroup>,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 8.dp,
    groupSpacing: Dp = 36.dp,
    axisColor: Color = Color.LightGray
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val transitionProgress = remember { Animatable(0f) }
    var mousePosition by remember { mutableStateOf(Offset.Zero) }
    var chartSize by remember { mutableStateOf(IntSize.Zero) }

    val topPadding = 20.dp
    val bottomPadding = 40.dp
    val leftPadding = 25f
    val rightPadding = 20f

    val labelStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = onSurface)

    val rawMax = groups.flatMap { it.items }.maxOfOrNull { it.value }?.toFloat() ?: 1f
    val niceMax = remember(rawMax) { calculateNiceMax(rawMax) }

    val steps = 5
    val yLabels = (0..steps).map { i ->
        val yVal = (niceMax / steps) * i
        if (yVal >= 1000) "${(yVal / 1000).roundToInt()}k" else yVal.roundToInt().toString()
    }
    val maxYLabelWidth = remember(yLabels, textMeasurer) {
        yLabels.maxOfOrNull { textMeasurer.measure(it, style = TextStyle(fontSize = 11.sp, color = onSurface)).size.width }?.toFloat() ?: 0f
    }

    val leftMargin = maxYLabelWidth + leftPadding

    LaunchedEffect(groups) {
        transitionProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }

    Column(modifier = modifier.padding(16.dp).widthIn(max = 1000.dp)) {
        Box(
            modifier = Modifier
                .onPointerEvent(PointerEventType.Move) { event ->
                    mousePosition = event.changes.first().position
                }
                .onPointerEvent(PointerEventType.Exit) {
                    mousePosition = Offset.Zero
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp + bottomPadding)
                    .onSizeChanged { chartSize = it }
            ) {
                val chartWidth = (size.width - leftMargin - rightPadding).coerceAtLeast(0f)
                val chartTopY = topPadding.toPx()
                val chartBottomY = size.height - bottomPadding.toPx()
                val chartHeight = chartBottomY - chartTopY

                val totalBars = groups.sumOf { it.items.size }
                val totalItemSpacings = groups.sumOf { group -> ((group.items.size - 1).coerceAtLeast(0)).toDouble() * itemSpacing.toPx().toDouble() }.toFloat()
                val totalGroupSpacings = ((groups.size - 1).coerceAtLeast(0)).toFloat() * groupSpacing.toPx()

                val availableForBars = chartWidth - totalItemSpacings - totalGroupSpacings
                val barPx = (availableForBars / totalBars).coerceAtLeast(4f) // min 4px to avoid too thin

                for (i in 0..steps) {
                    val yPos = chartBottomY - (i * (chartHeight / steps))
                    drawLine(axisColor, Offset(leftMargin, yPos), Offset(leftMargin + chartWidth, yPos))

                    val yLabel = yLabels[i]
                    val labelLayout = textMeasurer.measure(yLabel, style = TextStyle(fontSize = 11.sp, color = onSurface))
                    drawText(
                        labelLayout,
                        topLeft = Offset(leftMargin - labelLayout.size.width - 10f, yPos - labelLayout.size.height / 2)
                    )
                }

                var currentX = leftMargin
                groups.forEachIndexed { gIndex, group ->
                    if (gIndex > 0) currentX += groupSpacing.toPx()
                    val groupStartX = currentX
                    group.items.forEachIndexed { iIndex, item ->
                        if (iIndex > 0) currentX += itemSpacing.toPx()
                        val barHeight = (item.value.toFloat() / niceMax) * chartHeight * transitionProgress.value

                        drawRoundRect(
                            color = item.color,
                            topLeft = Offset(currentX, chartBottomY - barHeight),
                            size = Size(barPx, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                        currentX += barPx
                    }

                    val groupEndX = currentX
                    val labelLayout = textMeasurer.measure(
                        group.groupLabel,
                        style = labelStyle
                    )
                    drawText(
                        labelLayout,
                        topLeft = Offset(groupStartX + (groupEndX - groupStartX) / 2 - labelLayout.size.width / 2, chartBottomY + 10f)
                    )
                }

                drawLine(axisColor, Offset(leftMargin, chartBottomY), Offset(leftMargin + chartWidth, chartBottomY), 1.dp.toPx())
                drawLine(axisColor, Offset(leftMargin, chartBottomY), Offset(leftMargin, chartTopY), 1.dp.toPx())
            }

            val hoveredData = remember(mousePosition, chartSize, groups, transitionProgress.value, density) {
                if (chartSize == IntSize.Zero) return@remember null

                with(density) {
                    val chartWidth = (chartSize.width.toFloat() - leftMargin - rightPadding).coerceAtLeast(0f)
                    val chartTopY = topPadding.toPx()
                    val chartBottomY = chartSize.height.toFloat() - bottomPadding.toPx()
                    val chartHeight = chartBottomY - chartTopY

                    val totalBars = groups.sumOf { it.items.size }
                    val totalItemSpacings = groups.sumOf { group -> ((group.items.size - 1).coerceAtLeast(0)).toDouble() * itemSpacing.toPx().toDouble() }.toFloat()
                    val totalGroupSpacings = ((groups.size - 1).coerceAtLeast(0)).toFloat() * groupSpacing.toPx()

                    val availableForBars = chartWidth - totalItemSpacings - totalGroupSpacings
                    val barPx = (availableForBars / totalBars).coerceAtLeast(4f)

                    var currentX = leftMargin
                    groups.forEachIndexed { gIndex, group ->
                        if (gIndex > 0) currentX += groupSpacing.toPx()
                        group.items.forEachIndexed { iIndex, item ->
                            if (iIndex > 0) currentX += itemSpacing.toPx()
                            val barHeight = (item.value.toFloat() / niceMax) * chartHeight * transitionProgress.value
                            val rect = Rect(currentX, chartBottomY - barHeight, currentX + barPx, chartBottomY)
                            if (rect.contains(mousePosition)) {
                                return@remember item to mousePosition
                            }
                            currentX += barPx
                        }
                    }
                    null
                }
            }

            hoveredData?.let { (item, pos) ->
                HoverPopup(
                    item = item,
                    position = pos
                )
            }
        }
        LegendLayout(groups.flatMap { it.items }.distinctBy { it.name })
    }
}

@Composable
private fun HoverPopup(
    item: BarItem,
    position: Offset
) {
    Popup(
        offset = IntOffset(position.x.toInt() + 20, position.y.toInt() + 20),
        properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Text(
                text = "${item.name}: ${item.value.toInt()}",
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun calculateNiceMax(max: Float): Float {
    if (max <= 0) return 100f
    val unit = 10.0.pow(floor(log10(max.toDouble()))).toFloat()
    val normalized = max / unit

    val niceNormalized = when {
        normalized <= 1.5 -> 1.5f
        normalized <= 2.0 -> 2.0f
        normalized <= 2.5 -> 2.5f
        normalized <= 3.0 -> 3.0f
        normalized <= 4.0 -> 4.0f
        normalized <= 5.0 -> 5.0f
        normalized <= 6.0 -> 6.0f
        normalized <= 8.0 -> 8.0f
        else -> 10.0f
    }
    val niceMax = niceNormalized * unit
    return if (niceMax < max) niceMax + unit else niceMax
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LegendLayout(workers: List<BarItem>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        workers.forEach { worker ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                Box(modifier = Modifier.size(10.dp).background(worker.color, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = worker.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}