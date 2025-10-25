package com.github.radlance.autodispatch.request.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.create_request
import autodispatch.composeapp.generated.resources.no_results_filters
import autodispatch.composeapp.generated.resources.no_results_generic
import autodispatch.composeapp.generated.resources.no_results_query
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestViewModel = koinViewModel()
) {
    var showSearchFilters by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    var selectedDepartureCities by remember { mutableStateOf(listOf<String>()) }
    var selectedDestinationCities by remember { mutableStateOf(listOf<String>()) }
    var selectedCargoTypes by remember { mutableStateOf(listOf<String>()) }
    var selectedStatuses by remember { mutableStateOf(listOf<String>()) }
    var selectedDrivers by remember { mutableStateOf(listOf<String>()) }
    var selectedVehicles by remember { mutableStateOf(listOf<String>()) }

    val requestsUiState by viewModel.loadRequestUiState.collectAsState()

    requestsUiState.Reduce(
        onLoading = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        },
        onSuccess = { request ->
            val filteredRequests = request.requests.filter { req ->
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
                val matchesStatus = selectedStatuses.isEmpty() || req.statusName in selectedStatuses
                val matchesDriver =
                    selectedDrivers.isEmpty() || req.driverFullName in selectedDrivers
                val matchesVehicle =
                    selectedVehicles.isEmpty() || req.vehicleInfo in selectedVehicles

                matchesQuery && matchesDeparture && matchesDestination &&
                        matchesCargo && matchesStatus && matchesDriver && matchesVehicle
            }

            Column(modifier = modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth()) {
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
                            onCheckedChange = { showSearchFilters = it },
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
                        filterDepartureCities = request.departureCities,
                        filterDestinationCities = request.destinationCities,
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
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }


                if (filteredRequests.isEmpty()) {
                    val maxLength = 100
                    val displayQuery =
                        if (query.length > maxLength) query.take(maxLength) + "…" else query

                    val message = when {
                        query.isNotBlank() -> stringResource(
                            Res.string.no_results_query,
                            displayQuery
                        )

                        selectedDepartureCities.isNotEmpty() ||
                                selectedDestinationCities.isNotEmpty() ||
                                selectedCargoTypes.isNotEmpty() ||
                                selectedStatuses.isNotEmpty() ||
                                selectedDrivers.isNotEmpty() ||
                                selectedVehicles.isNotEmpty() -> stringResource(Res.string.no_results_filters)

                        else -> stringResource(Res.string.no_results_generic)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = buildAnnotatedString {
                                    append(message)
                                },
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 3
                            )
                        }
                    }
                } else {
                    RequestTable(filteredRequests)
                }
            }
        },
        onError = {
            ErrorMessage(
                message = it,
                onRetry = {
                    viewModel.loadRequests()
                    if (loadProfileUiState is FetchResultUiState.Error) {
                        onReloadProfile()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}