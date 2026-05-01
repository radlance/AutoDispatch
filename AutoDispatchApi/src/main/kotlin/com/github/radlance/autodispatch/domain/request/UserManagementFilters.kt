package com.github.radlance.autodispatch.domain.request

import com.github.radlance.autodispatch.domain.admin.UserRole
import com.github.radlance.autodispatch.domain.common.Status
import kotlinx.serialization.Serializable

@Serializable
data class UserManagementFilters(
    val statuses: List<Status>,
    val roles: List<UserRole>
)