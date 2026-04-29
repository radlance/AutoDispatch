package com.github.radlance.autodispatch.navigation.data

import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.common.data.clearBearerTokenCache
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalNavigationRepository(
    private val dataStoreManager: DataStoreManager,
    private val httpClient: HttpClient
) : NavigationRepository {

    override val authorized: Flow<Boolean> = dataStoreManager.refreshToken.map { it != null }

    override suspend fun deleteToken() {
        dataStoreManager.deleteTokens()
        httpClient.clearBearerTokenCache()
    }

    override val sessionExpired: Flow<Boolean> = dataStoreManager.sessionExpired

    override suspend fun saveSessionExpired(expired: Boolean) {
        dataStoreManager.saveSessionExpired(expired)
    }

    override val userRoleId: Flow<Int?> = dataStoreManager.userRoleId

    override suspend fun deleteUserRoleId() {
        dataStoreManager.deleteUserRoleId()
    }
}
