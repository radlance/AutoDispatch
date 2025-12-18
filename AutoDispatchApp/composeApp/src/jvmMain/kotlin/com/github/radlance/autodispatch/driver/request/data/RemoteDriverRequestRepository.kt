package com.github.radlance.autodispatch.driver.request.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDriverRequestListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.driver.request.domain.DriverRequest
import com.github.radlance.autodispatch.driver.request.domain.DriverRequestRepository

class RemoteDriverRequestRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverRequestRepository {
    override suspend fun availableRequests(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverRequest>, String> = handleRequest.handle {
        apiService.availableRequests(
            searchQuery = searchQuery,
            page = page,
            pageSize = pageSize
        ).toDriverRequestListPaginatedResult()
    }
}