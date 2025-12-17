package com.github.radlance.autodispatch.delivery.core.presentation

import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.PaginatedViewModel
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository

class DeliveryViewModel(
    private val repository: DeliveryRepository
) : PaginatedViewModel<Delivery, ListPaginatedResult<Delivery>>(
    pageSize = 5
) {

    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ) = repository.deliveries(query, page, pageSize)

    override fun getItems(result: ListPaginatedResult<Delivery>) =
        result.items

    override fun hasMore(result: ListPaginatedResult<Delivery>) =
        result.hasMore
}