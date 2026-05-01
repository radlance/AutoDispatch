package com.github.radlance.autodispatch.request.core.presentation

import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.request.core.domain.RequestFilters
import com.github.radlance.autodispatch.request.core.domain.Request
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult

data class RequestScreenState(
    val filters: FetchResultUiState<RequestFilters, String> = FetchResultUiState.Loading,
    val requestsResultState: FetchResultUiState<TablePaginatedResult<Request>, String> = FetchResultUiState.Loading,

    val query: String = "",
    val selectedDepartureCities: List<String> = emptyList(),
    val selectedDestinationCities: List<String> = emptyList(),
    val selectedCargoTypes: List<String> = emptyList(),
    val selectedStatuses: List<String> = emptyList(),
    val selectedDrivers: List<String> = emptyList(),
    val selectedVehicles: List<String> = emptyList(),

    val pageIndex: Int = 0,
    val pageSize: Int = 15,

    val lastSuccessfulRequests: TablePaginatedResult<Request>? = null,
    val lastAttemptedRequest: LastRequestParams? = null
)

data class LastRequestParams(
    val page: Int,
    val pageSize: Int,
    val searchQuery: String?,
    val originCityIds: List<Int>,
    val destinationCityIds: List<Int>,
    val cargoTypeIds: List<Int>,
    val statusIds: List<Int>,
    val driverIds: List<Int>,
    val vehicleIds: List<Int>
)