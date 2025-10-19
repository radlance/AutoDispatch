package com.github.radlance.autodispatch.auth.data

import com.github.radlance.autodispatch.auth.domain.AuthRepository
import com.github.radlance.autodispatch.common.data.ApiService
import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.domain.FetchResult

internal class RemoteAuthRepository(
    private val apiService: ApiService,
    private val handleRequest: HandleRequest,
    private val dataStoreManager: DataStoreManager
) : AuthRepository {
    override suspend fun signIn(login: String, password: String): FetchResult<String, String> =
        handleRequest.handle {
            apiService.signIn(loginUser = SignInUserDto(login = login, password = password)).also {
                dataStoreManager.saveToken(it)
            }
        }
}