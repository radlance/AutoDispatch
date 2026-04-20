package com.github.radlance.autodispatch.driver.core.presentation

import com.github.radlance.autodispatch.common.domain.TablePaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.driver.core.domain.Driver

data class DriverScreenState(
    val driversResultState: FetchResultUiState<TablePaginatedResult<Driver>, String> = FetchResultUiState.Loading,
    val query: String = "",
    val pageIndex: Int = 0,
    val pageSize: Int = 15,
    val lastSuccessfulRequest: TablePaginatedResult<Driver>? = null,
    val lastAttemptedRequest: LastDriverRequestParams? = null
)

data class LastDriverRequestParams(
    val page: Int,
    val pageSize: Int,
    val searchQuery: String?
)