package com.github.radlance.autodispatch.reuqest.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val address: String?,
    val lat: Double,
    val lon: Double,
)
