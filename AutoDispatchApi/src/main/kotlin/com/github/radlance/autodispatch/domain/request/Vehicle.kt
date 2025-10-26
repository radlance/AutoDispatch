package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val id: Int,
    val model: String,
    val licencePlate: String,
    val year: Int,
    val mileage: Int,
    val fuelType: String,
    val status: String,
    val lastServiceDate: String?,
    val isActive: Boolean
)
