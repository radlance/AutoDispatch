package com.github.radlance.autodispatch.delivery.core.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.PaginatedViewModel
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class DeliveryViewModel(
    private val repository: DeliveryRepository
) : PaginatedViewModel<Delivery, ListPaginatedResult<Delivery>>(
    pageSize = 5
) {

    init {
        repository
            .deliveriesFlow()
            .onEach { updated ->
                stateMutable.update { current ->
                    val success =
                        current.itemsState as? FetchResultUiState.Success
                            ?: return@update current

                    val merged = success.data.map {
                        updated[it.id] ?: it
                    }

                    if (merged != success.data) {
                        current.copy(itemsState = FetchResultUiState.Success(merged))
                    } else current
                }
            }
            .launchIn(viewModelScope)
    }


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