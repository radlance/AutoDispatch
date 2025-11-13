package com.github.radlance.autodispatch.delivery.details.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface DeliveryDetailsRepository {

    suspend fun deliveryDetails(deliveryId: Int): FetchResult<DeliveryDetailed, DeliveryError>

    suspend fun acceptDelivery(deliveryId: Int): FetchResult<Unit, DeliveryError>
}