package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }
    val statisticsState by viewModel.statisticsState.collectAsState()
    statisticsState.Reduce(
        onLoading = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        },
        onSuccess = { statistics ->
            Box(modifier = modifier) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StatisticsGrid(statistics = statistics)
                    Spacer(Modifier.height(24.dp))
                    StatisticsDiagrams(statistics = statistics)
                    Spacer(Modifier.height(24.dp))
                    DownloadReportDialog()
                }
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, top = 50.dp),
                    adapter = rememberScrollbarAdapter(
                        scrollState = scrollState
                    )
                )
            }
        },
        onError = { errorMsg ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMsg, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.loadStatistics()
                        }
                    ) {
                        Text(stringResource(Res.string.retry))
                    }
                }
            }
        }
    )
}