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
import kotlin.time.ExperimentalTime

class RemoteDeliveryDetailsRepository(
    private val apiService: ApiServiceMobile,
    private val cache: DeliveryCache
) : DeliveryDetailsRepository {

    override suspend fun deliveryDetails(deliveryId: Int): FetchResult<DeliveryDetailed, RequestError> {
        return try {
            val delivery = apiService.deliveryDetails(deliveryId).toDeliveryDetailed()
            FetchResult.Success(delivery)
        } catch (e: ClientRequestException) {
            val message = e.response.bodyAsText()
            if (e.response.status == HttpStatusCode.NotFound || e.response.status == HttpStatusCode.Forbidden) {
                FetchResult.Error(RequestError.InternalError(message))
            } else {
                FetchResult.Error(RequestError.BaseError(message))
            }
        } catch (_: SocketTimeoutException) {
            FetchResult.Error(RequestError.BaseError("Таймаут соединения"))
        } catch (_: IOException) {
            FetchResult.Error(RequestError.BaseError("Ошибка подключения"))
        } catch (e: Exception) {
            FetchResult.Error(RequestError.BaseError(e.message ?: "Неизвестная ошибка"))
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun acceptDelivery(deliveryId: Int): FetchResult<Unit, RequestError> {
        return try {
            apiService.startDelivery(deliveryId)
            cache.update(deliveryId) {
                it.copy(
                    status = RequestStatus.InProgress,
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
            FetchResult.Success(Unit)
        } catch (e: ClientRequestException) {
            val message = e.response.bodyAsText()
            when (e.response.status) {
                HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
                    FetchResult.Error(RequestError.InternalError(message))
                }

                HttpStatusCode.Conflict -> {
                    try {
                        val errorResponse = e.response.body<ErrorResponse>()

                        when (errorResponse.errorCode) {
                            "DRIVER_BUSY" -> FetchResult.Error(
                                RequestError.DriverBusyError(errorResponse.message)
                            )
                            "DELIVERY_CANCELED" -> FetchResult.Error(
                                RequestError.DeliveryCanceledError(errorResponse.message)
                            )
                            else -> FetchResult.Error(
                                RequestError.GenericStateError(errorResponse.message)
                            )
                        }
                    } catch (_: Exception) {
                        FetchResult.Error(RequestError.BaseError(message))
                    }
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
}