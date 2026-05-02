package com.github.radlance.autodispatch.admin.core.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRoleDto(
    val id: Int,
    val name: String
)
