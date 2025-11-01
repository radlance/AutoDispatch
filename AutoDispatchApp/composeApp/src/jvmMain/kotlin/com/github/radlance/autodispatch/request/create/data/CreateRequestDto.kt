package com.github.radlance.autodispatch.request.create.data

import kotlinx.serialization.Serializable

@Serializable
data class CreateRequestDto(
    val loadingPoint: String,
    val unloadingPoint: String,
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
