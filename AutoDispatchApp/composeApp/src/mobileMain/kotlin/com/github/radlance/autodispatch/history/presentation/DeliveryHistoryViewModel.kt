package com.github.radlance.autodispatch.history.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.history.domain.DeliveryHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeliveryHistoryViewModel(
    private val repository: DeliveryHistoryRepository
) : BaseViewModel() {

    private val historyStateMutable =
        MutableStateFlow<DeliveryHistoryState<Delivery, String>>(
            DeliveryHistoryState()
        )
    val historyState = historyStateMutable.onStart {
        loadNextItems()
    }.stateInViewModel(initialValue = historyStateMutable.value)

    private val pageSize = 5
    private val paginator = Paginator(
        initialKey = 1,
        onInitialLoad = { isLoading ->
            historyStateMutable.update { currentState ->
                if (isLoading) {
                    currentState.copy(
                        itemsState = FetchResultUiState.Loading,
                        error = null
                    )
                } else {
                    currentState
                }
            }
        },
        onLoadMore = { isLoading ->
            historyStateMutable.update {
                it.copy(isLoadingMore = isLoading, error = null)
            }
        },
        onRequest = { currentPage ->
            repository.history(
                page = currentPage,
                pageSize = pageSize
            )
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { message ->
            historyStateMutable.update { currentState ->
                if (currentState.itemsState is FetchResultUiState.Loading) {
                    currentState.copy(
                        itemsState = FetchResultUiState.Error(message),
                        isLoadingMore = false
                    )
                } else {
                    currentState.copy(
                        error = message,
                        isLoadingMore = false
                    )
                }
            }
        },
        onSuccess = { result, _ ->
            val itemsState = historyStateMutable.value.itemsState
            val newItems = result.items

            val updatedItemsState = if (itemsState is FetchResultUiState.Success) {
                FetchResultUiState.Success(itemsState.data + newItems)
            } else {
                FetchResultUiState.Success(newItems)
            }
            historyStateMutable.update {
                it.copy(
                    itemsState = updatedItemsState,
                    error = null
                )
            }

        },
        endReached = { _, response ->
            !response.hasMore
        }
    )

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }

    fun refreshHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyStateMutable.update {
                it.copy(itemsState = FetchResultUiState.Loading)
            }
            paginator.refresh()
        }
    }
}