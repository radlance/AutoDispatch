package com.github.radlance.autodispatch.delivery.details.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.ErrorResponse
import com.github.radlance.autodispatch.common.data.toDeliveryDetailed
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

class RemoteDeliveryDetailsRepository(
    private val apiService: ApiServiceMobile
) : DeliveryDetailsRepository {

    override suspend fun deliveryDetails(deliveryId: Int): FetchResult<DeliveryDetailed, DeliveryError> {
        return try {
            val delivery = apiService.deliveryDetails(deliveryId).toDeliveryDetailed()
            FetchResult.Success(delivery)
        } catch (e: ClientRequestException) {
            val message = e.response.bodyAsText()
            if (e.response.status == HttpStatusCode.NotFound || e.response.status == HttpStatusCode.Forbidden) {
                FetchResult.Error(DeliveryError.InternalError(message))
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

    override suspend fun acceptDelivery(deliveryId: Int): FetchResult<Unit, DeliveryError> {
        return try {
            apiService.startDelivery(deliveryId)
            FetchResult.Success(Unit)
        } catch (e: ClientRequestException) {
            val message = e.response.bodyAsText()
            when (e.response.status) {
                HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
                    FetchResult.Error(DeliveryError.InternalError(message))
                }

                HttpStatusCode.Conflict -> {
                    try {
                        val errorResponse = e.response.body<ErrorResponse>()

                        when (errorResponse.errorCode) {
                            "DRIVER_BUSY" -> FetchResult.Error(
                                DeliveryError.DriverBusyError(errorResponse.message)
                            )
                            "DELIVERY_CANCELED" -> FetchResult.Error(
                                DeliveryError.DeliveryCanceledError(errorResponse.message)
                            )
                            else -> FetchResult.Error(
                                DeliveryError.GenericStateError(errorResponse.message)
                            )
                        }
                    } catch (_: Exception) {
                        FetchResult.Error(DeliveryError.BaseError(message))
                    }
                }

                else -> {
                    FetchResult.Error(DeliveryError.BaseError(message))
                }
            }
        } catch (_: SocketTimeoutException) {
            FetchResult.Error(DeliveryError.BaseError("Таймаут соединения"))
        } catch (_: IOException) {
            FetchResult.Error(DeliveryError.BaseError("Ошибка подключения"))
        } catch (e: Exception) {
            FetchResult.Error(DeliveryError.BaseError(e.message ?: "Неизвестная ошибка"))
        }
    }
}