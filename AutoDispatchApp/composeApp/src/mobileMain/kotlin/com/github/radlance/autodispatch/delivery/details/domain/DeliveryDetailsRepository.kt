package com.github.radlance.autodispatch.delivery.details.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.RequestError

interface DeliveryDetailsRepository {

    suspend fun deliveryDetails(deliveryId: Int): FetchResult<DeliveryDetailed, RequestError>

    suspend fun acceptDelivery(deliveryId: Int): FetchResult<Unit, RequestError>
}