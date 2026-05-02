package com.github.radlance.autodispatch.admin.core.data

import com.github.radlance.autodispatch.common.data.StatusDto
import kotlinx.serialization.Serializable

@Serializable
data class UserManagementFiltersDto(
    val statuses: List<StatusDto>,
    val roles: List<UserRoleDto>
)
