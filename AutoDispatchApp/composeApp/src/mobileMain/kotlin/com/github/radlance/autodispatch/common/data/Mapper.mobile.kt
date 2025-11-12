package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.delivery.core.data.DeliveryDto
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import kotlinx.datetime.LocalDateTime

fun DeliveryDto.toDelivery(): Delivery {
    return Delivery(
        id = id,
        status = status.toRequestStatus(),
        loadingPoint = loadingPoint,
        unloadingPoint = unloadingPoint,
        cargoWeight = cargoWeight,
        cargoVolume = cargoVolume,
        cargoTypeName = cargoTypeName,
        createdAt = createdAt.removeSuffix("Z").let { LocalDateTime.parse(it) },
        updatedAt = updatedAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) },
        requestNumber = requestNumber
    )
}