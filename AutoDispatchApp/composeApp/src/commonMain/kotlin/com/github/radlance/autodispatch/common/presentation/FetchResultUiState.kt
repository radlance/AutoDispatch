package com.github.radlance.autodispatch.common.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface FetchResultUiState<out D, out E> {

    @Composable
    fun Reduce(
        onLoading: @Composable () -> Unit = {},
        onSuccess: @Composable (D) -> Unit = {},
        onError: @Composable (E) -> Unit = {}
    )

    object Idle : FetchResultUiState<Nothing, Nothing> {
        @Composable
        override fun Reduce(
            onLoading: @Composable () -> Unit,
            onSuccess: @Composable (Nothing) -> Unit,
            onError: @Composable (Nothing) -> Unit
        ) = Unit
    }

    object Loading : FetchResultUiState<Nothing, Nothing> {

        @Composable
        override fun Reduce(
            onLoading: @Composable () -> Unit,
            onSuccess: @Composable (Nothing) -> Unit,
            onError: @Composable (Nothing) -> Unit
        ) = onLoading()
    }

    data class Success<D>(private val data: D) : FetchResultUiState<D, Nothing> {
        @Composable
        override fun Reduce(
            onLoading: @Composable () -> Unit,
            onSuccess: @Composable (D) -> Unit,
            onError: @Composable (Nothing) -> Unit
        ) = onSuccess(data)
    }

    data class Error<E>(private val error: E) : FetchResultUiState<Nothing, E> {
        @Composable
        override fun Reduce(
            onLoading: @Composable () -> Unit,
            onSuccess: @Composable (Nothing) -> Unit,
            onError: @Composable (E) -> Unit
        ) = onError(error)
    }
}