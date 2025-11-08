package com.github.radlance.autodispatch.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Int,
    val status: RequestStatus?,
    val origin: String?,
    val destination: String?,
    val transportationDescription: String?,
    val cargoTypeName: String?,
    val cargoWeight: Double?,
    val cargoVolume: Double?,
    val cargoDescription: String?,
    val loadingPoint: String?,
    val unloadingPoint: String?,
    val startedTripAt: String?,
    val endedTripAt: String?,
    val driverId: Int?,
    val driverFullName: String?,
    val organizationName: String?,
    val organizationPhoneNumber: String?,
    val organizationEmail: String?,
    val vehicleInfo: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val requestNumber: String?,
)

@Serializable
data class PaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)