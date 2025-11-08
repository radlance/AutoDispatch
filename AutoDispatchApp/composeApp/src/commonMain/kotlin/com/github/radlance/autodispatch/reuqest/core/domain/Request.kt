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
    val cargoTypeName: String,
    val cargoWeight: Double,
    val cargoVolume: Double?,
    val cargoDescription: String?,
    val loadingPoint: String,
    val unloadingPoint: String,
    val startedTripAt: LocalDateTime?,
    val endedTripAt: LocalDateTime?,
    val driverId: Int?,
    val driverFullName: String?,
    val organizationName: String,
    val organizationPhoneNumber: String?,
    val organizationEmail: String,
    val vehicleInfo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val requestNumber: String
)