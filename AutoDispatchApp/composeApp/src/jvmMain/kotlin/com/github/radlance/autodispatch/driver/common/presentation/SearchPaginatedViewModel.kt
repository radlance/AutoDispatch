package com.github.radlance.autodispatch.driver.common.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.Paginator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
abstract class SearchPaginatedViewModel<T>(
    private val pageSize: Int = 10
) : BaseViewModel() {

    protected val stateMutable = MutableStateFlow(
        DriverPaginationState<T, String>()
    )
    val state = stateMutable.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    private fun setupQueryFlow() {
        queryFlow
            .drop(1)
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                paginator.refresh()
            }
            .launchIn(viewModelScope)
    }

    init {
        setupQueryFlow()
    }

    protected abstract suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<T>, String>

    protected val paginator = Paginator(
        initialKey = 1,
        onInitialLoad = { isLoading ->
            if (isLoading) {
                stateMutable.update {
                    it.copy(
                        paginatorState = it.paginatorState.copy(
                            itemsState = FetchResultUiState.Loading,
                            error = null
                        ),
                    )
                }
            }
        },
        onLoadMore = { isLoading ->
            stateMutable.update {
                it.copy(
                    paginatorState = it.paginatorState.copy(
                        isLoadingMore = isLoading,
                        error = null
                    )
                )
            }
        },

        onRequest = { page ->
            val searchQuery = queryFlow.value.takeIf { it.isNotBlank() }
            request(
                query = searchQuery,
                page = page,
                pageSize = pageSize
            )
        },
        getNextKey = { page, _ -> page + 1 },
        onError = { message ->
            stateMutable.update { current ->
                current.copy(
                    paginatorState = if (current.paginatorState.itemsState is FetchResultUiState.Loading) {
                        current.paginatorState.copy(
                            itemsState = FetchResultUiState.Error(message),
                            isLoadingMore = false
                        )
                    } else {
                        current.paginatorState.copy(
                            error = message,
                            isLoadingMore = false
                        )
                    }
                )
            }
        },
        onSuccess = { result, _ ->
            val items = result.items

            stateMutable.update { current ->
                val updated = when (val s = current.paginatorState.itemsState) {
                    is FetchResultUiState.Success -> s.data + items
                    else -> items
                }

                val isInitialEmptyLoad =
                    current.paginatorState.itemsState !is FetchResultUiState.Success
                            && items.isEmpty()
                            && current.query.isBlank()

                current.copy(
                    paginatorState = current.paginatorState.copy(
                        itemsState = FetchResultUiState.Success(updated),
                        error = null
                    ),
                    isEmptyResult = if (current.paginatorState.itemsState !is FetchResultUiState.Success)
                        isInitialEmptyLoad
                    else current.isEmptyResult
                )
            }
        },
        endReached = { _, result ->
            !result.hasMore
        }
    )

    open fun resetState() {
        viewModelScope.coroutineContext.cancelChildren()
        queryFlow.value = ""
        stateMutable.update {
            it.copy(
                paginatorState = it.paginatorState.copy(itemsState = FetchResultUiState.Loading),
                query = "",
                isEmptyResult = true
            )
        }
        paginator.reset()
        setupQueryFlow()
    }

    fun onQueryChange(query: String) {
        stateMutable.update { it.copy(query = query) }
        queryFlow.value = query
    }
}