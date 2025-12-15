package com.github.radlance.autodispatch.request.change.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.core.domain.Customer

interface ChangeRequestRepository {

    suspend fun customers(query: String): List<Customer>

    suspend fun createRequest(request: ChangeRequest): FetchResult<Unit, String>

    suspend fun editRequest(requestId: Int, request: ChangeRequest): FetchResult<Unit, String>

    suspend fun cancelRequest(requestId: Int): FetchResult<Unit, String>

    suspend fun cancelAssignment(requestId: Int): FetchResult<Unit, String>

    suspend fun rejectDocument(requestId: Int, rejectionReason: String): FetchResult<Unit, String>

    suspend fun approveDocument(requestId: Int): FetchResult<Unit, String>
}