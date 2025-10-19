package com.github.radlance.autodispatch.common.presentation

import androidx.compose.runtime.Composable

interface FetchResultUiState<out D, out E> {

    @Composable
    fun Reduce(
        onLoading: () -> Unit = {},
        onSuccess: (D) -> Unit = {},
        onError: (E) -> Unit = {}
    )

    object Idle : FetchResultUiState<Nothing, Nothing> {
        @Composable
        override fun Reduce(
            onLoading: () -> Unit,
            onSuccess: (Nothing) -> Unit,
            onError: (Nothing) -> Unit
        ) = Unit
    }

    object Loading : FetchResultUiState<Nothing, Nothing> {

        @Composable
        override fun Reduce(
            onLoading: () -> Unit,
            onSuccess: (Nothing) -> Unit,
            onError: (Nothing) -> Unit
        ) = onLoading()
    }

    data class Success<D>(private val data: D) : FetchResultUiState<D, Nothing> {
        @Composable
        override fun Reduce(
            onLoading: () -> Unit,
            onSuccess: (D) -> Unit,
            onError: (Nothing) -> Unit
        ) = onSuccess(data)
    }

    data class Error<E>(private val error: E) : FetchResultUiState<Nothing, E> {
        @Composable
        override fun Reduce(
            onLoading: () -> Unit,
            onSuccess: (Nothing) -> Unit,
            onError: (E) -> Unit
        ) = onError(error)
    }
}