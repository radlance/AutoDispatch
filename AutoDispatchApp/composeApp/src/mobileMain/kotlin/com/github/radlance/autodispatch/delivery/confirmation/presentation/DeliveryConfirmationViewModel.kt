package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.runtime.mutableStateListOf
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.confirmation.domain.DeliveryConfirmationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeliveryConfirmationViewModel(
    private val repository: DeliveryConfirmationRepository
) : BaseViewModel() {
    val documents = mutableStateListOf<ByteArray>()

    private val completeDeliveryStateMutable = MutableStateFlow<FetchResultUiState<Unit, String>>(
        FetchResultUiState.Idle
    )
    val completeDeliveryState = completeDeliveryStateMutable.asStateFlow()

    fun completeDelivery(deliveryId: Int, documents: List<ByteArray>) {
        completeDeliveryStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.completeDelivery(deliveryId, documents) }) {
            completeDeliveryStateMutable.value = it.toUiState()
        }
    }

    fun resetState() {
        completeDeliveryStateMutable.value = FetchResultUiState.Idle
    }
}