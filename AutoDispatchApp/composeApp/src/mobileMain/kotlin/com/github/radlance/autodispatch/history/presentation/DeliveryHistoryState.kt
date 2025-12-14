package com.github.radlance.autodispatch.history.presentation

import com.github.radlance.autodispatch.common.presentation.FetchResultUiState

data class DeliveryHistoryState<T, E>(
    val itemsState: FetchResultUiState<List<T>, E> = FetchResultUiState.Idle,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)
