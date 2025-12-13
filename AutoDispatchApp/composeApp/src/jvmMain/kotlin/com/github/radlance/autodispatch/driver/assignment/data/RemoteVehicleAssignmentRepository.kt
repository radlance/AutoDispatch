package com.github.radlance.autodispatch.driver.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toVehicle
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle

class RemoteVehicleAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : VehicleAssignmentRepository {

    override suspend fun vehicleAssignments(): FetchResult<List<Vehicle>, String> =
        handleRequest.handle {
            apiService.vehicleAssignments().map { it.toVehicle() }
        }

    override suspend fun assignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, String> = handleRequest.handle {
        apiService.assignVehicleToDriver(vehicleId, driverId)
    }
}