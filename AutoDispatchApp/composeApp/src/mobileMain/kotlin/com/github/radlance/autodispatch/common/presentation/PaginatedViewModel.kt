package com.github.radlance.autodispatch.common.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
abstract class PaginatedViewModel<T, R>(
    private val pageSize: Int = 20
) : BaseViewModel() {

    private val queryFlowMutable = MutableStateFlow("")
    val queryFlow = queryFlowMutable.asStateFlow()

    protected val stateMutable =
        MutableStateFlow<PaginatorState<T, String>>(
            PaginatorState(itemsState = FetchResultUiState.Idle)
        )

    val state = stateMutable
        .onStart { loadNextItems() }
        .stateInViewModel(initialValue = stateMutable.value)

    init {
        queryFlowMutable
            .drop(1)
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                paginator.refresh()
            }
            .launchIn(viewModelScope)
    }

    protected abstract suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<R, String>

    protected abstract fun getItems(result: R): List<T>

    protected abstract fun hasMore(result: R): Boolean

    private val paginator = Paginator(
        initialKey = 1,
        onInitialLoad = { isLoading ->
            if (isLoading) {
                stateMutable.update {
                    it.copy(
                        itemsState = FetchResultUiState.Loading,
                        error = null
                    )
                }
            }
        },
        onLoadMore = { isLoading ->
            stateMutable.update {
                it.copy(isLoadingMore = isLoading, error = null)
            }
        },
        onRequest = { page ->
            val searchQuery = queryFlow.value.takeIf { it.isNotBlank() }
            request(searchQuery, page, pageSize)
        },
        getNextKey = { page, _ -> page + 1 },
        onError = { message ->
            stateMutable.update { current ->
                if (current.itemsState is FetchResultUiState.Loading) {
                    current.copy(
                        itemsState = FetchResultUiState.Error(message),
                        isLoadingMore = false
                    )
                } else {
                    current.copy(
                        error = message,
                        isLoadingMore = false
                    )
                }
            }
        },
        onSuccess = { result, _ ->
            val items = getItems(result)

            stateMutable.update { current ->
                val updated = when (val s = current.itemsState) {
                    is FetchResultUiState.Success -> FetchResultUiState.Success(s.data + items)
                    else -> FetchResultUiState.Success(items)
                }

                val historyIsActuallyPresent = items.isNotEmpty() && queryFlow.value.isBlank()

                val newEmptyHistoryValue = if (current.isEmptyHistory) {
                    !historyIsActuallyPresent
                } else {
                    false
                }

                current.copy(
                    itemsState = updated,
                    error = null,
                    isEmptyHistory = newEmptyHistoryValue
                )
            }
        },
        endReached = { _, result ->
            !hasMore(result)
        }
    )

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            stateMutable.update {
                it.copy(itemsState = FetchResultUiState.Loading)
            }
            paginator.refresh()
        }
    }

    fun onQueryChange(query: String) {
        stateMutable.update { it.copy(query = query) }
        queryFlowMutable.value = query
    }
}
