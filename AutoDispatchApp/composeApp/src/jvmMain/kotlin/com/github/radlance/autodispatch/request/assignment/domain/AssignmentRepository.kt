package com.github.radlance.autodispatch.request.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.DeliveryError

interface AssignmentRepository {

    suspend fun requestAssignment(): FetchResult<List<DriverStats>, String>

    suspend fun assignRequestToDriver(requestId: Int, driverId: Int): FetchResult<Unit, DeliveryError>

    suspend fun reassignRequestToDriver(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError>
}