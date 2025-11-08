package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.reuqest.core.data.RequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ApiServiceMobile : ApiService {

    suspend fun myRequests(): List<RequestDto>
}

internal class KtorApiServiceMobile(
    private val httpClient: HttpClient,
    private val apiService: ApiService
) : ApiServiceMobile, ApiService by apiService {

    override suspend fun myRequests(): List<RequestDto> {
        return httpClient.get("requests/my").body()
    }
}