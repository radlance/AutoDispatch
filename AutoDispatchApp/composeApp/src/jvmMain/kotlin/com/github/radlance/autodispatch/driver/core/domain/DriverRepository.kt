package com.github.radlance.autodispatch.driver.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.TablePaginatedResult

interface DriverRepository {

    suspend fun drivers(
        page: Int = 1,
        pageSize: Int = 10,
        searchQuery: String? = null
    ): FetchResult<TablePaginatedResult<Driver>, String>
}