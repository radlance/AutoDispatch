package com.github.radlance.autodispatch.history.presentation

import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.PaginatedViewModel
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.history.domain.DeliveryHistoryRepository

class DeliveryHistoryViewModel(
    private val repository: DeliveryHistoryRepository
) : PaginatedViewModel<Delivery, ListPaginatedResult<Delivery>>(
    pageSize = 5
) {

    override suspend fun request(
        page: Int,
        pageSize: Int
    ) = repository.history(page, pageSize)

    override fun getItems(result: ListPaginatedResult<Delivery>) =
        result.items

    override fun hasMore(result: ListPaginatedResult<Delivery>) =
        result.hasMore
}