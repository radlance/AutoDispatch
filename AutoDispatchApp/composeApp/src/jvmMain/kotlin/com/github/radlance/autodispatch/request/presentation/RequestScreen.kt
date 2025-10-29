package com.github.radlance.autodispatch.request.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.create_request
import autodispatch.composeapp.generated.resources.search_by_requests
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.request.domain.Request
import com.seanproctor.datatable.DataTableState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestViewModel = koinViewModel()
) {
    var showCreationDialog by rememberSaveable { mutableStateOf(false) }
    var showRequestDetailsPanel by rememberSaveable { mutableStateOf(false) }
    var showSearchFilters by rememberSaveable { mutableStateOf(false) }
    var selectedRequest by rememberSaveable { mutableStateOf<Request?>(null) }

    val requestsUiState by viewModel.requestScreenState.collectAsState()
    val pageIndex = requestsUiState.pageIndex
    val pageSize = requestsUiState.pageSize
    val query = requestsUiState.query
    val selectedDepartureCities = requestsUiState.selectedDepartureCities
    val selectedDestinationCities = requestsUiState.selectedDestinationCities
    val selectedCargoTypes = requestsUiState.selectedCargoTypes
    val selectedStatuses = requestsUiState.selectedStatuses
    val selectedDrivers = requestsUiState.selectedDrivers
    val selectedVehicles = requestsUiState.selectedVehicles

    val dataTableState = remember { DataTableState() }
    val scope = rememberCoroutineScope()

    Row(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.weight(1f)) {
            requestsUiState.filters.Reduce(
                onLoading = {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                },
                onSuccess = { filters ->
                    if (showCreationDialog) {
                        RequestCreationDialog(
                            cities = filters.cities,
                            cargoTypes = filters.cargoTypes,
                            onDismiss = {
                                showCreationDialog = false
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CustomTextField(
                            value = query,
                            onValueChange = viewModel::onQueryChanged,
                            placeholder = stringResource(Res.string.search_by_requests),
                            leadingIcon = Icons.Default.Search,
                            labelText = null,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        FilledTonalIconToggleButton(
                            checked = showSearchFilters,
                            onCheckedChange = {
                                if (showSearchFilters) {
                                    scope.launch {
                                        dataTableState.verticalScrollState.scrollTo(0)
                                    }
                                }
                                showSearchFilters = it
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = { showCreationDialog = true }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(Res.string.create_request))
                        }
                    }

                    AnimatedVisibility(
                        visible = showSearchFilters,
                        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                    ) {
                        RequestFilters(
                            selectedDepartureCities = selectedDepartureCities,
                            selectedDestinationCities = selectedDestinationCities,
                            selectedCargoTypes = selectedCargoTypes,
                            selectedStatuses = selectedStatuses,
                            selectedDrivers = selectedDrivers,
                            selectedVehicles = selectedVehicles,
                            cities = filters.cities,
                            filterCargoTypes = filters.cargoTypes,
                            filterStatuses = filters.statuses,
                            filterDrivers = filters.drivers,
                            filterVehicles = filters.vehicles,
                            onDepartureCitiesChanged = viewModel::onDepartureCitiesChanged,
                            onDestinationCitiesChanged = viewModel::onDestinationCitiesChanged,
                            onCargoTypesChanged = viewModel::onCargoTypesChanged,
                            onStatusesChanged = viewModel::onStatusesChanged,
                            onDriversChanged = viewModel::onDriversChanged,
                            onVehiclesChanged = viewModel::onVehiclesChanged,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                        )
                    }

                    requestsUiState.requestsResultState.Reduce(
                        onSuccess = { request ->
                            val requestsToShow = request.items

                            if (requestsToShow.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text("Ничего не найдено", textAlign = TextAlign.Center)
                                    }
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        RequestTable(
                                            requests = run {
                                                selectedRequest?.let { selected ->
                                                    val find =
                                                        requestsToShow.find { r ->
                                                            r.requestNumber == selected.requestNumber
                                                        }
                                                    find?.let {
                                                        if (it != selected) {
                                                            selectedRequest = find
                                                        }
                                                    }
                                                }
                                                requestsToShow
                                            },
                                            onRequestClick = { req ->
                                                showRequestDetailsPanel =
                                                    if (req == selectedRequest) {
                                                        !showRequestDetailsPanel
                                                    } else true

                                                selectedRequest = req
                                            },
                                            dataTableState = dataTableState,
                                            pageIndex = pageIndex,
                                            pageSize = pageSize
                                        )

                                        VerticalScrollbar(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 4.dp, top = 50.dp),
                                            adapter = rememberDataTableScrollbarAdapter(
                                                scrollState = dataTableState.verticalScrollState
                                            )
                                        )
                                    }

                                    val totalCount = request.totalCount.toInt()
                                    val start = min(pageIndex * pageSize + 1, totalCount)
                                    val end = min(start + pageSize - 1, totalCount)
                                    val pageCount = (totalCount + pageSize - 1) / pageSize

                                    BottomPagingBar(
                                        start = start,
                                        end = end,
                                        totalCount = totalCount,
                                        pageIndex = pageIndex,
                                        pageCount = pageCount,
                                        onFirst = { viewModel.onPageIndexChanged(0) },
                                        onPrev = { viewModel.onPageIndexChanged(pageIndex - 1) },
                                        onNext = { viewModel.onPageIndexChanged(pageIndex + 1) },
                                        onLast = { viewModel.onPageIndexChanged(pageCount - 1) }
                                    )
                                }
                            }
                        },
                        onLoading = {
                            val previous = requestsUiState.lastSuccessfulRequests
                            val requestsToShow = previous?.items ?: emptyList()
                            Column(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    RequestTable(
                                        requests = requestsToShow,
                                        onRequestClick = { req ->
                                            showRequestDetailsPanel = if (req == selectedRequest) {
                                                !showRequestDetailsPanel
                                            } else true

                                            selectedRequest = req
                                        },
                                        dataTableState = dataTableState,
                                        pageIndex = pageIndex,
                                        pageSize = pageSize
                                    )

                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }

                                    VerticalScrollbar(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 4.dp, top = 50.dp),
                                        adapter = rememberDataTableScrollbarAdapter(
                                            scrollState = dataTableState.verticalScrollState
                                        )
                                    )
                                }

                                val totalCount = previous?.totalCount?.toInt() ?: 0
                                val start =
                                    if (totalCount > 0) min(
                                        pageIndex * pageSize + 1,
                                        totalCount
                                    ) else 0
                                val end =
                                    if (totalCount > 0) min(start + pageSize - 1, totalCount) else 0
                                val pageCount =
                                    if (totalCount > 0) (totalCount + pageSize - 1) / pageSize else 1

                                BottomPagingBar(
                                    start = start,
                                    end = end,
                                    totalCount = totalCount,
                                    pageIndex = pageIndex,
                                    pageCount = pageCount,
                                    onFirst = {},
                                    onPrev = {},
                                    onNext = {},
                                    onLast = {}
                                )
                            }
                        },
                        onError = { errorMsg ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = errorMsg, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            viewModel.retryLoadRequests()
                                        }
                                    ) {
                                        Text("Повторить")
                                    }
                                }
                            }
                        }
                    )
                },
                onError = {
                    ErrorMessage(
                        message = it,
                        onRetry = {
                            viewModel.loadFilters()
                            if (loadProfileUiState is FetchResultUiState.Error) {
                                onReloadProfile()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )

        }

        AnimatedVisibility(
            visible = showRequestDetailsPanel,
            enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
        ) {
            RequestDetailsPanel(
                onClosePanel = { showRequestDetailsPanel = false },
                request = selectedRequest!!,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(350.dp)
            )
        }
    }
}