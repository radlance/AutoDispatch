package com.github.radlance.autodispatch.profile.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import kotlinx.coroutines.flow.Flow

interface DriverProfileRepository {

    suspend fun profileDetails(): FetchResult<ProfileDetails, String>

    fun deliveriesStatsFlow(): Flow<DeliveriesStats>

    suspend fun uploadProfileImage(image: ByteArray): FetchResult<Unit, String>

    suspend fun deleteProfileImage(): FetchResult<Unit, String>

    suspend fun logout()
}