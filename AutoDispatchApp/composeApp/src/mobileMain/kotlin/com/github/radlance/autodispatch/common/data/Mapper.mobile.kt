package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.domain.toRequestStatus
import com.github.radlance.autodispatch.delivery.core.data.DeliveryDto
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.delivery.details.data.DeliveryDetailedDto
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.profile.data.ProfileDetailsDto
import com.github.radlance.autodispatch.profile.domain.ProfileDetails

fun DeliveryDto.toDelivery(): Delivery {
    return Delivery(
        id = id,
        status = status.id.toRequestStatus(),
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        cargoWeight = cargoWeight,
        cargoTypeName = cargoTypeName,
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt?.toLocalDateTimeFromUtc(),
        requestNumber = requestNumber
    )
}

fun DeliveryDetailedDto.toDeliveryDetailed(): DeliveryDetailed {
    return DeliveryDetailed(
        id = id,
        status = status.id.toRequestStatus(),
        origin = origin,
        destination = destination,
        transportationDescription = transportationDescription,
        cargo = cargo.toCargo(),
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        dispatcherFullName = dispatcherFullName,
        dispatcherPhoneNumber = dispatcherPhoneNumber,
        customer = customer.toCustomer(),
        vehicle = vehicle.toVehicle(),
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt.toLocalDateTimeFromUtc(),
        requestNumber = requestNumber,
        rejectionReason = rejectionReason,
        documents = documents.map { it.toDeliveryDocument() }
    )
}

fun ProfileDetailsDto.toProfileDetails(): ProfileDetails {
    return ProfileDetails(
        fullName = fullName,
        avatarUrl = avatarUrl?.asImageUrl(),
        deliveriesStats = deliveriesStats.toDeliveriesStats(),
        phoneNumber = phoneNumber,
        vehicle = vehicle?.toVehicle()
    )
}

fun ListPaginatedResultDto<DeliveryDto>.toDeliveryListPaginatedResult(): ListPaginatedResult<Delivery> {
    return ListPaginatedResult(
        items = items.map { it.toDelivery() },
        hasMore = hasMore
    )
}