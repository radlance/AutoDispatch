package com.github.radlance.autodispatch.delivery.confirmation.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.confirmation.domain.DeliveryConfirmationRepository
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData

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

private fun List<ByteArray>.createImageFormData(): List<PartData> = formData {
    this@createImageFormData.forEachIndexed { index, bytes ->
        append(
            "file$index",
            bytes,
            Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(
                    HttpHeaders.ContentDisposition,
                    "form-data; name=\"file$index\"; filename=\"photo_$index.jpg\""
                )
            }
        )
    }
}