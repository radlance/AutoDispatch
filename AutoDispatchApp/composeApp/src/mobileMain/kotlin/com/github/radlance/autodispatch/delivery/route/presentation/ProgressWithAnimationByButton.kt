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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

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
fun ProgressWithAnimationByButton() {
    var progress by remember { mutableStateOf(0.65f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    val stepTexts = remember {
        listOf(
            0.01f to "first",
            0.33f to "second",
            0.66f to "third",
            1f to "last"
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            horizontalAlignment = Alignment.Start
        ) {
            stepTexts.forEach { (threshold, text) ->
                AnimatedVisibility(
                    visible = progress >= threshold,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Прогресс: ${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                progress = (progress + 0.25f).coerceAtMost(1f)
            },
            modifier = Modifier.width(280.dp)
        ) {
            Text("Увеличить на +25%")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { progress = 0f },
            modifier = Modifier.width(280.dp)
        ) {
            Text("Сбросить до 0%")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { progress = 1f },
            modifier = Modifier.width(280.dp)
        ) {
            Text("Сразу 100%")
        }
    }
}