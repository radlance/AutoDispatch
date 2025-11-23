package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.delivery.core.data.DeliveryDto
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.details.data.DeliveryDetailedDto
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed

fun DeliveryDto.toDelivery(): Delivery {
    return Delivery(
        id = id,
        status = status.toRequestStatus(),
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        cargoWeight = cargoWeight,
        cargoVolume = cargoVolume,
        cargoTypeName = cargoTypeName,
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt?.toLocalDateTimeFromUtc(),
        requestNumber = requestNumber
    )
}

fun DeliveryDetailedDto.toDeliveryDetailed(): DeliveryDetailed {
    return DeliveryDetailed(
        id = id,
        status = status.toRequestStatus(),
        origin = origin,
        destination = destination,
        transportationDescription = transportationDescription,
        cargo = cargo.toCargo(),
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        dispatcherFullName = dispatcherFullName,
        dispatcherPhoneNumber = dispatcherPhoneNumber,
        customer = customer.toCustomer(),
        vehicle = vehicle.toVehicleFilter(),
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt?.toLocalDateTimeFromUtc(),
        requestNumber = requestNumber
    )
}