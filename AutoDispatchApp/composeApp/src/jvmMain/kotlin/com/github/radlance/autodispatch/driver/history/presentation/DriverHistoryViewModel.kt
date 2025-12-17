package com.github.radlance.autodispatch.driver.history.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.Paginator
import com.github.radlance.autodispatch.driver.history.domain.DriverHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class DriverHistoryViewModel(
    private val repository: DriverHistoryRepository
) : BaseViewModel() {
    private val pageSize = 3

    private val driverHistoryStateMutable = MutableStateFlow(
        DriverHistoryState()
    )
    val driverHistoryState = driverHistoryStateMutable.asStateFlow()
    private val queryFlow = MutableStateFlow("")

    private val paginator = Paginator(
        initialKey = 1,
        onInitialLoad = { isLoading ->
            if (isLoading) {
                driverHistoryStateMutable.update {
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
            driverHistoryStateMutable.update {
                it.copy(
                    paginatorState = it.paginatorState.copy(
                        isLoadingMore = isLoading,
                        error = null
                    )
                )
            }
        },

        onRequest = { page ->
            with(driverHistoryState.value) {
                val searchQuery = queryFlow.value.takeIf { it.isNotBlank() }
                repository.history(
                    driverId = selectedDriverId,
                    searchQuery = searchQuery,
                    page = page,
                    pageSize = pageSize
                )
            }
        },
        getNextKey = { page, _ -> page + 1 },
        onError = { message ->
            driverHistoryStateMutable.update { current ->
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

            driverHistoryStateMutable.update { current ->
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
                    isEmptyHistory = if (current.paginatorState.itemsState !is FetchResultUiState.Success)
                        isInitialEmptyLoad
                    else current.isEmptyHistory
                )
            }
        },
        endReached = { _, result ->
            !result.hasMore
        }
    )

    init {
        queryFlow
            .drop(1)
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                paginator.refresh()
            }
            .launchIn(viewModelScope)
    }

    fun loadNextItems(driverId: Int) {
        driverHistoryStateMutable.update {
            it.copy(selectedDriverId = driverId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }

    fun resetState() {
        driverHistoryStateMutable.update {
            it.copy(
                paginatorState = it.paginatorState.copy(itemsState = FetchResultUiState.Loading),
                query = "",
                isEmptyHistory = true
            )
        }
        paginator.reset()
    }


    fun onQueryChange(query: String) {
        driverHistoryStateMutable.update { it.copy(query = query) }
        queryFlow.value = query
    }
}