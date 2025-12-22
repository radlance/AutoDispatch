package com.github.radlance.autodispatch.driver.unassignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.RequestError

interface VehicleUnassignmentRepository {

    suspend fun unassignVehicle(driverId: Int): FetchResult<Unit, RequestError>
}