package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class UserFilterDto(
    val id: Int,
    val fullName: String
)