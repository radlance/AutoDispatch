package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestStatusDto(
    val id: Int,
    val name: String
)
