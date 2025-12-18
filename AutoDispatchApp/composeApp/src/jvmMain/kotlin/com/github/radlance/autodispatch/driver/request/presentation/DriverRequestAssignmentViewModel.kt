package com.github.radlance.autodispatch.driver.request.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.driver.common.presentation.DriverPaginatedViewModel
import com.github.radlance.autodispatch.driver.request.domain.DriverRequest
import com.github.radlance.autodispatch.driver.request.domain.DriverRequestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverRequestAssignmentViewModel(
    private val repository: DriverRequestRepository
) : DriverPaginatedViewModel<DriverRequest, ListPaginatedResult<DriverRequest>>() {
    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverRequest>, String> = repository.availableRequests(
        searchQuery = query,
        page = page,
        pageSize = pageSize
    )

    override fun getItems(result: ListPaginatedResult<DriverRequest>): List<DriverRequest> {
        return result.items
    }

    override fun hasMore(result: ListPaginatedResult<DriverRequest>): Boolean {
        return result.hasMore
    }

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
}