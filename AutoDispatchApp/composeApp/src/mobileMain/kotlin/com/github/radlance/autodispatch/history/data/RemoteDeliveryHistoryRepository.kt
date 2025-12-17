package com.github.radlance.autodispatch.history.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDeliveryListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.history.domain.DeliveryHistoryRepository

class RemoteDeliveryHistoryRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : DeliveryHistoryRepository {

    override suspend fun history(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<Delivery>, String> = handleRequest.handle {
        apiService.history(searchQuery, page, pageSize).toDeliveryListPaginatedResult()
    }
}