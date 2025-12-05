package com.github.radlance.autodispatch.history.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.history.domain.DeliveryHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class DeliveryHistoryViewModel(
    private val repository: DeliveryHistoryRepository
) : BaseViewModel() {

    private val historyStateMutable =
        MutableStateFlow<FetchResultUiState<List<Delivery>, String>>(FetchResultUiState.Idle)
    val historyState = historyStateMutable.onStart {
        fetchHistory()
    }.stateInViewModel(initialValue = historyStateMutable.value)

    fun fetchHistory() {
        historyStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::history) {
            historyStateMutable.value = it.toUiState()
        }
    }
}