package com.github.radlance.autodispatch.request.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDriverStatsListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.assignment.domain.DriverAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

class RemoteDriverAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverAssignmentRepository {

    override suspend fun driverAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<DriverStats>, String> =
        handleRequest.handle {
            apiService.driverAssignments(
                page = page,
                pageSize = pageSize,
                searchQuery = searchQuery
            ).toDriverStatsListPaginatedResult()
        }

    override suspend fun assignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, RequestError> = handleRequest.handleClassified {
            apiService.assignDriverToRequest(requestId, driverId)
    }

    override suspend fun reassignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, RequestError> = handleRequest.handleClassified {
            apiService.reassignDriverToRequest(requestId, driverId)
    }
}