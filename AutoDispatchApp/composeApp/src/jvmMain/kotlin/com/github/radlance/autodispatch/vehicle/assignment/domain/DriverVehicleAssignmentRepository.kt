package com.github.radlance.autodispatch.vehicle.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult

interface DriverVehicleAssignmentRepository {

    suspend fun driversWithoutVehicle(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<DriverWithoutVehicle>, String>
}