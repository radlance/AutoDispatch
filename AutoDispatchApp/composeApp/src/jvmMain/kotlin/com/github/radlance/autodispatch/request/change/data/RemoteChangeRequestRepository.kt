package com.github.radlance.autodispatch.request.change.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toCreateRequestDto
import com.github.radlance.autodispatch.common.data.toCustomer
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.change.domain.ChangeRequest
import com.github.radlance.autodispatch.request.change.domain.ChangeRequestRepository
import com.github.radlance.autodispatch.request.change.domain.Customer

class RemoteChangeRequestRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : ChangeRequestRepository {

    override suspend fun customers(query: String): List<Customer> {
        return try {
            apiService.customers(query).map { it.toCustomer() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun createRequest(request: ChangeRequest): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.createRequest(changeRequestDto = request.toCreateRequestDto())
    }

    override suspend fun editRequest(
        requestId: Int,
        request: ChangeRequest
    ): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.editRequest(
                requestId = requestId,
                changeRequestDto = request.toCreateRequestDto()
            )
        }

    override suspend fun cancelRequest(requestId: Int): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.cancelRequest(requestId = requestId)
        }

    override suspend fun cancelAssignment(requestId: Int): FetchResult<Unit, String> =
        handleRequest.handle {
            apiService.cancelAssignment(requestId = requestId)
        }
}