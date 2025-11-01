package com.github.radlance.autodispatch.request.create.domain

import kotlinx.serialization.Serializable

@Serializable
data class CreateRequest(
    val loadingPoint: String,
    val unloadingPoint: String,
    val cargoTypeId: Int,
    val cargoWeight: Double,
    val cargoVolume: Double,
    val cargoDescription: String?,
    val customerId: Int,
    val startedTripAt: String?,
    val endedTripAt: String?,
    val createdAt: String?,
    val originId: Int?,
    val destinationId: Int?,
    val requestNumber: String?,
    val transportationDescription: String?
)
