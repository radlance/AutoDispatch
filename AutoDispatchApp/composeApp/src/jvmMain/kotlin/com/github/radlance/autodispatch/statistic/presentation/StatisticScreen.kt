package com.github.radlance.autodispatch.statistic.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun StatisticScreen(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        FullPieChartScreen(
            dataValues = listOf(
                Pair("Android", 20f),
                Pair("Windows", 45f),
                Pair("Linux", 35f),
            )
        )
    }
}