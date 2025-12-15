package com.github.radlance.autodispatch.driver.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.Vehicle

interface VehicleAssignmentRepository {

    suspend fun vehicleAssignments(): FetchResult<List<Vehicle>, String>

    suspend fun assignVehicleToDriver(vehicleId: Int, driverId: Int): FetchResult<Unit, String>
}