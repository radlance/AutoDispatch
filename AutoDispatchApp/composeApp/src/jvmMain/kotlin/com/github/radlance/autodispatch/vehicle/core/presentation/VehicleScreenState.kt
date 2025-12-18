package com.github.radlance.autodispatch.vehicle.core.presentation

import com.github.radlance.autodispatch.common.domain.TablePaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleDetailed

data class VehicleScreenState(
    val vehicleResultState: FetchResultUiState<TablePaginatedResult<VehicleDetailed>, String> = FetchResultUiState.Loading,
    val query: String = "",
    val pageIndex: Int = 0,
    val pageSize: Int = 15,
    val lastSuccessfulRequest: TablePaginatedResult<VehicleDetailed>? = null,
    val lastAttemptedRequest: LastVehicleRequestParams? = null
)

data class LastVehicleRequestParams(
    val page: Int,
    val pageSize: Int,
    val searchQuery: String?
)