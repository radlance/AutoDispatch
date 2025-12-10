package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.driver.data.DriverDto
import com.github.radlance.autodispatch.request.assignment.data.AssignRequestDto
import com.github.radlance.autodispatch.request.assignment.data.DriverStatsDto
import com.github.radlance.autodispatch.request.change.data.ChangeRequestDto
import com.github.radlance.autodispatch.request.change.data.CoordsDto
import com.github.radlance.autodispatch.request.change.data.PointDto
import com.github.radlance.autodispatch.request.change.data.RejectDocumentDto
import com.github.radlance.autodispatch.request.core.data.FiltersDto
import com.github.radlance.autodispatch.request.core.data.PaginatedResultDto
import com.github.radlance.autodispatch.reuqest.core.data.CustomerDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

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

    suspend fun cancelRequest(requestId: Int)

    suspend fun cancelAssignment(requestId: Int)

    suspend fun requestAssignment(): List<DriverStatsDto>

    suspend fun assignRequestToDriver(requestId: Int, driverId: Int)

    suspend fun reassignRequestToDriver(requestId: Int, driverId: Int)

    suspend fun coords(): CoordsDto

    suspend fun points(query: String): List<PointDto>

    suspend fun rejectDocument(requestId: Int, rejectDocumentDto: RejectDocumentDto)

    suspend fun approveDocument(requestId: Int)

    suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): PaginatedResultDto<DriverDto>
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

    override suspend fun cancelRequest(requestId: Int) {
        httpClient.put("requests/${requestId}/cancel")
    }

    override suspend fun cancelAssignment(requestId: Int) {
        httpClient.put("requests/${requestId}/assignment/cancel")
    }

    override suspend fun requestAssignment(): List<DriverStatsDto> {
        return httpClient.get("requests/request-assignment").body()
    }

    override suspend fun assignRequestToDriver(requestId: Int, driverId: Int) {
        httpClient.post("requests/${requestId}/assign") {
            setBody(AssignRequestDto(driverId))
        }
    }

    override suspend fun reassignRequestToDriver(requestId: Int, driverId: Int) {
        httpClient.put("requests/${requestId}/assign") {
            setBody(AssignRequestDto(driverId))
        }
    }

    override suspend fun coords(): CoordsDto {
        return httpClient.get {
            url("http://ip-api.com/json/")
            headers.remove("Authorization")
        }.body()
    }

    override suspend fun points(query: String): List<PointDto> {
        return httpClient.get {
            url("https://nominatim.openstreetmap.org/search")
            header("User-Agent", "AutoDispatch")
            parameter("q", query)
            parameter("format", "jsonv2")
            parameter("limit", 3)
            parameter("countrycodes", "ru")
            parameter("polygon_geojson", 1)
        }.body()
    }

    override suspend fun rejectDocument(requestId: Int, rejectDocumentDto: RejectDocumentDto) {
        httpClient.post("documents/${requestId}/reject") {
            setBody(rejectDocumentDto)
        }
    }

    override suspend fun approveDocument(requestId: Int) {
        httpClient.post("documents/${requestId}/approve")
    }

    override suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): PaginatedResultDto<DriverDto> {
        return httpClient.get("drivers") {
            url {
                parameters.append("page", page.toString())
                parameters.append("pageSize", pageSize.toString())

                searchQuery?.let { parameters.append("search", it) }
            }
        }.body()
    }
}