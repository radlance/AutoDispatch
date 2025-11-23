package com.github.radlance.autodispatch.delivery.core.data

import com.github.radlance.autodispatch.reuqest.core.data.PointDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestStatusDto
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDto(
    val id: Int,
    val status: RequestStatusDto,
    val loadingPoint: PointDto,
    val unloadingPoint: PointDto,
    val cargoWeight: Double,
    val cargoVolume: Double?,
    val cargoTypeName: String,
    val createdAt: String,
    val updatedAt: String?,
    val requestNumber: String,
)