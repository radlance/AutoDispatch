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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
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
    var selectedRequestNumber by rememberSaveable { mutableStateOf("") }
    var showRequestDetailsPanel by rememberSaveable { mutableStateOf(false) }
    var showSearchFilters by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    var selectedDepartureCities by remember { mutableStateOf(listOf<String>()) }
    var selectedDestinationCities by remember { mutableStateOf(listOf<String>()) }
    var selectedCargoTypes by remember { mutableStateOf(listOf<String>()) }
    var selectedStatuses by remember { mutableStateOf(listOf<String>()) }
    var selectedDrivers by remember { mutableStateOf(listOf<String>()) }
    var selectedVehicles by remember { mutableStateOf(listOf<String>()) }

    val requestsUiState by viewModel.requestScreenState.collectAsState()
    val pagingState = rememberPaginatedDataTableState(10)
    val dataTableState = remember(pagingState.pageSize, pagingState.pageIndex) { DataTableState() }
    val scope = rememberCoroutineScope()

    Row(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.weight(1f)) {
            requestsUiState.filters.Reduce(
                onLoading = {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                },
                onSuccess = { request ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        SearchField(
                            query = query,
                            onQueryChange = { query = it },
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
                        Button(onClick = {}) {
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
                            cities = request.cities,
                            filterCargoTypes = request.cargoTypes,
                            filterStatuses = request.statuses,
                            filterDrivers = request.drivers,
                            filterVehicles = request.vehicles,
                            onDepartureCitiesChanged = { selectedDepartureCities = it },
                            onDestinationCitiesChanged = { selectedDestinationCities = it },
                            onCargoTypesChanged = { selectedCargoTypes = it },
                            onStatusesChanged = { selectedStatuses = it },
                            onDriversChanged = { selectedDrivers = it },
                            onVehiclesChanged = { selectedVehicles = it },
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
                },
                onError = {
                    ErrorMessage(
                        message = it,
                        onRetry = {
                            viewModel.loadAllInformation()
                            if (loadProfileUiState is FetchResultUiState.Error) {
                                onReloadProfile()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )

            requestsUiState.requestsResultState.Reduce(
                onSuccess = { request ->
                    val filteredRequests = request.items.filter { req ->
                        val matchesQuery = query.isBlank() || listOfNotNull(
                            req.requestNumber,
                            req.origin,
                            req.destination,
                            req.cargoTypeName,
                            req.createdAt.toString(),
                            req.driverFullName,
                            req.vehicleInfo
                        ).any { it.contains(query, ignoreCase = true) }

                        val matchesDeparture =
                            selectedDepartureCities.isEmpty() || req.origin in selectedDepartureCities
                        val matchesDestination =
                            selectedDestinationCities.isEmpty() || req.destination in selectedDestinationCities
                        val matchesCargo =
                            selectedCargoTypes.isEmpty() || req.cargoTypeName in selectedCargoTypes
                        val matchesStatus =
                            selectedStatuses.isEmpty() || req.statusName in selectedStatuses
                        val matchesDriver =
                            selectedDrivers.isEmpty() || req.driverFullName in selectedDrivers
                        val matchesVehicle =
                            selectedVehicles.isEmpty() || req.vehicleInfo in selectedVehicles

                        matchesQuery && matchesDeparture && matchesDestination &&
                                matchesCargo && matchesStatus && matchesDriver && matchesVehicle
                    }

                    if (filteredRequests.isEmpty()) {
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
                                    requests = filteredRequests,
                                    onRequestClick = {
                                        selectedRequestNumber = it.requestNumber!!
                                        showRequestDetailsPanel = !showRequestDetailsPanel
                                    },
                                    dataTableState = dataTableState,
                                    state = pagingState
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

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val start = min(pagingState.pageIndex * pagingState.pageSize + 1, pagingState.count)
                                val end = min(start + pagingState.pageSize - 1, pagingState.count)
                                val pageCount = (pagingState.count + pagingState.pageSize - 1) / pagingState.pageSize
                                Text("$start-$end из ${pagingState.count}")
                                IconButton(
                                    onClick = { pagingState.pageIndex = 0 },
                                    enabled = pagingState.pageIndex > 0
                                ) { Icon(Icons.Outlined.FirstPage, null) }
                                IconButton(
                                    onClick = { pagingState.pageIndex-- },
                                    enabled = pagingState.pageIndex > 0
                                ) { Icon(Icons.Default.ChevronLeft, null) }
                                IconButton(
                                    onClick = {
                                        pagingState.pageIndex++
                                        viewModel.loadRequests(page = pagingState.pageIndex + 1)
                                    },
                                    enabled = pagingState.pageIndex < pageCount - 1
                                ) { Icon(Icons.Default.ChevronRight, null) }
                                IconButton(
                                    onClick = { pagingState.pageIndex = pageCount - 1 },
                                    enabled = pagingState.pageIndex < pageCount - 1
                                ) { Icon(Icons.AutoMirrored.Default.LastPage, null) }
                            }
                        }
                    }
                },
                onLoading = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = showRequestDetailsPanel,
            enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .padding(8.dp)
            ) {
                Text("Детали запроса: $selectedRequestNumber")
            }
        }
    }
}
