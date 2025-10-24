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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.create_request
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
    var selectedDepartureCity by remember { mutableStateOf("") }
    var selectedDestinationCity by remember { mutableStateOf("") }
    var selectedCargoType by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("") }
    var selectedDriver by remember { mutableStateOf("") }
    var selectedVehicle by remember { mutableStateOf("") }

    val requestsUiState by viewModel.loadRequestUiState.collectAsState()

    requestsUiState.Reduce(
        onLoading = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        },
        onSuccess = { request ->
            val filterDepartureCities = listOf("Все города")
            val filterDestinationCities = listOf("Все города")
            val filterCargoTypes = listOf("Все типы") + request.cargoTypes.map { it.name }
            val filterStatuses = listOf("Все статусы")
            val filterDrivers = listOf("Все водители")
            val filterVehicles = listOf("Все автомобили")

            Column(modifier = modifier.fillMaxSize()) {
                if (selectedDepartureCity.isEmpty()) {
                    selectedDepartureCity = filterDepartureCities.first()
                }
                if (selectedDestinationCity.isEmpty()) {
                    selectedDestinationCity = filterDestinationCities.first()
                }
                if (selectedCargoType.isEmpty()) {
                    selectedCargoType = filterCargoTypes.first()
                }
                if (selectedStatus.isEmpty()) {
                    selectedStatus = filterStatuses.first()
                }
                if (selectedDriver.isEmpty()) {
                    selectedDriver = filterDrivers.first()
                }
                if (selectedVehicle.isEmpty()) {
                    selectedVehicle = filterVehicles.first()
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
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
                            modifier = Modifier
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
                    Spacer(Modifier.height(4.dp))
                }

                AnimatedVisibility(
                    visible = showSearchFilters,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    RequestFilters(
                        selectedDepartureCity = selectedDepartureCity,
                        selectedDestinationCity = selectedDestinationCity,
                        selectedCargoType = selectedCargoType,
                        selectedStatus = selectedStatus,
                        selectedDriver = selectedDriver,
                        selectedVehicle = selectedVehicle,
                        filterDepartureCities = filterDepartureCities,
                        filterDestinationCities = filterDestinationCities,
                        filterCargoTypes = filterCargoTypes,
                        filterStatuses = filterStatuses,
                        filterDrivers = filterDrivers,
                        filterVehicles = filterVehicles,
                        onDepartureCitySelected = { selectedDepartureCity = it },
                        onDestinationCitySelected = { selectedDestinationCity = it },
                        onCargoTypeSelected = { selectedCargoType = it },
                        onStatusSelected = { selectedStatus = it },
                        onDriverSelected = { selectedDriver = it },
                        onVehicleSelected = { selectedVehicle = it },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                RequestTable(request.requests)
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

