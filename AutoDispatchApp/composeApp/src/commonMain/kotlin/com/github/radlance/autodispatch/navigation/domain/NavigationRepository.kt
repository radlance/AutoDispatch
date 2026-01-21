package com.github.radlance.autodispatch.navigation.domain

import kotlinx.coroutines.flow.Flow

interface NavigationRepository {

    val authorized: Flow<Boolean>

    suspend fun deleteToken()

    val sessionExpired: Flow<Boolean>

    suspend fun saveSessionExpired(expired: Boolean)
}