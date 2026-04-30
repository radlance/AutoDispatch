package com.github.radlance.autodispatch.request.change.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.core.domain.Customer

interface ChangeRequestRepository {

    suspend fun customers(query: String): List<Customer>

    suspend fun createRequest(request: ChangeRequest): FetchResult<Unit, RequestError>

    suspend fun editRequest(requestId: Int, request: ChangeRequest): FetchResult<Unit, RequestError>

    suspend fun cancelRequest(requestId: Int): FetchResult<Unit, RequestError>

    suspend fun removeRequest(requestId: Int): FetchResult<Unit, RequestError>

    suspend fun rejectDocument(requestId: Int, rejectionReason: String): FetchResult<Unit, String>

    suspend fun approveDocument(requestId: Int): FetchResult<Unit, String>

    suspend fun approveShippingDocument(requestId: Int): FetchResult<Unit, String>

    suspend fun unassignDriver(requestId: Int): FetchResult<Unit, RequestError>
}