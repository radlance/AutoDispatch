package com.github.radlance.autodispatch.navigation.domain

import kotlinx.coroutines.flow.Flow

interface NavigationRepository {

    val authorized: Flow<Boolean>

    val sessionExpired: Flow<Boolean>
}