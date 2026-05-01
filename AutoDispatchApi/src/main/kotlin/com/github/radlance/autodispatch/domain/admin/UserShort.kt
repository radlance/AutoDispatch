package com.github.radlance.autodispatch.domain.admin

import kotlinx.serialization.Serializable

@Serializable
data class UserShort(
    val id: Int,
    val login: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String?
)