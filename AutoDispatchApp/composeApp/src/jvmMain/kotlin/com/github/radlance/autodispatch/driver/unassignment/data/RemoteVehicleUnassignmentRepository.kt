package com.github.radlance.autodispatch.driver.unassignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.driver.unassignment.domain.VehicleUnassignmentRepository

class RemoteVehicleUnassignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : VehicleUnassignmentRepository {

    override suspend fun unassignVehicle(driverId: Int): FetchResult<Unit, RequestError> =
        handleRequest.handleClassified {
            apiService.unassignVehicle(driverId)
        }
}