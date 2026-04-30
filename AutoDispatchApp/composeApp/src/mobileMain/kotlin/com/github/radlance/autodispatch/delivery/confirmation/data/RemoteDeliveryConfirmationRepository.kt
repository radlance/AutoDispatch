package com.github.radlance.autodispatch.delivery.confirmation.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.createImageFormData
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.delivery.confirmation.domain.DeliveryConfirmationRepository
import com.github.radlance.autodispatch.request.core.domain.DocumentType
import com.github.radlance.autodispatch.delivery.core.data.DeliveryCache
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RemoteDeliveryConfirmationRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest,
    private val cache: DeliveryCache
) : DeliveryConfirmationRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun shipDocuments(
        deliveryId: Int,
        documents: List<ByteArray>
    ): FetchResult<Unit, String> = handleRequest.handle {
        apiService.shipDocuments(deliveryId, documents.createImageFormData())
    }.also { result ->
        if (result is FetchResult.Success) {
            cache.update(deliveryId) {
                it.copy(
                    status = RequestStatus.OnCheck,
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
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
                        status = RequestStatus.OnCheck,
                        updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                }
            }
        }


    @OptIn(ExperimentalTime::class)
    override suspend fun retakeDocument(
        deliveryId: Int,
        documents: List<ByteArray>,
        type: DocumentType
    ): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.retakeDocument(deliveryId, documents.createImageFormData(), type)
        }.also { result ->
            if (result is FetchResult.Success) {
                cache.update(deliveryId) {
                    it.copy(
                        status = RequestStatus.OnCheck,
                        updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                }
            }
        }
}