package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val address: String?,
    val lat: Double,
    val lon: Double
)
