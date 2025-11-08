package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    modifier: Modifier = Modifier,
    viewModel: RequestViewModel = koinViewModel()
) {
    val requestsState by viewModel.requestsState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Доставки")
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = requestsState is FetchResultUiState.Loading,
            onRefresh = viewModel::fetchRequests
        ) {
            requestsState.Reduce(
                onLoading = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)
                    ) {
                        items(5) {
                            RequestCardShimmer()
                        }
                    }
                },
                onSuccess = { requests ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp)
                    ) {
                        items(items = requests, key = { it.id }) { request ->
                            RequestCard(request = request)
                        }
                    }
                },
                onError = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorMessage(message = it, onRetry = viewModel::fetchRequests)
                    }
                }
            )
        }

    }
}