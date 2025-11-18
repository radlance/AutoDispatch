package com.github.radlance.autodispatch.request.change.data

import kotlinx.serialization.Serializable

@Serializable
data class CoordsDto(
    val lat: Double,
    val lon: Double
)
