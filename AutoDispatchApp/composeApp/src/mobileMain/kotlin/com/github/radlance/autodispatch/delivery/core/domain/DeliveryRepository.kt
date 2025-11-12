package com.github.radlance.autodispatch.delivery.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface DeliveryRepository {

    suspend fun request(): FetchResult<List<Delivery>, String>
}