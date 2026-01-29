package com.github.radlance.autodispatch.delivery.core.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.deliveries
import autodispatch.composeapp.generated.resources.no_deliveries_yet
import autodispatch.composeapp.generated.resources.when_deliveries_appear
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.EmptySearchPlaceholder
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.PaginationErrorItem
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryScreen(
    navigateToDeliveryDetails: (Int, String) -> Unit,
    navigateToDeliveryRoute: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    deliveryViewModel: DeliveryViewModel = koinViewModel(),
    deliveryDetailsViewModel: DeliveryDetailsViewModel = koinViewModel()
) {
    val deliveriesState by deliveryViewModel.state.collectAsStateWithLifecycle()
    val queryFlow by deliveryViewModel.queryFlow.collectAsStateWithLifecycle()
    val isSearchVisible by remember {
        derivedStateOf {
            !deliveriesState.isEmptyHistory
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
                val newOffset = searchBarOffsetHeightPx.value + delta
                coroutineScope.launch {
                    searchBarOffsetHeightPx.snapTo(newOffset.coerceIn(-searchBarHeightPx, 0f))
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val targetValue = if (searchBarOffsetHeightPx.value > -searchBarHeightPx / 2) {
                    0f
                } else {
                    -searchBarHeightPx
                }
                searchBarOffsetHeightPx.animateTo(
                    targetValue = targetValue,
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
                return super.onPostFling(consumed, available)
            }
        }
    }
    Scaffold(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.deliveries))
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = deliveriesState.itemsState is FetchResultUiState.Loading,
            onRefresh = deliveryViewModel::refresh
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

                deliveriesState.itemsState.Reduce(
                    onLoading = {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 12.dp),
                            modifier = contentModifier,
                        ) {
                            items(5) {
                                DeliveryCardShimmer()
                            }
                        }
                    },
                    onSuccess = { deliveries ->
                        if (deliveries.isNotEmpty()) {
                            val lazyListState = rememberLazyListState()
                            val deliveriesItems =
                                (deliveriesState.itemsState as? FetchResultUiState.Success)?.data.orEmpty()

                            LaunchedEffect(lazyListState, deliveriesItems.size) {
                                if (deliveriesItems.isEmpty()) return@LaunchedEffect
                                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .distinctUntilChanged()
                                    .collect { lastVisibleIndex ->
                                        if (lastVisibleIndex == deliveriesItems.lastIndex && deliveriesState.error == null) {
                                            deliveryViewModel.loadNextItems()
                                        }
                                    }
                            }
                            LazyColumn(
                                state = lazyListState,
                                modifier = contentModifier,
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(items = deliveries, key = { it.id }) { delivery ->
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
                                if (deliveriesState.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }

                                if (deliveriesState.error != null) {
                                    item {
                                        PaginationErrorItem(
                                            message = deliveriesState.error ?: "Ошибка загрузки",
                                            onRetry = deliveryViewModel::loadNextItems
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                    .padding(horizontal = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (deliveriesState.isEmptyHistory) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Inbox,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp).alpha(0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = stringResource(Res.string.no_deliveries_yet),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.alpha(0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(Res.string.when_deliveries_appear),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.alpha(0.5f)
                                        )
                                    }
                                } else EmptySearchPlaceholder()
                            }
                        }
                    },
                    onError = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ErrorMessage(message = it, onRetry = deliveryViewModel::loadNextItems)
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
                            onValueChange = { deliveryViewModel.onQueryChange(it) },
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