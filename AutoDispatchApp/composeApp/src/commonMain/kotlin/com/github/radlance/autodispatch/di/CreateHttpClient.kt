package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.auth.data.TokenDto
import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.common.data.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

const val CurrentIp = "192.168.0.123"

fun createHttpClient(dataStoreManager: DataStoreManager): HttpClient {
    return HttpClient(engine = httpClientEngine) {
        expectSuccess = true
        defaultRequest {
            url("http://$CurrentIp:8084/api/")
            contentType(ContentType.Application.Json)
        }
        install(ContentNegotiation) {
            json(json = Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.SIMPLE
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = runBlocking { dataStoreManager.token.first() }
                    token?.let { BearerTokens(accessToken = it, refreshToken = null) }
                }

                refreshTokens {
                    val token = runBlocking { dataStoreManager.token.first() }

                    try {
                        val newToken = withTimeout(5000) {
                            client.post("auth/refresh-token") {
                                setBody(TokenDto(token!!))
                            }.body<TokenDto>().accessToken
                        }

                        with(dataStoreManager) {
                            deleteToken()
                            saveToken(newToken)
                        }

                        BearerTokens(accessToken = newToken, refreshToken = null)
                    } catch (_: TimeoutCancellationException) {
                        dataStoreManager.deleteToken()
                        dataStoreManager.saveSessionExpired(expired = true)
                        null
                    }
                }
            }
        }
    }
}