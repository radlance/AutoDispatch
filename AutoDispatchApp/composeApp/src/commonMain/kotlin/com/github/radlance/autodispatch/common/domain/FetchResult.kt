package com.github.radlance.autodispatch.common.domain

interface FetchResult<out D, out E> {

    fun <R> map(mapper: (D) -> R): FetchResult<R, E>

    suspend fun <R> fold(onSuccess: suspend (D) -> R, onError: suspend (E) -> R): R

    data class Success<out D>(private val data: D) : FetchResult<D, Nothing> {
        override fun <R> map(mapper: (D) -> R): FetchResult<R, Nothing> =
            Success(mapper(data))

        override suspend fun <R> fold(
            onSuccess: suspend (D) -> R,
            onError: suspend (Nothing) -> R
        ): R = onSuccess(data)
    }

    data class Error<out E>(private val error: E) : FetchResult<Nothing, E> {
        override fun <R> map(mapper: (Nothing) -> R): FetchResult<R, E> = Error(error)

        override suspend fun <R> fold(
            onSuccess: suspend (Nothing) -> R,
            onError: suspend (E) -> R
        ): R = onError(error)
    }
}
