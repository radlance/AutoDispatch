package com.github.radlance.autodispatch.domain.delivery

import com.github.radlance.autodispatch.domain.request.RequestStatus
import kotlinx.serialization.Serializable

@Serializable
data class Delivery(
    val id: Int,
    val status: RequestStatus?,
    val loadingPoint: String?,
    val unloadingPoint: String?,
    val cargoWeight: Double?,
    val cargoVolume: Double?,
    val cargoTypeName: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val requestNumber: String?
)
