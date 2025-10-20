package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.repository.ProfileRepository

class ProfileService(
    private val profileRepository: ProfileRepository
) {

    suspend fun userByLogin(login: String): User = profileRepository.userByLogin(login)
}