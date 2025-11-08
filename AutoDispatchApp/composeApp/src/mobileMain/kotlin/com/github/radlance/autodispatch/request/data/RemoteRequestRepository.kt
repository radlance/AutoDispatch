package com.github.radlance.autodispatch.request.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toRequest
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.request.domain.RequestRepository
import com.github.radlance.autodispatch.reuqest.core.domain.Request

class RemoteRequestRepository(
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : RequestRepository {
    override suspend fun request(): FetchResult<List<Request>, String> = handleRequest.handle {
        apiService.myRequests().map { it.toRequest() }
    }
}