package com.github.radlance.autodispatch.profile.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface DriverProfileRepository {

    suspend fun profileDetails(): FetchResult<ProfileDetails, String>

    suspend fun logout()
}