package com.github.radlance.autodispatch.request.assignment.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface AssignmentRepository {

    suspend fun requestAssignment(): FetchResult<RequestAssignment, String>
}