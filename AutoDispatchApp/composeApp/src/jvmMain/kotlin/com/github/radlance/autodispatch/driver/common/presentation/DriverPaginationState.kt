package com.github.radlance.autodispatch.driver.common.presentation

import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.PaginatorState

data class DriverPaginationState<R, E>(
    val selectedDriverId: Int = -1,
    val query: String = "",
    val paginatorState: PaginatorState<R, E> = PaginatorState(
        FetchResultUiState.Loading
    ),
    val isEmptyResult: Boolean = true
)