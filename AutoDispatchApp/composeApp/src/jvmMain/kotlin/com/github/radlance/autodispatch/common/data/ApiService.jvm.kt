package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.data.RequestResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ApiServiceJvm : ApiService {

    suspend fun requests(): RequestResponseDto
}

internal class KtorApiServiceJvm(
    private val httpClient: HttpClient,
    private val apiService: ApiService
) : ApiServiceJvm, ApiService by apiService {

    override suspend fun requests(): RequestResponseDto {
        return httpClient.get("requests").body()
    }
}