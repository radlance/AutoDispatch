package com.github.radlance.autodispatch.reuqest.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class VehicleFilter(
    val id: Int,
    val model: String,
    val licensePlate: String
)
