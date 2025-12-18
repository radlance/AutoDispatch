package com.github.radlance.autodispatch.vehicle.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult

interface VehicleRepository {

    suspend fun vehicles(
        page: Int = 1,
        pageSize: Int = 10,
        searchQuery: String? = null
    ): FetchResult<TablePaginatedResult<VehicleDetailed>, String>
}