package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.statistics.domain.DashboardStatistics
import com.github.radlance.autodispatch.uikit.vector.DocumentIcon

@Composable
fun StatisticsGrid(statistics: DashboardStatistics) {
    val statsData = listOf(
        StatItemData(
            title = "Всего заявок",
            value = statistics.general.totalRequests.toString(),
            icon = DocumentIcon,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        StatItemData(
            title = "Завершено",
            value = statistics.general.completedRequests.toString(),
            icon = Icons.Outlined.CheckCircle,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        StatItemData(
            title = "Автомобили",
            value = statistics.general.totalVehicles.toString(),
            icon = Icons.Outlined.LocalShipping,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        StatItemData(
            title = "Водители",
            value = statistics.general.totalDrivers.toString(),
            icon = Icons.Outlined.Group,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 4
    ) {
        statsData.forEach { item ->
            StatCard(
                data = item,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

data class StatItemData(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val contentColor: Color
)

@Composable
private fun StatCard(
    data: StatItemData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(data.backgroundColor.copy(alpha = 0.6f))
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(12.dp),
                        color = data.contentColor.copy(alpha = 0.2f)
                    )
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = data.contentColor
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = data.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = data.value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}