package com.github.radlance.autodispatch.domain.driver

import kotlinx.serialization.Serializable

@Serializable
data class DriverWithoutCar(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val totalDeliveries: Int
)
