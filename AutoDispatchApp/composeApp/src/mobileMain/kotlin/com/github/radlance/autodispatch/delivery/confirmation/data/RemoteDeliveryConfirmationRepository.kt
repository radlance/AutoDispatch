package com.github.radlance.autodispatch.delivery.confirmation.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.createImageFormData
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.Status
import com.github.radlance.autodispatch.delivery.confirmation.domain.DeliveryConfirmationRepository
import com.github.radlance.autodispatch.delivery.core.data.DeliveryCache

class RemoteDeliveryConfirmationRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest,
    private val cache: DeliveryCache
) : DeliveryConfirmationRepository {
    override suspend fun completeDelivery(
        deliveryId: Int,
        documents: List<ByteArray>
    ): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.completeDelivery(deliveryId, documents.createImageFormData())
        }.also { result ->
            if (result is FetchResult.Success) {
                cache.update(deliveryId) {
                    it.copy(
                        status = Status(id = 6, name = "На проверке")
                    )
                }
            }
        }


    override suspend fun retakeDocument(
        deliveryId: Int,
        documents: List<ByteArray>
    ): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.retakeDocument(deliveryId, documents.createImageFormData())
        }.also { result ->
            if (result is FetchResult.Success) {
                cache.update(deliveryId) {
                    it.copy(
                        status = Status(id = 6, name = "На проверке")
                    )
                }
            }
        }
}