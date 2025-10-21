package com.github.radlance.autodispatch.profile.data

import com.github.radlance.autodispatch.common.data.ApiService
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toUser
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.profile.domain.ProfileRepository
import com.github.radlance.autodispatch.profile.domain.User

internal class RemoteProfileRepository(
    private val apiService: ApiService,
    private val handleRequest: HandleRequest
) : ProfileRepository {

    override suspend fun profile(): FetchResult<User, String> = handleRequest.handle {
        apiService.profile().toUser()
    }
}