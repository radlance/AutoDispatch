package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestStatus(
    val id: Int,
    val name: String
)
