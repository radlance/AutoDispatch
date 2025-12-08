package com.github.radlance.autodispatch.reuqest.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val id: Int,
    val model: String,
    val licensePlate: String,
    val payloadCapacity: Int
)
