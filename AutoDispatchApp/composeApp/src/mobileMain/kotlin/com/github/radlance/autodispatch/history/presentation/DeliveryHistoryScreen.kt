package com.github.radlance.autodispatch.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.PaginationErrorItem
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryCard
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryCardShimmer
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHistoryScreen(
    navigateToDeliveryDetails: (Int, String) -> Unit,
    navigateToDeliveryRoute: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeliveryHistoryViewModel = koinViewModel(),
    deliveryDetailsViewModel: DeliveryDetailsViewModel = koinViewModel()
) {
    val historyState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "История доставок")
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = historyState.itemsState is FetchResultUiState.Loading,
            onRefresh = viewModel::refresh
        ) {
            historyState.itemsState.Reduce(
                onLoading = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)
                    ) {
                        items(5) {
                            DeliveryCardShimmer()
                        }
                    }
                },
                onSuccess = { history ->
                    if (history.isNotEmpty()) {
                        val lazyListState = rememberLazyListState()
                        val historyItems = (historyState.itemsState as? FetchResultUiState.Success)?.data.orEmpty()

                        LaunchedEffect(lazyListState, historyItems.size) {
                            if (historyItems.isEmpty()) return@LaunchedEffect
                            snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                .distinctUntilChanged()
                                .collect { lastVisibleIndex ->
                                    if (lastVisibleIndex == historyItems.lastIndex && historyState.error == null) {
                                        viewModel.loadNextItems()
                                    }
                                }
                        }
                        LazyColumn(
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp)
                        ) {
                            items(items = history, key = { it.id }) { delivery ->
                                DeliveryCard(
                                    navigateToDeliveryDetails = {
                                        navigateToDeliveryDetails(
                                            delivery.id,
                                            delivery.requestNumber
                                        )
                                        deliveryDetailsViewModel.fetchDeliveryDetails(delivery.id)
                                    },
                                    onContinueDeliveryClick = {
                                        navigateToDeliveryRoute(
                                            delivery.id,
                                            delivery.requestNumber
                                        )
                                        deliveryDetailsViewModel.fetchDeliveryDetails(delivery.id)
                                    },
                                    delivery = delivery
                                )
                            }
                            if (historyState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            if (historyState.error != null) {
                                item {
                                    PaginationErrorItem(
                                        message = historyState.error ?: "Ошибка загрузки",
                                        onRetry = viewModel::loadNextItems
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                .padding(horizontal = 18.dp), contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp).alpha(0.7f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "История доставок пуста",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Здесь появятся завершённые и отменённые доставки",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.alpha(0.5f)
                                )
                            }
                        }
                    }
                },
                onError = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorMessage(message = it, onRetry = viewModel::loadNextItems)
                    }
                }
            )
        }
    }
}