package com.github.radlance.autodispatch.delivery.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.reuqest.core.domain.Request

interface DeliveryRepository {

    suspend fun request(): FetchResult<List<Request>, String>
}