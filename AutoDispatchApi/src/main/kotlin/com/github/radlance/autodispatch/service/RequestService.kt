package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.request.PaginatedResult
import com.github.radlance.autodispatch.domain.request.Filters
import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.repository.RequestRepository

class RequestService(
    private val requestRepository: RequestRepository
) {
    suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ): PaginatedResult<Request> {

        val requests = requestRepository.requests(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery,
            originCityIds = originCityIds,
            destinationCityIds = destinationCityIds,
            cargoTypeIds = cargoTypeIds,
            statusIds = statusIds,
            driverIds = driverIds,
            vehicleIds = vehicleIds
        )
        return requests
    }

    suspend fun filters(): Filters {
        val filters = requestRepository.filters()
        return filters
    }
}