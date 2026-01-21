package com.github.radlance.autodispatch.common.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.domain.DriverStatus

@Composable
fun DriverStatusWithColor(
    status: DriverStatus?,
    fontSize: TextUnit = TextUnit.Unspecified,
    verticalPadding: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) =
        status?.colors()
            ?: (MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = status?.title ?: "-",
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = verticalPadding
            )
        )
    }
}

@Composable
private fun DriverStatus.colors() =
    when (this) {
        DriverStatus.Free ->
            MaterialTheme.colorScheme.primaryContainer to
                    MaterialTheme.colorScheme.onPrimaryContainer

        DriverStatus.OnRoute ->
            MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer
    }
