package com.github.radlance.autodispatch.delivery.details.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeliveryDetailsViewModel(
    private val repository: DeliveryDetailsRepository
) : BaseViewModel() {
    private val deliveryStateMutable =
        MutableStateFlow<FetchResultUiState<DeliveryDetailed, String>>(FetchResultUiState.Idle)
    val deliveryState = deliveryStateMutable.asStateFlow()

    private var deliveryJob: Job? = null

    fun fetchDeliveryDetails(deliveryId: Int) {
        deliveryJob?.cancel()
        deliveryStateMutable.value = FetchResultUiState.Loading
        deliveryJob = handle(background = { delay(3000); repository.deliveryDetails(deliveryId) }) {
            deliveryStateMutable.value = it.toUiState()
        }
    }
}