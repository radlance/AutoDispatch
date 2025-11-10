package com.github.radlance.autodispatch.delivery.core.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository
) : BaseViewModel() {

    private val deliveriesStateMutable =
        MutableStateFlow<FetchResultUiState<List<Request>, String>>(FetchResultUiState.Idle)
    var requestsState = deliveriesStateMutable.onStart {
        fetchRequests()
    }.stateInViewModel(initialValue = deliveriesStateMutable.value)

    fun fetchRequests() {
        deliveriesStateMutable.value = FetchResultUiState.Loading
        handle(background = deliveryRepository::request) {
            deliveriesStateMutable.value = it.toUiState()
        }
    }
}