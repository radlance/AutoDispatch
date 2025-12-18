package com.github.radlance.autodispatch.vehicle.core.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toPaginatedResultVehicleDetails
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleDetailed
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleRepository

class RemoteVehicleRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : VehicleRepository {
    override suspend fun vehicles(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<TablePaginatedResult<VehicleDetailed>, String> = handleRequest.handle {
        apiService.vehicles(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery
        ).toPaginatedResultVehicleDetails()
    }
}