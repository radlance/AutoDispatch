package com.github.radlance.autodispatch.request.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDriverStats
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.assignment.domain.AssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

class RemoteAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : AssignmentRepository {

    override suspend fun requestAssignment(): FetchResult<List<DriverStats>, String> =
        handleRequest.handle {
            apiService.requestAssignment().map { it.toDriverStats() }
        }
}