package com.github.radlance.autodispatch.common.presentation

import com.github.radlance.autodispatch.common.domain.FetchResult

suspend fun <D, E> FetchResult<D, E>.toUiState(): FetchResultUiState<D, E> {
    var result: FetchResultUiState<D, E> = FetchResultUiState.Loading

    this.fold(
        onSuccess = { data ->
            result = FetchResultUiState.Success(data)
        },
        onError = { error ->
            result = FetchResultUiState.Error(error)
        }
    )

    return result
}