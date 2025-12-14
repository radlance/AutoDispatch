package com.github.radlance.autodispatch.history.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.core.domain.Delivery

interface DeliveryHistoryRepository {

    suspend fun history(
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<Delivery>, String>
}