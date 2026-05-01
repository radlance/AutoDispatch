package com.github.radlance.autodispatch.admin.domain

import com.github.radlance.autodispatch.common.domain.UserRole
import kotlinx.datetime.LocalDateTime

data class UserDetailed(
    val id: Int,
    val login: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val avatarUrl: String?,
    val status: UserStatus,
    val role: UserRole,
    val createdBy: UserShort?,
    val updatedBy: UserShort?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val lastLoginAt: LocalDateTime?
)
