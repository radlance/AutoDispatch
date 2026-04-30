package com.github.radlance.autodispatch.delivery.confirmation.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.DocumentType

interface DeliveryConfirmationRepository {

    suspend fun completeDelivery(deliveryId: Int, documents: List<ByteArray>): FetchResult<Unit, String>

    suspend fun shipDocuments(deliveryId: Int, documents: List<ByteArray>): FetchResult<Unit, String>

    suspend fun retakeDocument(
        deliveryId: Int,
        documents: List<ByteArray>,
        type: DocumentType
    ): FetchResult<Unit, String>
}