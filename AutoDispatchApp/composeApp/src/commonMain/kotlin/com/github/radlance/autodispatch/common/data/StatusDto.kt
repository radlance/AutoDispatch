package com.github.radlance.autodispatch.common.data

import kotlinx.serialization.Serializable

@Serializable
data class StatusDto(
    val id: Int,
    val name: String
)