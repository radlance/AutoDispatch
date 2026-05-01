package com.github.radlance.autodispatch.domain.admin

import com.github.radlance.autodispatch.domain.common.Status
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailed(
    val id: Int,
    val login: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String?,
    val status: Status,
    val role: UserRole,
    val createdBy: UserShort?,
    val updatedBy: UserShort?,
    val createdAt: String,
    val updatedAt: String?,
    val lastLoginAt: String?
)