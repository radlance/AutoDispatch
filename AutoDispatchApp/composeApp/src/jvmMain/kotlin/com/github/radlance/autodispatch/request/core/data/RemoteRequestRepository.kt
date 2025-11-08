package com.github.radlance.autodispatch.request.core.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toFilters
import com.github.radlance.autodispatch.common.data.toPaginatedResultRequest
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.Filters
import com.github.radlance.autodispatch.request.core.domain.PaginatedResult
import com.github.radlance.autodispatch.request.core.domain.Request
import com.github.radlance.autodispatch.request.core.domain.RequestRepository

// TODO обновить страницу входа (преимущества и тд)

class RemoteRequestRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : RequestRepository {
    override suspend fun filters(): FetchResult<Filters, String> = handleRequest.handle {
        apiService.filters().toFilters()
    }

    override suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ): FetchResult<PaginatedResult<Request>, String> = handleRequest.handle {
        apiService.requests(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery,
            originCityIds = originCityIds,
            destinationCityIds = destinationCityIds,
            cargoTypeIds = cargoTypeIds,
            statusIds = statusIds,
            driverIds = driverIds,
            vehicleIds = vehicleIds
        ).toPaginatedResultRequest()
    }
}