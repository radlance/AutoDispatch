package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.change.data.ChangeRequestDto
import com.github.radlance.autodispatch.request.change.data.CustomerDto
import com.github.radlance.autodispatch.request.core.data.FiltersDto
import com.github.radlance.autodispatch.request.core.data.PaginatedResultDto
import com.github.radlance.autodispatch.request.core.data.RequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

interface ApiServiceJvm : ApiService {

    suspend fun filters(): FiltersDto

    suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ): PaginatedResultDto<RequestDto>

    suspend fun customers(query: String): List<CustomerDto>

    suspend fun createRequest(changeRequestDto: ChangeRequestDto)

    suspend fun editRequest(requestId: Int, changeRequestDto: ChangeRequestDto)

    suspend fun removeRequest(requestId: Int)
}

internal class KtorApiServiceJvm(
    private val httpClient: HttpClient,
    private val apiService: ApiService
) : ApiServiceJvm, ApiService by apiService {

    override suspend fun filters(): FiltersDto {
        return httpClient.get("requests/filters").body()
    }

    override suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ): PaginatedResultDto<RequestDto> {
        return httpClient.get("requests") {
            url {
                parameters.append("page", page.toString())
                parameters.append("pageSize", pageSize.toString())

                searchQuery?.let { parameters.append("search", it) }

                if (originCityIds.isNotEmpty()) {
                    parameters.append("originCityIds", originCityIds.joinToString(","))
                }
                if (destinationCityIds.isNotEmpty()) {
                    parameters.append("destinationCityIds", destinationCityIds.joinToString(","))
                }
                if (cargoTypeIds.isNotEmpty()) {
                    parameters.append("cargoTypeIds", cargoTypeIds.joinToString(","))
                }
                if (statusIds.isNotEmpty()) {
                    parameters.append("statusIds", statusIds.joinToString(","))
                }
                if (driverIds.isNotEmpty()) {
                    parameters.append("driverIds", driverIds.joinToString(","))
                }
                if (vehicleIds.isNotEmpty()) {
                    parameters.append("vehicleIds", vehicleIds.joinToString(","))
                }
            }
        }.body()
    }

    override suspend fun customers(query: String): List<CustomerDto> {
        return httpClient.get("requests/customers") {
            parameter("q", query)
        }.body()
    }

    override suspend fun createRequest(changeRequestDto: ChangeRequestDto) {
        httpClient.post("requests") {
            setBody(changeRequestDto)
        }
    }

    override suspend fun editRequest(requestId: Int, changeRequestDto: ChangeRequestDto) {
        httpClient.put("requests/${requestId}") {
            setBody(changeRequestDto)
        }
    }

    override suspend fun removeRequest(requestId: Int) {
        httpClient.delete("requests/${requestId}")
    }
}