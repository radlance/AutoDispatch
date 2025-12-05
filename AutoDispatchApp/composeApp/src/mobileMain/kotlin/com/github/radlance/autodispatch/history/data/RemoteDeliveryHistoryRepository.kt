package com.github.radlance.autodispatch.history.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDelivery
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.history.domain.DeliveryHistoryRepository

class RemoteDeliveryHistoryRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : DeliveryHistoryRepository {

    override suspend fun history(): FetchResult<List<Delivery>, String> = handleRequest.handle {
        apiService.history().map { it.toDelivery() }
    }
}