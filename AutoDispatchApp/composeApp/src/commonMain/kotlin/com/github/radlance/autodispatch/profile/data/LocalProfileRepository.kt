package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.common.data.clearBearerTokenCache
import com.github.radlance.autodispatch.profile.domain.ProfileRepository
import io.ktor.client.HttpClient

internal class LocalProfileRepository(
    private val dataStoreManager: DataStoreManager,
    private val httpClient: HttpClient
) : ProfileRepository {

    override suspend fun logout() {
        dataStoreManager.deleteTokens()
        dataStoreManager.deleteUserRoleId()
        httpClient.clearBearerTokenCache()
    }
}