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
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.uikit.theme.statusPalette

@Composable
fun StatusWithColor(
    status: RequestStatus?,
    fontSize: TextUnit = TextUnit.Unspecified,
    verticalPadding: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    val palette = MaterialTheme.statusPalette
    val (bgColor, textColor) =
        status?.colors()
            ?: (palette.neutralBg to palette.neutralText)

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
private fun RequestStatus.colors() =
    when (this) {
        RequestStatus.Waiting -> {
            val palette = MaterialTheme.statusPalette
            palette.neutralBg to palette.neutralText
        }

        RequestStatus.Assigned -> {
            val palette = MaterialTheme.statusPalette
            palette.infoBg to palette.infoText
        }

        RequestStatus.InProgress -> {
            val palette = MaterialTheme.statusPalette
            palette.progressBg to palette.progressText
        }

        RequestStatus.OnCheck -> {
            val palette = MaterialTheme.statusPalette
            palette.reviewBg to palette.reviewText
        }

        RequestStatus.Completed -> {
            val palette = MaterialTheme.statusPalette
            palette.successBg to palette.successText
        }

        RequestStatus.Canceled,
        RequestStatus.Rejected -> {
            val palette = MaterialTheme.statusPalette
            palette.errorBg to palette.errorText
        }
    }
