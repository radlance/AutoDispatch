package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class VehicleFilter(
    val id: Int,
    val model: String,
    val licensePlate: String
)
