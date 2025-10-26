package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.request.data.FiltersDto
import com.github.radlance.autodispatch.request.data.PaginatedResultDto
import com.github.radlance.autodispatch.request.data.RequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

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
}