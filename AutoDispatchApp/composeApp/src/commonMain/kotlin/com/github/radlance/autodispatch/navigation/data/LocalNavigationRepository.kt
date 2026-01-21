package com.github.radlance.autodispatch.navigation.data

import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.navigation.domain.NavigationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalNavigationRepository(
    private val dataStoreManager: DataStoreManager
) : NavigationRepository {

    override val authorized: Flow<Boolean> = dataStoreManager.token.map { it != null }

    override suspend fun deleteToken() {
        dataStoreManager.deleteToken()
    }

    override val sessionExpired: Flow<Boolean> = dataStoreManager.sessionExpired

    override suspend fun saveSessionExpired(expired: Boolean) {
        dataStoreManager.saveSessionExpired(expired)
    }
}