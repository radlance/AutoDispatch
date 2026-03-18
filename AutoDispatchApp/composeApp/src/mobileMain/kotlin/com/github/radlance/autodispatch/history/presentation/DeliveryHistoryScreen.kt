package com.github.radlance.autodispatch.history.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.EmptyHistoryPlaceholder
import com.github.radlance.autodispatch.common.presentation.EmptySearchPlaceholder
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.PaginationErrorItem
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryCard
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryCardShimmer
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

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
    val queryFlow by viewModel.queryFlow.collectAsStateWithLifecycle()
    val isSearchVisible by remember {
        derivedStateOf {
            !historyState.isEmptyHistory
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val searchBarOffsetHeightPx = remember { Animatable(0f) }
    val searchBarHeightPx = with(density) { 72.dp.toPx() }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val currentOffset = searchBarOffsetHeightPx.value

                val newOffset = (currentOffset + delta).coerceIn(-searchBarHeightPx, 0f)

                val consumed = newOffset - currentOffset

                coroutineScope.launch {
                    searchBarOffsetHeightPx.snapTo(newOffset)
                }

                return Offset(x = 0f, y = consumed)
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                val targetValue = if (searchBarOffsetHeightPx.value > -searchBarHeightPx / 2) {
                    0f
                } else {
                    -searchBarHeightPx
                }
                searchBarOffsetHeightPx.animateTo(
                    targetValue = targetValue,
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
                return available
            }
        }
    }
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
            Box(Modifier.fillMaxSize()) {
                val topPadding = with(density) {
                    (searchBarHeightPx + searchBarOffsetHeightPx.value).coerceAtLeast(0f).toDp()
                } + 8.dp
                val contentModifier = if (isSearchVisible) {
                    Modifier
                        .fillMaxSize()
                        .padding(top = topPadding, start = 18.dp, end = 18.dp)
                } else {
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp)
                }

                historyState.itemsState.Reduce(
                    onLoading = {
                        LazyColumn(
                            modifier = contentModifier.nestedScroll(nestedScrollConnection),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            items(5) {
                                DeliveryCardShimmer()
                            }
                        }
                    },
                    onSuccess = { history ->
                        if (history.isNotEmpty()) {
                            val lazyListState = rememberLazyListState()
                            val historyItems =
                                (historyState.itemsState as? FetchResultUiState.Success)?.data.orEmpty()
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
                                modifier = contentModifier.nestedScroll(nestedScrollConnection),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
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
                                Modifier.fillMaxSize()
                                    .padding(top = topPadding)
                                    .verticalScroll(rememberScrollState())
                                    .nestedScroll(nestedScrollConnection)
                                    .padding(horizontal = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (historyState.isEmptyHistory) {
                                    EmptyHistoryPlaceholder()
                                } else EmptySearchPlaceholder()
                            }
                        }
                    },
                    onError = {
                        Box(contentModifier, contentAlignment = Alignment.Center) {
                            ErrorMessage(message = it, onRetry = viewModel::loadNextItems)
                        }
                    }
                )

                if (isSearchVisible) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = searchBarOffsetHeightPx.value.roundToInt()
                                )
                            }
                            .alpha(
                                (searchBarHeightPx + searchBarOffsetHeightPx.value).coerceIn(
                                    0f,
                                    searchBarHeightPx
                                ) / searchBarHeightPx
                            )
                            .height(72.dp)
                            .fillMaxWidth()
                    ) {
                        CustomTextField(
                            value = queryFlow,
                            onValueChange = { viewModel.onQueryChange(it) },
                            placeholder = "Поиск по доставкам",
                            leadingIcon = Icons.Default.Search,
                            labelText = null,
                            height = TextFieldDefaults.MinHeight,
                            searchBarColors = SearchBarDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}