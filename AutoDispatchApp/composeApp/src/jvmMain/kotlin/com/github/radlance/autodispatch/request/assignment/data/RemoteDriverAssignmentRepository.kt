package com.github.radlance.autodispatch.request.assignment.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDriverStats
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.request.assignment.domain.DriverAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

class RemoteDriverAssignmentRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : DriverAssignmentRepository {

    override suspend fun driverAssignments(): FetchResult<List<DriverStats>, String> =
        handleRequest.handle {
            apiService.driverAssignments().map { it.toDriverStats() }
        }

    override suspend fun assignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError> {
        return try {
            apiService.assignDriverToRequest(requestId, driverId)
            FetchResult.Success(Unit)
        } catch (e: ClientRequestException) {

            FetchResult.Error(DeliveryError.BaseError(e.response.bodyAsText()))

        } catch (e: SocketTimeoutException) {
            FetchResult.Error(DeliveryError.BaseError("Таймаут соединения"))
        } catch (e: IOException) {
            FetchResult.Error(DeliveryError.BaseError("Ошибка подключения"))
        } catch (e: Exception) {
            FetchResult.Error(DeliveryError.BaseError(e.message ?: "Неизвестная ошибка"))
        }
    }

    override suspend fun reassignDriverToRequest(
        requestId: Int,
        driverId: Int
    ): FetchResult<Unit, DeliveryError> {
        return try {
            apiService.reassignDriverToRequest(requestId, driverId)
            FetchResult.Success(Unit)
        } catch (e: ClientRequestException) {
            val message = e.response.bodyAsText()
            if (e.response.status == HttpStatusCode.Conflict) {
                FetchResult.Error(DeliveryError.GenericStateError(message))
            } else {
                FetchResult.Error(DeliveryError.BaseError(message))
            }
        } catch (e: SocketTimeoutException) {
            FetchResult.Error(DeliveryError.BaseError("Таймаут соединения"))
        } catch (e: IOException) {
            FetchResult.Error(DeliveryError.BaseError("Ошибка подключения"))
        } catch (e: Exception) {
            FetchResult.Error(DeliveryError.BaseError(e.message ?: "Неизвестная ошибка"))
        }
    }
}