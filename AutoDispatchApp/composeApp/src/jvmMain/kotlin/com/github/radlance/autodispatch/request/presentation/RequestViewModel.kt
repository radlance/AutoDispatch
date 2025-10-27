package com.github.radlance.autodispatch.request.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.domain.RequestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestViewModel(
    private val requestRepository: RequestRepository
) : BaseViewModel() {

    private val requestScreenStateMutable = MutableStateFlow(RequestScreenState())
    val requestScreenState = requestScreenStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 500L

    init {
        loadFilters()
    }

    fun loadFilters() {
        requestScreenStateMutable.update { it.copy(filters = FetchResultUiState.Loading) }
        handle(
            background = { requestRepository.filters() }
        ) { filtersResult ->
            requestScreenStateMutable.update { state ->
                state.copy(filters = filtersResult.toUiState())
            }
            if (filtersResult is FetchResult.Success) {
                triggerRequestLoad()
            }
        }
    }

    private fun triggerRequestLoad() {
        val state = requestScreenStateMutable.value
        val filters = (state.filters as? FetchResultUiState.Success)?.data ?: return

        val departureIds =
            filters.cities.filter { it.name in state.selectedDepartureCities }
                .map { it.id }
        val destinationIds =
            filters.cities.filter { it.name in state.selectedDestinationCities }
                .map { it.id }
        val cargoTypeIds =
            filters.cargoTypes.filter { it.name in state.selectedCargoTypes }
                .map { it.id }
        val statusIds =
            filters.statuses.filter { it.name in state.selectedStatuses }.map { it.id }
        val driverIds =
            filters.drivers.filter { it.fullName in state.selectedDrivers }.map { it.id }
        val vehicleIds =
            filters.vehicles.filter { it.model in state.selectedVehicles }.map { it.id }
        val searchQuery = state.query.takeIf { it.isNotBlank() }

        val params = LastRequestParams(
            page = state.pageIndex,
            pageSize = state.pageSize,
            searchQuery = searchQuery,
            originCityIds = departureIds,
            destinationCityIds = destinationIds,
            cargoTypeIds = cargoTypeIds,
            statusIds = statusIds,
            driverIds = driverIds,
            vehicleIds = vehicleIds
        )
        requestScreenStateMutable.update { it.copy(lastAttemptedRequest = params) }

        loadRequests(
            page = state.pageIndex + 1,
            pageSize = state.pageSize,
            searchQuery = searchQuery,
            originCityIds = departureIds,
            destinationCityIds = destinationIds,
            cargoTypeIds = cargoTypeIds,
            statusIds = statusIds,
            driverIds = driverIds,
            vehicleIds = vehicleIds
        )
    }

    private fun loadRequests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ) {
        requestScreenStateMutable.update { state ->
            state.copy(requestsResultState = FetchResultUiState.Loading)
        }
        handle(
            background = {
                requestRepository.requests(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery,
                    originCityIds = originCityIds,
                    destinationCityIds = destinationCityIds,
                    cargoTypeIds = cargoTypeIds,
                    statusIds = statusIds,
                    driverIds = driverIds,
                    vehicleIds = vehicleIds
                )
            }
        ) { requests ->
            requestScreenStateMutable.update { state ->
                val uiState = requests.toUiState()
                if (uiState is FetchResultUiState.Success) {
                    state.copy(
                        requestsResultState = uiState,
                        lastSuccessfulRequests = uiState.data
                    )
                } else {
                    state.copy(requestsResultState = uiState)
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        requestScreenStateMutable.update { it.copy(query = query, pageIndex = 0) }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(debounceTime)
            triggerRequestLoad()
        }
    }

    fun onDepartureCitiesChanged(cities: List<String>) {
        requestScreenStateMutable.update {
            it.copy(
                selectedDepartureCities = cities,
                pageIndex = 0
            )
        }
        triggerRequestLoad()
    }

    fun onDestinationCitiesChanged(cities: List<String>) {
        requestScreenStateMutable.update {
            it.copy(
                selectedDestinationCities = cities,
                pageIndex = 0
            )
        }
        triggerRequestLoad()
    }

    fun onCargoTypesChanged(types: List<String>) {
        requestScreenStateMutable.update { it.copy(selectedCargoTypes = types, pageIndex = 0) }
        triggerRequestLoad()
    }

    fun onStatusesChanged(statuses: List<String>) {
        requestScreenStateMutable.update { it.copy(selectedStatuses = statuses, pageIndex = 0) }
        triggerRequestLoad()
    }

    fun onDriversChanged(drivers: List<String>) {
        requestScreenStateMutable.update { it.copy(selectedDrivers = drivers, pageIndex = 0) }
        triggerRequestLoad()
    }

    fun onVehiclesChanged(vehicles: List<String>) {
        requestScreenStateMutable.update { it.copy(selectedVehicles = vehicles, pageIndex = 0) }
        triggerRequestLoad()
    }

    fun onPageIndexChanged(pageIndex: Int) {
        requestScreenStateMutable.update { it.copy(pageIndex = pageIndex) }
        triggerRequestLoad()
    }

    fun retryLoadRequests() {
        val lastParams = requestScreenStateMutable.value.lastAttemptedRequest
        if (lastParams != null) {
            loadRequests(
                page = lastParams.page + 1,
                pageSize = lastParams.pageSize,
                searchQuery = lastParams.searchQuery,
                originCityIds = lastParams.originCityIds,
                destinationCityIds = lastParams.destinationCityIds,
                cargoTypeIds = lastParams.cargoTypeIds,
                statusIds = lastParams.statusIds,
                driverIds = lastParams.driverIds,
                vehicleIds = lastParams.vehicleIds
            )
        } else {
            triggerRequestLoad()
        }
    }
}