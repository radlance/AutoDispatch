package com.github.radlance.autodispatch.request.create.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface CreateRequestRepository {

    suspend fun customers(query: String): List<Customer>

    suspend fun createRequest(request: CreateRequest): FetchResult<Unit, String>

    suspend fun editRequest(requestId: Int, request: CreateRequest): FetchResult<Unit, String>
}