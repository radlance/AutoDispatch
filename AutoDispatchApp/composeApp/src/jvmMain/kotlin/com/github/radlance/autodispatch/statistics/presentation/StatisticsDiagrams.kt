package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.statistics.domain.DashboardStatistics

@Composable
fun StatisticsDiagrams(
    statistics: DashboardStatistics,
    modifier: Modifier = Modifier
) {
    val extendedMaterialPalette = listOf(
        Color(0xFF5A9DE4),
        Color(0xFF007BFF),
        Color(0xFF00D2B4),
        Color(0xFF6A5AE0),
        Color(0xFFF9B89B),
        Color(0xFFED6A8A),
        Color(0xFF9B308F),
        Color(0xFF2E5AAC),
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFF00BCD4),
        Color(0xFF9C27B0),
        Color(0xFFFF5722),
        Color(0xFF795548),
        Color(0xFF607D8B),
        Color(0xFFFFC107)
    )
    val statusColors = mapOf(
        "Ожидает" to Color(0xFF5A9DE4),
        "Назначена" to Color(0xFF007BFF),
        "В пути" to Color(0xFF00D2B4),
        "Завершена" to Color(0xFF4CAF50),
        "Отменена" to Color(0xFFCC2828),
        "На проверке" to Color(0xFF6A5AE0),
        "Отклонена" to Color(0xFFFF9800)
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticsDiagramCard(
            label = "Распределение заявок",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            val pieData = statistics.requestsByStatus.map { item ->
                PieChartData(
                    label = item.label,
                    value = item.count.toFloat(),
                    color = statusColors.getValue(item.label)
                )
            }

            AnimatedPieChart(
                data = pieData,
                innerRadiusRatio = 0.5f,
                modifier = Modifier.height(250.dp)
            )
        }

        StatisticsDiagramCard(
            label = "Топ водителей",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            TopDriverList(statistics.topDrivers)
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticsDiagramCard(
            label = "Статус автомобилей",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            val pieData = statistics.vehiclesByStatus.map { item ->
                PieChartData(
                    label = item.label,
                    value = item.count.toFloat(),
                    color = if (item.label == "Свободен") Color(0xFF4CAF50) else Color(0xFF007BFF)
                )
            }

            AnimatedPieChart(
                data = pieData,
                innerRadiusRatio = 0.5f,
                modifier = Modifier.height(250.dp)
            )
        }
        StatisticsDiagramCard(
            label = "Статус водителей",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            val pieData = statistics.driversByStatus.map { item ->
                PieChartData(
                    label = item.label,
                    value = item.count.toFloat(),
                    color = if (item.label == "Свободен") Color(0xFF4CAF50) else Color(0xFF007BFF)
                )
            }

            AnimatedPieChart(
                data = pieData,
                innerRadiusRatio = 0.5f,
                modifier = Modifier.height(250.dp)
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    StatisticsDiagramCard(
        label = "Типы грузов",
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val pieData = statistics.requestsByCargoType.mapIndexed { index, item ->
            PieChartData(
                label = item.label,
                value = item.count.toFloat(),
                color = extendedMaterialPalette.getOrNull(index)
                    ?: extendedMaterialPalette.random()
            )
        }

        AnimatedPieChart(
            data = pieData,
            maxItemsInEachRow = Int.MAX_VALUE,
            modifier = Modifier.height(250.dp)
        )
    }
}

@Composable
private fun StatisticsDiagramCard(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))
            content()
        }
    }
}