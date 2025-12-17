package com.github.radlance.autodispatch.driver.history.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDriverHistoryListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.driver.history.domain.DriverHistory
import com.github.radlance.autodispatch.driver.history.domain.DriverHistoryRepository

class RemoteDriverHistoryRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverHistoryRepository {
    override suspend fun history(
        driverId: Int,
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverHistory>, String> = handleRequest.handle {
        apiService.driverHistory(
            driverId = driverId,
            searchQuery = searchQuery,
            page = page,
            pageSize = pageSize
        ).toDriverHistoryListPaginatedResult()
    }
}