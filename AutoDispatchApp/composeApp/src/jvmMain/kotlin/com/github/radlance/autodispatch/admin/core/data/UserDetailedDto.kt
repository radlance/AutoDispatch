package com.github.radlance.autodispatch.admin.core.data

import com.github.radlance.autodispatch.common.data.StatusDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailedDto(
    val id: Int,
    val login: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String?,
    val status: StatusDto,
    val role: UserRoleDto,
    val createdBy: UserShortDto?,
    val updatedBy: UserShortDto?,
    val createdAt: String,
    val updatedAt: String?,
    val lastLoginAt: String?
)
