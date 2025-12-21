package com.github.radlance.autodispatch.driver.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toVehicleListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.request.core.domain.Vehicle

class RemoteVehicleAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : VehicleAssignmentRepository {

    override suspend fun vehicleAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<Vehicle>, String> =
        handleRequest.handle {
            apiService.vehicleAssignments(
                page = page,
                pageSize = pageSize,
                searchQuery = searchQuery
            ).toVehicleListPaginatedResult()
        }

    override suspend fun assignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError> = handleRequest.handleAssignment {
        apiService.assignVehicleToDriver(vehicleId, driverId)
    }

    override suspend fun reassignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError> = handleRequest.handleAssignment {
        apiService.reassignVehicleToDriver(vehicleId, driverId)
    }
}