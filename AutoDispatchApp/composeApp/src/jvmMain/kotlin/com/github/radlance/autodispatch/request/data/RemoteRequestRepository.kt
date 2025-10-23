package com.github.radlance.autodispatch.request.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toRequestResponse
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.domain.RequestRepository
import com.github.radlance.autodispatch.request.domain.RequestResponse

class RemoteRequestRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : RequestRepository {
    override suspend fun requests(): FetchResult<RequestResponse, String> = handleRequest.handle {
        apiService.requests().toRequestResponse()
    }
}