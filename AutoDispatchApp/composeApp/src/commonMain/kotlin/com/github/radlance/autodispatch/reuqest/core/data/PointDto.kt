package com.github.radlance.autodispatch.reuqest.core.data

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val address: String?,
    val lat: Double,
    val lon: Double,
)
