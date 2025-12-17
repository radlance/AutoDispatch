package com.github.radlance.autodispatch.common.presentation

import com.github.radlance.autodispatch.common.domain.FetchResult
import kotlinx.coroutines.coroutineScope

class Paginator<Key, Item, Error>(
    private val initialKey: Key,
    private val onInitialLoad: (Boolean) -> Unit,
    private val onLoadMore: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> FetchResult<Item, Error>,
    private val getNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (Error) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Item) -> Boolean
) {

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() = coroutineScope {
        if (isMakingRequest || isEndReached) return@coroutineScope

        isMakingRequest = true
        val isInitial = currentKey == initialKey

        if (isInitial) {
            onInitialLoad(true)
        } else {
            onLoadMore(true)
        }

        val result = onRequest(currentKey)
        isMakingRequest = false

        result.fold(
            onSuccess = { item ->
                currentKey = getNextKey(currentKey, item)
                onSuccess(item, currentKey)
                isEndReached = endReached(currentKey, item)

                if (isInitial) {
                    onInitialLoad(false)
                } else {
                    onLoadMore(false)
                }
            },
            onError = {
                onError(it)
            }
        )
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }

    suspend fun refresh() {
        reset()
        loadNextItems()
    }
}
