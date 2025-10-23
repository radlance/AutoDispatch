package com.github.radlance.autodispatch.request.domain

import kotlinx.datetime.LocalDateTime

data class Request(
    val id: Int,
    val statusName: String?,
    val origin: String?,
    val destination: String?,
    val tripDate: String?,
    val cargoTypeName: String?,
    val cargoWeight: Double?,
    val cargoVolume: Double?,
    val cargoDescription: String?,
    val loadingPoint: String?,
    val unloadingPoint: String?,
    val startedTripAt: LocalDateTime?,
    val endedTripAt: LocalDateTime?,
    val driverFullName: String?,
    val organizationName: String?,
    val organizationPhoneNumber: String?,
    val organizationEmail: String?,
    val vehicleInfo: String?
)
