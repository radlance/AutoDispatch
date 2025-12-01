package com.github.radlance.autodispatch.reuqest.core.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int,
    val status: RequestStatus,
    val origin: String,
    val destination: String,
    val transportationDescription: String?,
    val cargo: Cargo,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val driverId: Int?,
    val driverFullName: String?,
    val customer: Customer,
    val vehicleInfo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val requestNumber: String,
    val documents: List<String> = emptyList()
)