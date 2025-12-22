package com.github.radlance.autodispatch.request.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.domain.RequestError

interface DriverAssignmentRepository {

    suspend fun driverAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<DriverStats>, String>

    suspend fun assignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, RequestError>

    suspend fun reassignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, RequestError>
}