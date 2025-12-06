package com.github.radlance.autodispatch.controlpanel.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.profile.domain.User

interface ControlPanelRepository {

    suspend fun profile(): FetchResult<User, String>

    suspend fun logout()
}