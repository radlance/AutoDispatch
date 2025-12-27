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

data class BarItem(
    val name: String,
    val value: Double,
    val color: Color
)

data class ChartGroup(
    val groupLabel: String,
    val items: List<BarItem>
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GroupedRowChart(
    groups: List<ChartGroup>,
    modifier: Modifier = Modifier,
    barThickness: Dp = 28.dp,
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

    val verticalPadding = 20.dp
    val labelPadding = 25f
    val rightMargin = 60f

    val labelStyle =
        TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = onSurface)

    val maxLabelWidth = remember(groups, textMeasurer) {
        groups.maxOfOrNull { group ->
            textMeasurer.measure(group.groupLabel, style = labelStyle).size.width
        }?.toFloat() ?: 0f
    }

    val leftMargin = maxLabelWidth + labelPadding

    val rawMax = groups.flatMap { it.items }.maxOfOrNull { it.value }?.toFloat() ?: 1f
    val niceMax = remember(rawMax) { calculateNiceMax(rawMax) }

    val chartContentHeightPx = remember(groups, density) {
        with(density) {
            var total = 0f
            groups.forEachIndexed { gIndex, group ->
                if (gIndex > 0) total += groupSpacing.toPx()
                group.items.forEachIndexed { iIndex, _ ->
                    if (iIndex > 0) total += itemSpacing.toPx()
                    total += barThickness.toPx()
                }
            }
            total + (verticalPadding.toPx() * 2)
        }
    }

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
                    .height(with(density) { chartContentHeightPx.toDp() + 40.dp })
                    .onSizeChanged { chartSize = it }
            ) {
                val chartWidth = (size.width - leftMargin - rightMargin).coerceAtLeast(0f)
                val topOffset = verticalPadding.toPx()

                val steps = 5
                for (i in 0..steps) {
                    val xVal = (niceMax / steps) * i
                    val xPos = leftMargin + (i * (chartWidth / steps))

                    drawLine(axisColor, Offset(xPos, 0f), Offset(xPos, chartContentHeightPx))

                    val xLabel =
                        if (xVal >= 1000) "${(xVal / 1000).roundToInt()}k" else xVal.roundToInt()
                            .toString()
                    val labelTopLeft = Offset(xPos - 10f, chartContentHeightPx + 10f)
                    val availableWidth = size.width - labelTopLeft.x
                    val availableHeight = size.height - labelTopLeft.y
                    if (availableWidth > 0 && availableHeight > 0) {
                        drawText(
                            textMeasurer = textMeasurer,
                            text = xLabel,
                            topLeft = labelTopLeft,
                            style = TextStyle(fontSize = 11.sp, color = onSurface),
                            softWrap = false
                        )
                    }
                }

                var currentY = topOffset
                groups.forEachIndexed { gIndex, group ->
                    if (gIndex > 0) currentY += groupSpacing.toPx()
                    val groupStart = currentY
                    group.items.forEachIndexed { iIndex, item ->
                        if (iIndex > 0) currentY += itemSpacing.toPx()
                        val barWidth =
                            (item.value.toFloat() / niceMax) * chartWidth * transitionProgress.value

                        drawRoundRect(
                            color = item.color,
                            topLeft = Offset(leftMargin, currentY),
                            size = Size(barWidth, barThickness.toPx()),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                        currentY += barThickness.toPx()
                    }

                    val groupEnd = currentY
                    val labelLayout = textMeasurer.measure(
                        group.groupLabel,
                        style = labelStyle
                    )
                    drawText(
                        labelLayout,
                        topLeft = Offset(
                            leftMargin - labelLayout.size.width - labelPadding,
                            groupStart + (groupEnd - groupStart) / 2 - labelLayout.size.height / 2
                        )
                    )
                }

                drawLine(
                    axisColor,
                    Offset(leftMargin, 0f),
                    Offset(leftMargin, chartContentHeightPx),
                    1.dp.toPx()
                )
                drawLine(
                    axisColor, Offset(leftMargin, chartContentHeightPx), Offset(
                        leftMargin + chartWidth,
                        chartContentHeightPx
                    ), 1.dp.toPx()
                )
            }

            val hoveredData =
                remember(mousePosition, chartSize, groups, transitionProgress.value, density) {
                    if (chartSize == IntSize.Zero) return@remember null

                    val chartWidth =
                        (chartSize.width.toFloat() - leftMargin - rightMargin).coerceAtLeast(0f)
                    var currentY = with(density) { verticalPadding.toPx() }

                    groups.forEachIndexed { gIndex, group ->
                        if (gIndex > 0) currentY += with(density) { groupSpacing.toPx() }
                        group.items.forEachIndexed { iIndex, item ->
                            if (iIndex > 0) currentY += with(density) { itemSpacing.toPx() }
                            val width =
                                (item.value.toFloat() / niceMax) * chartWidth * transitionProgress.value
                            val rect = Rect(
                                leftMargin,
                                currentY,
                                leftMargin + width,
                                currentY + with(density) { barThickness.toPx() })
                            if (rect.contains(mousePosition)) {
                                return@remember item to mousePosition
                            }
                            currentY += with(density) { barThickness.toPx() }
                        }
                    }
                    null
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Box(modifier = Modifier.size(10.dp).background(worker.color, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = worker.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}