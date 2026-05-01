package com.github.radlance.autodispatch.admin.domain

import com.github.radlance.autodispatch.common.domain.UserRole

data class UserManagementFilters(
    val statuses: List<UserStatus>,
    val roles: List<UserRole>
)
