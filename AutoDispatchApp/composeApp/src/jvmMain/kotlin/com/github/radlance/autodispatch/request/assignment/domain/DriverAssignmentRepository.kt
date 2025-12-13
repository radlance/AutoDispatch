package com.github.radlance.autodispatch.request.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.DeliveryError

interface DriverAssignmentRepository {

    suspend fun driverAssignments(): FetchResult<List<DriverStats>, String>

    suspend fun assignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError>

    suspend fun reassignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError>
}