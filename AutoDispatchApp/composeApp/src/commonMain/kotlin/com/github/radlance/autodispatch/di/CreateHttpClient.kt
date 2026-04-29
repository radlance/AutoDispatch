package com.github.radlance.autodispatch.di

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val CurrentIp = "192.168.0.102"

fun createHttpClient(dataStoreManager: DataStoreManager): HttpClient {
    return HttpClient(engine = httpClientEngine) {
        expectSuccess = true
        defaultRequest {
            url("http://$CurrentIp:8084/api/")
            contentType(ContentType.Application.Json)
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.SIMPLE
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val access = dataStoreManager.accessToken.first()
                    val refresh = dataStoreManager.refreshToken.first()

                    if (access != null && refresh != null) {
                        BearerTokens(accessToken = access, refreshToken = refresh)
                    } else null
                }

                refreshTokens {
                    val currentRefreshToken = oldTokens?.refreshToken ?: return@refreshTokens null

                    try {
                        val newTokensDto = withTimeout(5000) {
                            client.post("auth/refresh-token") {
                                markAsRefreshTokenRequest()
                                setBody(RefreshTokenRequest(currentRefreshToken))
                            }.body<AuthTokens>()
                        }

                        dataStoreManager.saveTokens(
                            accessToken = newTokensDto.accessToken,
                            refreshToken = newTokensDto.refreshToken
                        )

                        BearerTokens(
                            accessToken = newTokensDto.accessToken,
                            refreshToken = newTokensDto.refreshToken
                        )
                    } catch (_: Exception) {
                        dataStoreManager.deleteTokens()
                        dataStoreManager.saveSessionExpired(expired = true)
                        null
                    }
                }
            }
        }
    }
}

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)