package com.github.radlance.autodispatch.admin.data

import kotlinx.serialization.Serializable

@Serializable
data class UserShortDto(
    val id: Int,
    val login: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String?
)