package com.github.radlance.autodispatch.request.change.domain

data class ChangeRequest(
    val loadingAddress: String?,
    val loadingLat: Double,
    val loadingLon: Double,
    val unloadingAddress: String?,
    val unloadingLat: Double,
    val unloadingLon: Double,
    val cargoTypeId: Int,
    val cargoWeight: Double,
    val cargoVolume: Double?,
    val cargoDescription: String?,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String?,
    val originId: Int?,
    val destinationId: Int?,
    val transportationDescription: String?
)
