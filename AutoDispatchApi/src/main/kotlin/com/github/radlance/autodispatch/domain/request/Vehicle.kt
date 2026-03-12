package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val regionCode: String,
    val payloadCapacity: Int
)
