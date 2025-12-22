package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.delivery.domain.RequestError
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

interface HandleRequest {

    suspend fun <D : Any> handle(action: suspend () -> D): FetchResult<D, String>

    suspend fun <D : Any> handleClassified(
        action: suspend () -> D
    ): FetchResult<D, RequestError>
}

internal class BaseHandleRequest : HandleRequest {

    override suspend fun <D : Any> handle(action: suspend () -> D): FetchResult<D, String> {
        return try {
            FetchResult.Success(action.invoke())
        } catch (e: ClientRequestException) {
            FetchResult.Error(e.response.bodyAsText())
        } catch (_: SocketTimeoutException) {
            FetchResult.Error("Таймаут соединения")
        } catch (_: IOException) {
            FetchResult.Error("Ошибка подключения")
        } catch (e: Exception) {
            FetchResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }

    override suspend fun <D : Any> handleClassified(
        action: suspend () -> D
    ): FetchResult<D, RequestError> = try {
        FetchResult.Success(action.invoke())
    } catch (e: ClientRequestException) {
        val message = e.response.bodyAsText()
        if (e.response.status == HttpStatusCode.Conflict) {
            try {
                val errorResponse = e.response.body<ErrorResponse>()
                FetchResult.Error(
                    when (errorResponse.errorCode) {
                        "DRIVER_BUSY" -> RequestError.DriverBusyError(errorResponse.message)
                        else -> RequestError.GenericStateError(errorResponse.message)
                    }
                )
            } catch (_: Exception) {
                FetchResult.Error(RequestError.BaseError(message))
            }
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