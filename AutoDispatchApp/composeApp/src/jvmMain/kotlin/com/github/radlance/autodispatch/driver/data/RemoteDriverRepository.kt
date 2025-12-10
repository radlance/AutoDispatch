package com.github.radlance.autodispatch.driver.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toPaginatedResultDriver
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.driver.domain.Driver
import com.github.radlance.autodispatch.driver.domain.DriverRepository
import com.github.radlance.autodispatch.request.core.domain.PaginatedResult

class RemoteDriverRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverRepository {
    override suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<PaginatedResult<Driver>, String> = handleRequest.handle {
        apiService.drivers(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery
        ).toPaginatedResultDriver()
    }
}