package com.github.radlance.autodispatch.delivery.details.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.ErrorResponse
import com.github.radlance.autodispatch.common.data.toDeliveryDetailed
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.delivery.core.data.DeliveryCache
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.domain.RequestError
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlin.time.Clock

class RemoteDeliveryDetailsRepository(
    private val apiService: ApiServiceMobile,
    private val cache: DeliveryCache
) : DeliveryDetailsRepository {

    override suspend fun deliveryDetails(deliveryId: Int): FetchResult<DeliveryDetailed, RequestError> {
        return safeApiCall {
            apiService.deliveryDetails(deliveryId).toDeliveryDetailed()
        }
    }

    override suspend fun acceptDelivery(deliveryId: Int): FetchResult<Unit, RequestError> {
        return safeApiCall {
            apiService.startDelivery(deliveryId)
        }.also { result ->
            if (result is FetchResult.Success) {
                cache.update(deliveryId) {
                    it.copy(
                        status = RequestStatus.InProgress,
                        updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                }
            }
        }
    }

    override suspend fun arriveLoading(deliveryId: Int): FetchResult<Unit, RequestError> {
        return safeApiCall { apiService.arriveLoading(deliveryId) }
    }

    override suspend fun arriveUnloading(deliveryId: Int): FetchResult<Unit, RequestError> {
        return safeApiCall { apiService.arriveUnloading(deliveryId) }
    }

    override suspend fun detourSheet(deliveryId: Int): FetchResult<ByteArray, RequestError> {
        return safeApiCall { apiService.detourSheet(deliveryId) }
    }

    private suspend fun <T> safeApiCall(
        action: suspend () -> T
    ): FetchResult<T, RequestError> {
        return try {
            FetchResult.Success(action())
        } catch (e: ClientRequestException) {
            val response = e.response
            val message = response.bodyAsText()

            when (response.status) {
                HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
                    FetchResult.Error(RequestError.InternalError(message))
                }
                HttpStatusCode.Conflict -> {
                    parseConflictError(e, message)
                }
                else -> {
                    FetchResult.Error(RequestError.BaseError(message))
                }
            }
        } catch (_: SocketTimeoutException) {
            FetchResult.Error(RequestError.BaseError("Таймаут соединения"))
        } catch (_: IOException) {
            FetchResult.Error(RequestError.BaseError("Ошибка подключения"))
        } catch (e: Exception) {
            FetchResult.Error(RequestError.BaseError(e.message ?: "Неизвестная ошибка"))
        }
    }

    private suspend fun <T> parseConflictError(
        e: ClientRequestException,
        fallbackMessage: String
    ): FetchResult<T, RequestError> {
        return try {
            val errorResponse = e.response.body<ErrorResponse>()
            val error = when (errorResponse.errorCode) {
                "DRIVER_BUSY" -> RequestError.DriverBusyError(errorResponse.message)
                "DELIVERY_CANCELED" -> RequestError.DeliveryCanceledError(errorResponse.message)
                "WORK_SCHEDULE" -> RequestError.WorkScheduleError(errorResponse.message)
                else -> RequestError.GenericStateError(errorResponse.message)
            }
            FetchResult.Error(error)
        } catch (_: Exception) {
            FetchResult.Error(RequestError.BaseError(fallbackMessage))
        }
    }
}