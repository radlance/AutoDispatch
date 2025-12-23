package com.github.radlance.autodispatch.delivery.confirmation.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.createImageFormData
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.confirmation.domain.DeliveryConfirmationRepository

class RemoteDeliveryConfirmationRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : DeliveryConfirmationRepository {
    override suspend fun completeDelivery(
        deliveryId: Int,
        documents: List<ByteArray>
    ): FetchResult<Unit, String> = handleRequest.handle {
        apiService.completeDelivery(deliveryId, documents.createImageFormData())
    }

    override suspend fun retakeDocument(
        deliveryId: Int,
        documents: List<ByteArray>
    ): FetchResult<Unit, String> = handleRequest.handle {

        apiService.retakeDocument(deliveryId, documents.createImageFormData())
    }
}