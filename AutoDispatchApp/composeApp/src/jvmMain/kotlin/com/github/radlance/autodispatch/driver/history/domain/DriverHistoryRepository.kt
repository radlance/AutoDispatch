package com.github.radlance.autodispatch.driver.history.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult

interface DriverHistoryRepository {

    suspend fun history(
        driverId: Int,
        page: Int,
        pageSize: Int
    ):  FetchResult<ListPaginatedResult<DriverHistory>, String>
}