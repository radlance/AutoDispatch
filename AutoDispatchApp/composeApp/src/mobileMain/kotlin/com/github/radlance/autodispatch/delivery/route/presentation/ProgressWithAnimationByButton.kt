package com.github.radlance.autodispatch.delivery.route.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MilestoneDot(isCompleted: Boolean) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) Color.Transparent else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isCompleted,
            enter = scaleIn(tween(350)) + fadeIn(tween(200)),
            exit = scaleOut(tween(300)) + fadeOut(tween(200))
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProgressWithAnimationByButton(
    progress: Float,
    leftLabel: String,
    rightLabel: String,
    steps: List<ProgressStep>,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    val labelBoxWidth = 64.dp

    val leftLabelColor by animateColorAsState(
        targetValue = if (progress > 0.01f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300)
    )

    val rightLabelColor by animateColorAsState(
        targetValue = if (progress >= 0.99f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MilestoneDot(isCompleted = progress > 0.01f)

            Spacer(Modifier.width(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.weight(1f),
                drawStopIndicator = {},
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.width(12.dp))

            MilestoneDot(isCompleted = progress >= 0.99f)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(modifier = Modifier.width(labelBoxWidth)) {
                Text(
                    text = leftLabel,
                    style = MaterialTheme.typography.labelSmall.copy(lineHeight = 12.sp),
                    color = leftLabelColor,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.width(12.dp))

            Spacer(Modifier.weight(1f))

            Box(modifier = Modifier.width(labelBoxWidth)) {
                Text(
                    text = rightLabel,
                    style = MaterialTheme.typography.labelSmall.copy(lineHeight = 12.sp),
                    color = rightLabelColor,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().animateContentSize()
        ) {
            steps.forEach { step ->
                AnimatedVisibility(
                    visible = step.isCompleted,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = step.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = step.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }
        }
    }
}

data class ProgressStep(
    val label: String,
    val time: String,
    val isCompleted: Boolean
)
