package com.github.radlance.autodispatch.driver.history.presentation

import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.PaginatorState
import com.github.radlance.autodispatch.driver.history.domain.DriverHistory

data class DriverHistoryState(
    val selectedDriverId: Int = -1,
    val query: String = "",
    val paginatorState: PaginatorState<DriverHistory, String> = PaginatorState(
        FetchResultUiState.Loading
    )
)
