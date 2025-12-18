package com.github.radlance.autodispatch.driver.request.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult

interface DriverRequestRepository {

    suspend fun availableRequests(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverRequest>, String>
}