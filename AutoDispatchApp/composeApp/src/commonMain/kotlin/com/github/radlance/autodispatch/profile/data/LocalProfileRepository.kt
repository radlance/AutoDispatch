package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.common.data.DataStoreManager
import com.github.radlance.autodispatch.profile.domain.ProfileRepository

internal class LocalProfileRepository(
    private val dataStoreManager: DataStoreManager
) : ProfileRepository {

    override suspend fun logout() {
        dataStoreManager.deleteToken()
    }
}