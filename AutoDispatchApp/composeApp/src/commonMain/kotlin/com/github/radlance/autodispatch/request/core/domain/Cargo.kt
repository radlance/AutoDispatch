package com.github.radlance.autodispatch.request.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Cargo(
    val type: CargoType,
    val weight: Double,
    val volume: Double?,
    val description: String?
)
