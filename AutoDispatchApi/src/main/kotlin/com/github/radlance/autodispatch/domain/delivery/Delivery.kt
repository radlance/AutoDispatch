package com.github.radlance.autodispatch.domain.delivery

import com.github.radlance.autodispatch.domain.request.Point
import com.github.radlance.autodispatch.domain.common.Status
import kotlinx.serialization.Serializable

@Serializable
data class Delivery(
    val id: Int,
    val status: Status?,
    val loadingPoint: Point,
    val unloadingPoint: Point,
    val cargoWeight: Double?,
    val cargoTypeName: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val requestNumber: String?
)
