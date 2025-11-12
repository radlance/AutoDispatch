package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.delivery.core.data.DeliveryDto
import com.github.radlance.autodispatch.delivery.details.data.DeliveryDetailedDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ApiServiceMobile : ApiService {

    suspend fun deliveries(): List<DeliveryDto>

    suspend fun deliveryDetails(deliveryId: Int): DeliveryDetailedDto
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
}