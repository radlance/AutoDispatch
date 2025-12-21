package com.github.radlance.autodispatch.vehicle.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDriverWithoutVehicleListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverVehicleAssignmentRepository
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverWithoutVehicle

class RemoteDriverVehicleAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverVehicleAssignmentRepository {

    override suspend fun driversWithoutVehicle(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<DriverWithoutVehicle>, String> = handleRequest.handle {
        apiService.driversWithoutVehicle(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery
        ).toDriverWithoutVehicleListPaginatedResult()
    }
}