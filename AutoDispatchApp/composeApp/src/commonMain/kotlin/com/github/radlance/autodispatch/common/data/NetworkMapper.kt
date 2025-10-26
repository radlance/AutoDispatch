package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.profile.data.UserDto
import com.github.radlance.autodispatch.profile.domain.User

internal fun UserDto.toUser(): User {
    return User(
        id = id,
        login = login,
        fullName = fullName,
        phoneNumber = phoneNumber
    )
}