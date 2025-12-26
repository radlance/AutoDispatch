package com.github.radlance.autodispatch.statistic.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FullPieChartScreen(dataValues: List<Pair<String, Float>>) {
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

    val chartData = dataValues.mapIndexed { index, pair ->
        PieChartData(pair.first, pair.second, extendedMaterialPalette.getOrElse(index) { Color.Gray })
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedPieChart(
            modifier = Modifier.size(300.dp).padding(top = 24.dp),
            data = chartData,
            innerRadiusRatio = 0.6f
        )

        PieChartLegend(data = chartData)
    }
}