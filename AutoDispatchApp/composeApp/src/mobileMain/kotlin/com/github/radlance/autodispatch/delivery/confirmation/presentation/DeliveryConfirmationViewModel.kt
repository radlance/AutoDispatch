package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.runtime.mutableStateListOf
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.confirmation.domain.DeliveryConfirmationRepository
import com.github.radlance.autodispatch.request.core.domain.DocumentType
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

    fun acceptDelivery(deliveryId: Int, documents: List<ByteArray>) {
        deliveryStateMutable.value = FetchResultUiState.Loading
        handle(
            background = {
                repository.completeDelivery(deliveryId, documents)
            }
        ) {
            deliveryStateMutable.value = it.toUiState()
        }
    }

    fun retakeDocument(deliveryId: Int, documents: List<ByteArray>, type: DocumentType) {
        deliveryStateMutable.value = FetchResultUiState.Loading
        handle(
            background = { repository.retakeDocument(deliveryId, documents, type) }
        ) {
            deliveryStateMutable.value = it.toUiState()
        }
    }

    fun shipDocuments(deliveryId: Int, documents: List<ByteArray>) {
        deliveryStateMutable.value = FetchResultUiState.Loading
        handle(
            background = { repository.shipDocuments(deliveryId, documents) }
        ) {
            deliveryStateMutable.value = it.toUiState()
        }
    }

    fun resetState() {
        deliveryStateMutable.value = FetchResultUiState.Idle
    }
}