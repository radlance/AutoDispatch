package com.github.radlance.autodispatch.driver.core.presentation

import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.request.core.domain.PaginatedResult

data class DriverScreenState(
    val driversResultState: FetchResultUiState<PaginatedResult<Driver>, String> = FetchResultUiState.Loading,
    val query: String = "",
    val pageIndex: Int = 0,
    val pageSize: Int = 15,
    val lastSuccessfulRequests: PaginatedResult<Driver>? = null,
    val lastAttemptedRequest: LastDriverRequestParams? = null
)

data class LastDriverRequestParams(
    val page: Int,
    val pageSize: Int,
    val searchQuery: String?
)