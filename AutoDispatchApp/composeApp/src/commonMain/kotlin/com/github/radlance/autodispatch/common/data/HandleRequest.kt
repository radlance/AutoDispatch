package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.common.domain.FetchResult
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText

interface HandleRequest {

    suspend fun <D : Any> handle(action: suspend () -> D): FetchResult<D, String>
}

internal class BaseHandleRequest : HandleRequest {

    override suspend fun <D : Any> handle(action: suspend () -> D): FetchResult<D, String> {
        return try {
            FetchResult.Success(action.invoke())
        } catch (e: ClientRequestException) {
            FetchResult.Error(e.response.bodyAsText())
        } catch (e: SocketTimeoutException) {
            FetchResult.Error("Таймаут соединения")
        }catch (e: Exception) {
            FetchResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }
}