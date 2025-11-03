package com.github.radlance.autodispatch.request.change.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface ChangeRequestRepository {

    suspend fun customers(query: String): List<Customer>

    suspend fun createRequest(request: ChangeRequest): FetchResult<Unit, String>

    suspend fun editRequest(requestId: Int, request: ChangeRequest): FetchResult<Unit, String>

    suspend fun removeRequest(requestId: Int): FetchResult<Unit, String>
}