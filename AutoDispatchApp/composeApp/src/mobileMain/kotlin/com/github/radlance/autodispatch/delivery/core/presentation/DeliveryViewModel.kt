package com.github.radlance.autodispatch.delivery.core.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class DeliveryViewModel(
    private val repository: DeliveryRepository
) : BaseViewModel() {

    private val deliveriesStateMutable =
        MutableStateFlow<FetchResultUiState<List<Delivery>, String>>(FetchResultUiState.Idle)
    val deliveriesState = deliveriesStateMutable.onStart {
        fetchDeliveries()
    }.stateInViewModel(initialValue = deliveriesStateMutable.value)

    fun fetchDeliveries() {
        deliveriesStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::deliveries) {
            deliveriesStateMutable.value = it.toUiState()
        }
    }
}