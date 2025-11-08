package com.github.radlance.autodispatch.reuqest.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class RequestStatus(
    val id: Int,
    val name: String
)
