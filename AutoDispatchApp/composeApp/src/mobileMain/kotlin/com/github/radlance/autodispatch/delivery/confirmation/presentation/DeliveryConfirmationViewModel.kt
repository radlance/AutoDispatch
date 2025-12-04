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

    private val deliveryStateMutable = MutableStateFlow<FetchResultUiState<Unit, String>>(
        FetchResultUiState.Idle
    )
    val deliveryState = deliveryStateMutable.asStateFlow()

    fun completeDelivery(deliveryId: Int, documents: List<ByteArray>, retake: Boolean) {
        deliveryStateMutable.value = FetchResultUiState.Loading
        handle(
            background = {
                if (retake) {
                    repository.retakeDocument(deliveryId, documents)
                } else {
                    repository.completeDelivery(deliveryId, documents)
                }
            }
        ) {
            deliveryStateMutable.value = it.toUiState()
        }
    }

    fun resetState() {
        deliveryStateMutable.value = FetchResultUiState.Idle
    }
}