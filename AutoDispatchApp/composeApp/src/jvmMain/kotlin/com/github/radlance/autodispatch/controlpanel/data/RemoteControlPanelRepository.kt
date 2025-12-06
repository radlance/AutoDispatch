package com.github.radlance.autodispatch.controlpanel.data

import com.github.radlance.autodispatch.common.data.ApiService
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toUser
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.controlpanel.domain.ControlPanelRepository
import com.github.radlance.autodispatch.profile.domain.ProfileRepository
import com.github.radlance.autodispatch.profile.domain.User

class RemoteControlPanelRepository(
    private val apiService: ApiService,
    private val handleRequest: HandleRequest,
    private val profileRepository: ProfileRepository
) :
    ControlPanelRepository, ProfileRepository by profileRepository {

    override suspend fun profile(): FetchResult<User, String> = handleRequest.handle {
        apiService.profile().toUser()
    }
}