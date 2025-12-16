package com.github.radlance.autodispatch.common.presentation

data class PaginatorState<T, E>(
    val itemsState: FetchResultUiState<List<T>, E> = FetchResultUiState.Idle,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)
