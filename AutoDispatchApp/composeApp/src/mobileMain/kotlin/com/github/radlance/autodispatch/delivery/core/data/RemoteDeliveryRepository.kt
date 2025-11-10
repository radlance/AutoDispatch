package com.github.radlance.autodispatch.delivery.core.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toRequest
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import com.github.radlance.autodispatch.reuqest.core.domain.Request

class RemoteDeliveryRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : DeliveryRepository {
    override suspend fun request(): FetchResult<List<Request>, String> = handleRequest.handle {
        apiService.myRequests().map { it.toRequest() }
    }
}