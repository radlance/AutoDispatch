package com.github.radlance.autodispatch.delivery.core.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDelivery
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository

class RemoteDeliveryRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : DeliveryRepository {
    override suspend fun request(): FetchResult<List<Delivery>, String> = handleRequest.handle {
        apiService.deliveries().map { it.toDelivery() }
    }
}