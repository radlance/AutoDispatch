package com.github.radlance.autodispatch.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val login: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: String?
)