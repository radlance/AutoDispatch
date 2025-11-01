package com.github.radlance.autodispatch.request.core.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int,
    val statusName: String,
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
    val driverFullName: String?,
    val organizationName: String,
    val organizationPhoneNumber: String?,
    val organizationEmail: String,
    val vehicleInfo: String?,
    val createdAt: LocalDateTime,
    val requestNumber: String
)