package com.github.radlance.autodispatch.controlpanel.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.shimmerBackground(
    cornerRadius: Dp = 8.dp
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
    )

    val gradientWidth = 500f
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - gradientWidth, y = 0f),
        end = Offset(x = translateAnim, y = 0f)
    )

    this.background(brush, RoundedCornerShape(cornerRadius))
}