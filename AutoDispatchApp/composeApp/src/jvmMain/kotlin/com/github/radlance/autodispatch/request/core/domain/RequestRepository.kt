package com.github.radlance.autodispatch.request.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.reuqest.core.domain.Request

interface RequestRepository {

    suspend fun filters(): FetchResult<Filters, String>

    suspend fun requests(
        page: Int = 1,
        pageSize: Int = 10,
        searchQuery: String? = null,
        originCityIds: List<Int> = emptyList(),
        destinationCityIds: List<Int> = emptyList(),
        cargoTypeIds: List<Int> = emptyList(),
        statusIds: List<Int> = emptyList(),
        driverIds: List<Int> = emptyList(),
        vehicleIds: List<Int> = emptyList()
    ): FetchResult<TablePaginatedResult<Request>, String>
}