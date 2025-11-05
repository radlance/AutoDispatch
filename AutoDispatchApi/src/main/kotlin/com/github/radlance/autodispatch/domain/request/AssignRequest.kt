package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class AssignRequest(
    val driverId: Int
)