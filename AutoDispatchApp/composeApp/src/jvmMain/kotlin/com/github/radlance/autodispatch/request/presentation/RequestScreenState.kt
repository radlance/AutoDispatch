package com.github.radlance.autodispatch.request.presentation

import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.request.domain.Filters
import com.github.radlance.autodispatch.request.domain.PaginatedResult
import com.github.radlance.autodispatch.request.domain.Request

data class RequestScreenState(
    val filters: FetchResultUiState<Filters, String> = FetchResultUiState.Idle,
    val requestsResultState: FetchResultUiState<PaginatedResult<Request>, String> = FetchResultUiState.Idle
)
