package com.github.radlance.autodispatch.delivery.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {

    suspend fun deliveries(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<Delivery>, String>

    fun deliveriesStream(): Flow<Map<Int, Delivery>>
}