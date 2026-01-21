package com.github.radlance.autodispatch.delivery.core.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDeliveryListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import kotlinx.coroutines.flow.Flow

class RemoteDeliveryRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest,
    private val cache: DeliveryCache
) : DeliveryRepository {

    override suspend fun deliveries(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<Delivery>, String> =
        handleRequest.handle {
            apiService
                .deliveries(searchQuery, page, pageSize)
                .toDeliveryListPaginatedResult()
                .also { result ->
                    cache.putAll(result.items)
                }
        }

    override fun deliveriesFlow(): Flow<Map<Int, Delivery>> =
        cache.items
}
