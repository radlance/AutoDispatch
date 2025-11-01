package com.github.radlance.autodispatch.request.create.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toCreateRequestDto
import com.github.radlance.autodispatch.common.data.toCustomer
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.create.domain.CreateRequest
import com.github.radlance.autodispatch.request.create.domain.CreateRequestRepository
import com.github.radlance.autodispatch.request.create.domain.Customer

class RemoteCreateRequestRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : CreateRequestRepository {

    override suspend fun customers(query: String): List<Customer> {
        return try {
            apiService.customers(query).map { it.toCustomer() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun createRequest(request: CreateRequest): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.createRequest(createRequestDto = request.toCreateRequestDto())
    }
}