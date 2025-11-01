package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestDto(
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
    val startedTripAt: String?,
    val endedTripAt: String?,
    val driverFullName: String?,
    val organizationName: String,
    val organizationPhoneNumber: String?,
    val organizationEmail: String,
    val vehicleInfo: String?,
    val createdAt: String,
    val requestNumber: String
)


