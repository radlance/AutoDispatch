package com.github.radlance.autodispatch.admin.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRoleDto(
    val id: Int,
    val name: String
)
