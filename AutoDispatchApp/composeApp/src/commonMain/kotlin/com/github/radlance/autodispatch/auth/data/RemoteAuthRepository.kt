package com.github.radlance.autodispatch.auth.data

import com.github.radlance.autodispatch.auth.domain.AuthRepository
import com.github.radlance.autodispatch.auth.domain.LoginResponse
import com.github.radlance.autodispatch.common.data.ApiService
import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.clearBearerTokenCache
import com.github.radlance.autodispatch.common.data.toLoginResponse
import com.github.radlance.autodispatch.common.domain.FetchResult
import io.ktor.client.HttpClient

internal class RemoteAuthRepository(
    private val apiService: ApiService,
    private val handleRequest: HandleRequest,
    private val dataStoreManager: DataStoreManager,
    private val httpClient: HttpClient
) : AuthRepository {
    override suspend fun signIn(
        login: String,
        password: String
    ): FetchResult<LoginResponse, String> =
        handleRequest.handle {
            dataStoreManager.deleteToken()
            httpClient.clearBearerTokenCache()
            apiService.signIn(loginUser = SignInUserDto(login = login, password = password)).also {
                dataStoreManager.saveToken(it.accessToken)
                dataStoreManager.saveUserRoleId(it.roleId)
                httpClient.clearBearerTokenCache()
            }
        }.map { it.toLoginResponse() }

    override suspend fun clearToken() {
        dataStoreManager.deleteToken()
        httpClient.clearBearerTokenCache()
    }
}
