package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.driver.core.data.DriverDto
import com.github.radlance.autodispatch.driver.history.data.DriverHistoryDto
import com.github.radlance.autodispatch.driver.request.data.DriverRequestDto
import com.github.radlance.autodispatch.request.assignment.data.AssignRequestDto
import com.github.radlance.autodispatch.request.assignment.data.DriverStatsDto
import com.github.radlance.autodispatch.request.change.data.ChangeRequestDto
import com.github.radlance.autodispatch.request.change.data.CoordsDto
import com.github.radlance.autodispatch.request.change.data.PointDetailedDto
import com.github.radlance.autodispatch.request.change.data.RejectDocumentDto
import com.github.radlance.autodispatch.request.core.data.CustomerDto
import com.github.radlance.autodispatch.request.core.data.FiltersDto
import com.github.radlance.autodispatch.request.core.data.RequestDto
import com.github.radlance.autodispatch.request.core.data.TablePaginatedResultDto
import com.github.radlance.autodispatch.request.core.data.VehicleDto
import com.github.radlance.autodispatch.vehicle.assignment.data.DriverWithoutVehicleDto
import com.github.radlance.autodispatch.vehicle.core.data.VehicleDetailedDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
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
    ): TablePaginatedResultDto<RequestDto>

    suspend fun customers(query: String): List<CustomerDto>

    suspend fun createRequest(changeRequestDto: ChangeRequestDto)

    suspend fun editRequest(requestId: Int, changeRequestDto: ChangeRequestDto)

    suspend fun cancelRequest(requestId: Int)

    suspend fun removeRequest(requestId: Int)

    suspend fun driverAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResultDto<DriverStatsDto>

    suspend fun assignDriverToRequest(requestId: Int, driverId: Int)

    suspend fun reassignDriverToRequest(requestId: Int, driverId: Int)

    suspend fun coords(): CoordsDto

    suspend fun points(query: String): List<PointDetailedDto>

    suspend fun rejectDocument(requestId: Int, rejectDocumentDto: RejectDocumentDto)

    suspend fun approveDocument(requestId: Int)

    suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): TablePaginatedResultDto<DriverDto>

    suspend fun vehicleAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResultDto<VehicleDto>

    suspend fun assignVehicleToDriver(vehicleId: Int, driverId: Int)

    suspend fun reassignVehicleToDriver(vehicleId: Int, driverId: Int)

    suspend fun driverHistory(
        driverId: Int,
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DriverHistoryDto>

    suspend fun availableRequests(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DriverRequestDto>

    suspend fun vehicles(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): TablePaginatedResultDto<VehicleDetailedDto>

    suspend fun driversWithoutVehicle(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResultDto<DriverWithoutVehicleDto>

    suspend fun unassignVehicle(driverId: Int)

    suspend fun unassignDriver(requestId: Int)
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
    ): TablePaginatedResultDto<RequestDto> {
        return httpClient.get("requests") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())

            searchQuery?.let { parameter("search", it) }

            if (originCityIds.isNotEmpty()) {
                parameter("originCityIds", originCityIds.joinToString(","))
            }
            if (destinationCityIds.isNotEmpty()) {
                parameter("destinationCityIds", destinationCityIds.joinToString(","))
            }
            if (cargoTypeIds.isNotEmpty()) {
                parameter("cargoTypeIds", cargoTypeIds.joinToString(","))
            }
            if (statusIds.isNotEmpty()) {
                parameter("statusIds", statusIds.joinToString(","))
            }
            if (driverIds.isNotEmpty()) {
                parameter("driverIds", driverIds.joinToString(","))
            }
            if (vehicleIds.isNotEmpty()) {
                parameter("vehicleIds", vehicleIds.joinToString(","))
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
        httpClient.put("requests/$requestId") {
            setBody(changeRequestDto)
        }
    }

    override suspend fun cancelRequest(requestId: Int) {
        httpClient.put("requests/$requestId/cancel")
    }

    override suspend fun removeRequest(requestId: Int) {
        httpClient.delete("requests/$requestId")
    }

    override suspend fun driverAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResultDto<DriverStatsDto> {
        return httpClient.get("drivers/assignments") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun assignDriverToRequest(requestId: Int, driverId: Int) {
        httpClient.post("requests/$requestId/assign") {
            setBody(AssignRequestDto(driverId))
        }
    }

    override suspend fun reassignDriverToRequest(requestId: Int, driverId: Int) {
        httpClient.put("requests/$requestId/assign") {
            setBody(AssignRequestDto(driverId))
        }
    }

    override suspend fun coords(): CoordsDto {
        return httpClient.get {
            url("http://ip-api.com/json/")
            headers.remove("Authorization")
        }.body()
    }

    override suspend fun points(query: String): List<PointDetailedDto> {
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
        httpClient.post("documents/$requestId/reject") {
            setBody(rejectDocumentDto)
        }
    }

    override suspend fun approveDocument(requestId: Int) {
        httpClient.post("documents/$requestId/approve")
    }

    override suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): TablePaginatedResultDto<DriverDto> {
        return httpClient.get("drivers") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun vehicleAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResultDto<VehicleDto> {
        return httpClient.get("vehicles/unassigned") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun assignVehicleToDriver(vehicleId: Int, driverId: Int) {
        httpClient.post("vehicles/$vehicleId/assignment") {
            setBody(AssignRequestDto(driverId))
        }
    }

    override suspend fun reassignVehicleToDriver(vehicleId: Int, driverId: Int) {
        httpClient.patch("vehicles/$vehicleId/assignment") {
            setBody(AssignRequestDto(driverId))
        }
    }

    override suspend fun driverHistory(
        driverId: Int,
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DriverHistoryDto> {
        return httpClient.get("deliveries/history/$driverId") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun availableRequests(
        searchQuery: String?,
        page: Int,
        pageSize: Int
    ): ListPaginatedResultDto<DriverRequestDto> {
        return httpClient.get("requests/available") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun vehicles(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): TablePaginatedResultDto<VehicleDetailedDto> {
        return httpClient.get("vehicles") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun driversWithoutVehicle(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResultDto<DriverWithoutVehicleDto> {
        return httpClient.get("drivers/without-vehicle") {
            parameter("page", page.toString())
            parameter("pageSize", pageSize.toString())
            searchQuery?.let { parameter("search", it) }
        }.body()
    }

    override suspend fun unassignVehicle(driverId: Int) {
        httpClient.delete("vehicles/assignment/$driverId")
    }

    override suspend fun unassignDriver(requestId: Int) {
        httpClient.delete("requests/$requestId/assign")
    }
}