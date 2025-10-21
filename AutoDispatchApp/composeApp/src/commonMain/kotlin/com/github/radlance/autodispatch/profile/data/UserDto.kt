package com.github.radlance.autodispatch.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val login: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String?
)