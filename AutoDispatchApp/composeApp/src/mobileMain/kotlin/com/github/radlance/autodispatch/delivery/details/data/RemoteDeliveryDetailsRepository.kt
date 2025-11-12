package com.github.radlance.autodispatch.delivery.details.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDeliveryDetailed
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository

class RemoteDeliveryDetailsRepository(
    private val handleRequest: HandleRequest,
    private val apiService: ApiServiceMobile
) : DeliveryDetailsRepository {

    override suspend fun deliveryDetails(deliveryId: Int): FetchResult<DeliveryDetailed, String> =
        handleRequest.handle {
            apiService.deliveryDetails(deliveryId).toDeliveryDetailed()
        }
}