package com.github.radlance.autodispatch.driver.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.ErrorResponse
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toVehicleListPaginatedResult
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

class RemoteVehicleAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : VehicleAssignmentRepository {

    override suspend fun vehicleAssignments(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): FetchResult<ListPaginatedResult<Vehicle>, String> =
        handleRequest.handle {
            apiService.vehicleAssignments(
                page = page,
                pageSize = pageSize,
                searchQuery = searchQuery
            ).toVehicleListPaginatedResult()
        }

    override suspend fun assignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError> = handleAssignment {
        apiService.assignVehicleToDriver(vehicleId, driverId)
    }

    override suspend fun reassignVehicleToDriver(
        vehicleId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError> = handleAssignment {
        apiService.reassignVehicleToDriver(vehicleId, driverId)
    }

    private suspend fun handleAssignment(
        block: suspend () -> Unit
    ): FetchResult<Unit, DeliveryError> = try {
        block()
        FetchResult.Success(Unit)
    } catch (e: ClientRequestException) {
        val message = e.response.bodyAsText()
        if (e.response.status == HttpStatusCode.Conflict) {
            try {
                val errorResponse = e.response.body<ErrorResponse>()
                FetchResult.Error(
                    when (errorResponse.errorCode) {
                        "DRIVER_BUSY" -> DeliveryError.DriverBusyError(errorResponse.message)
                        else -> DeliveryError.GenericStateError(errorResponse.message)
                    }
                )
            } catch (_: Exception) {
                FetchResult.Error(DeliveryError.BaseError(message))
            }
        } else {
            FetchResult.Error(DeliveryError.BaseError(message))
        }
    } catch (_: SocketTimeoutException) {
        FetchResult.Error(DeliveryError.BaseError("Таймаут соединения"))
    } catch (_: IOException) {
        FetchResult.Error(DeliveryError.BaseError("Ошибка подключения"))
    } catch (e: Exception) {
        FetchResult.Error(DeliveryError.BaseError(e.message ?: "Неизвестная ошибка"))
    }

}