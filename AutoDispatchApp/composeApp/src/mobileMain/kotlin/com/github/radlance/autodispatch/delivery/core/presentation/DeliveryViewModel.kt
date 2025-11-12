package com.github.radlance.autodispatch.delivery.core.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository
) : BaseViewModel() {

    private val deliveriesStateMutable =
        MutableStateFlow<FetchResultUiState<List<Delivery>, String>>(FetchResultUiState.Idle)
    val deliveriesState = deliveriesStateMutable.onStart {
        fetchRequests()
    }.stateInViewModel(initialValue = deliveriesStateMutable.value)

    fun fetchRequests() {
        deliveriesStateMutable.value = FetchResultUiState.Loading
        handle(background = deliveryRepository::request) {
            deliveriesStateMutable.value = it.toUiState()
        }
    }
}