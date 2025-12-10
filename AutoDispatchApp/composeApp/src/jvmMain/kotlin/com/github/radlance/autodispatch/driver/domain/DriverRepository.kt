package com.github.radlance.autodispatch.driver.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.PaginatedResult

interface DriverRepository {

    suspend fun requests(
        page: Int = 1,
        pageSize: Int = 10,
        searchQuery: String? = null
    ): FetchResult<PaginatedResult<Driver>, String>
}