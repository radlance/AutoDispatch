package com.github.radlance.autodispatch.vehicle.assignment.domain

data class DriverWithoutVehicle(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val totalDeliveries: Int
)
