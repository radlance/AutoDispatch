package com.github.radlance.autodispatch.delivery.details.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.domain.RequestError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeliveryDetailsViewModel(
    private val repository: DeliveryDetailsRepository
) : BaseViewModel() {
    private val deliveryStateMutable =
        MutableStateFlow<FetchResultUiState<DeliveryDetailed, RequestError>>(FetchResultUiState.Idle)
    val deliveryState = deliveryStateMutable.asStateFlow()

    private val acceptDeliveryStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(FetchResultUiState.Idle)
    val acceptDeliveryState = acceptDeliveryStateMutable.asStateFlow()

    private var deliveryJob: Job? = null

    fun fetchDeliveryDetails(deliveryId: Int) {
        deliveryJob?.cancel()
        deliveryStateMutable.value = FetchResultUiState.Loading
        deliveryJob = handle(background = { repository.deliveryDetails(deliveryId) }) {
            deliveryStateMutable.value = it.toUiState()
        }
    }

    fun acceptDelivery(deliveryId: Int) {
        acceptDeliveryStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.acceptDelivery(deliveryId) }) {
            acceptDeliveryStateMutable.value = it.toUiState()
        }
    }

    fun resetAcceptState() {
        acceptDeliveryStateMutable.value = FetchResultUiState.Idle
    }
}