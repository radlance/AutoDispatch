package com.github.radlance.autodispatch.driver.history.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.Paginator
import com.github.radlance.autodispatch.driver.history.domain.DriverHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DriverHistoryViewModel(
    private val repository: DriverHistoryRepository
) : BaseViewModel() {
    private val pageSize = 3

    private val driverHistoryStateMutable = MutableStateFlow(
        DriverHistoryState()
    )
    val driverHistoryState = driverHistoryStateMutable.asStateFlow()

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
            repository.history(
                driverId = driverHistoryState.value.selectedDriverId,
                page = page,
                pageSize = pageSize
            )
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
                    is FetchResultUiState.Success ->
                        FetchResultUiState.Success(s.data + items)

                    else ->
                        FetchResultUiState.Success(items)
                }

                current.copy(
                    paginatorState = current.paginatorState.copy(
                        itemsState = updated,
                        error = null
                    )
                )

            }
        },
        endReached = { _, result ->
            !result.hasMore
        }
    )

    fun open(driverId: Int) {

        paginator.reset()
        driverHistoryStateMutable.update {
            it.copy(selectedDriverId = driverId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }

    fun loadNextItems(driverId: Int) {
        driverHistoryStateMutable.update {
            it.copy(selectedDriverId = driverId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
}