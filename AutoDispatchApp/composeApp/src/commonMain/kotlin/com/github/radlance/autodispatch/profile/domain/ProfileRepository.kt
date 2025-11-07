package com.github.radlance.autodispatch.profile.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface ProfileRepository {

    suspend fun profile(): FetchResult<User, String>

    suspend fun logout()
}