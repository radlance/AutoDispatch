package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toProfileDetails
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.profile.domain.DriverProfileRepository
import com.github.radlance.autodispatch.profile.domain.ProfileDetails
import com.github.radlance.autodispatch.profile.domain.ProfileRepository

class RemoteDriverProfileRepository(
    private val profileRepository: ProfileRepository,
    private val apiService: ApiServiceMobile,
    private val handleRequest: HandleRequest
) : DriverProfileRepository, ProfileRepository by profileRepository {

    override suspend fun profileDetails(): FetchResult<ProfileDetails, String> =
        handleRequest.handle {
            apiService.profileDetails().toProfileDetails()
        }
}