package com.github.radlance.autodispatch.driver.core.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toPaginatedResultDriver
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.driver.core.domain.DriverRepository
import com.github.radlance.autodispatch.request.core.domain.TablePaginatedResult

class RemoteDriverRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverRepository {
    override suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<TablePaginatedResult<Driver>, String> = handleRequest.handle {
        apiService.drivers(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery
        ).toPaginatedResultDriver()
    }
}