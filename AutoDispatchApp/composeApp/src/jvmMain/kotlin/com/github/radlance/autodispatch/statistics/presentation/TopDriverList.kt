package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.LoadableImage
import com.github.radlance.autodispatch.common.utils.abbreviateName
import com.github.radlance.autodispatch.common.utils.avatarInitials
import com.github.radlance.autodispatch.common.presentation.DriverStatusWithColor
import com.github.radlance.autodispatch.statistics.domain.TopDriverStat
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun TopDriverList(topDrivers: List<TopDriverStat>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        topDrivers.forEach { driver ->
            TopDriverItem(driver)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun TopDriverItem(driverStat: TopDriverStat, modifier: Modifier = Modifier) {
    var lastImageRetryAttempt by rememberSaveable { mutableStateOf(0L) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            driverStat.avatarUrl?.let { avatarUrl ->
                LoadableImage(
                    documentUrl = avatarUrl,
                    onRetry = { lastImageRetryAttempt = Clock.System.now().toEpochMilliseconds() },
                    lastRetryAttempt = lastImageRetryAttempt,
                    onImageSelected = null,
                    modifier = Modifier.fillMaxSize(),
                    showLoading = false
                )
            } ?: Text(
                text = avatarInitials(driverStat.fullName),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        Spacer(Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = abbreviateName(driverStat.fullName),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = deliveriesCountText(driverStat.completedAssignments),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DriverStatusWithColor(status = driverStat.currentStatus, fontSize = 14.sp)
    }

}

fun deliveriesCountText(count: Long): String {
    val mod100 = count % 100
    val mod10 = count % 10

    val word = when {
        mod100 in 11..14 -> "доставок"
        mod10 == 1.toLong() -> "доставка"
        mod10 in 2..4 -> "доставки"
        else -> "доставок"
    }

    return "$count $word"
}
