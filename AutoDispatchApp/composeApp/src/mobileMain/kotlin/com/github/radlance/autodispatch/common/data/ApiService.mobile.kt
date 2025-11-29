package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.delivery.core.data.DeliveryDto
import com.github.radlance.autodispatch.delivery.details.data.DeliveryDetailedDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.content.PartData

interface ApiServiceMobile : ApiService {

    suspend fun deliveries(): List<DeliveryDto>

    suspend fun deliveryDetails(deliveryId: Int): DeliveryDetailedDto

    suspend fun startDelivery(deliveryId: Int)

    suspend fun completeDelivery(deliveryId: Int, formData: List<PartData>)
}

internal class KtorApiServiceMobile(
    private val httpClient: HttpClient,
    private val apiService: ApiService
) : ApiServiceMobile, ApiService by apiService {

    override suspend fun deliveries(): List<DeliveryDto> {
        return httpClient.get("deliveries").body()
    }

    override suspend fun deliveryDetails(deliveryId: Int): DeliveryDetailedDto {
        return httpClient.get("deliveries/${deliveryId}/details").body()
    }

    override suspend fun startDelivery(deliveryId: Int) {
        httpClient.post("deliveries/${deliveryId}/start")
    }

    override suspend fun completeDelivery(
        deliveryId: Int,
        formData: List<PartData>
    ) {

        httpClient.submitFormWithBinaryData(
            url = "deliveries/$deliveryId/complete",
            formData = formData
        )
    }
}