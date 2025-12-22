package com.github.radlance.autodispatch.driver.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.core.domain.Vehicle

interface VehicleAssignmentRepository {

    suspend fun vehicleAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<Vehicle>, String>

    suspend fun assignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, RequestError>

    suspend fun reassignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, RequestError>
}