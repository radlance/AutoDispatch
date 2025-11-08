package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestStatusDto(
    val id: Int,
    val name: String
)
