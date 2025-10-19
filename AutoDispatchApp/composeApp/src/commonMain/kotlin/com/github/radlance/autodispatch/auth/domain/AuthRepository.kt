package com.github.radlance.autodispatch.auth.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface AuthRepository {

    suspend fun signIn(login: String, password: String): FetchResult<String, String>
}