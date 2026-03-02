package com.github.radlance.autodispatch.delivery.core.data

import com.github.radlance.autodispatch.common.data.StatusDto
import com.github.radlance.autodispatch.request.core.data.PointDto
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDto(
    val id: Int,
    val status: StatusDto,
    val loadingPoint: PointDto,
    val unloadingPoint: PointDto,
    val cargoWeight: Double,
    val cargoTypeName: String,
    val plannedUnloadingAt: String? = null,
    val actualUnloadingAt: String? = null,
    val createdAt: String,
    val updatedAt: String?,
    val requestNumber: String,
)
