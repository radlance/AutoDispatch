package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.profile.data.UserDto
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.reuqest.core.data.RequestDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestStatusDto
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import kotlinx.datetime.LocalDateTime

internal fun UserDto.toUser(): User {
    return User(
        id = id,
        login = login,
        fullName = fullName,
        phoneNumber = phoneNumber
    )
}

fun RequestDto.toRequest(): Request {
    return Request(
        id = id,
        requestNumber = requestNumber,
        status = status.toRequestStatus(),
        origin = origin,
        destination = destination,
        cargoTypeName = cargoTypeName,
        cargoWeight = cargoWeight,
        cargoVolume = cargoVolume,
        cargoDescription = cargoDescription,
        loadingPoint = loadingPoint,
        unloadingPoint = unloadingPoint,
        startedTripAt = startedTripAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) },
        endedTripAt = endedTripAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) },
        driverId = driverId,
        driverFullName = driverFullName,
        organizationName = organizationName,
        organizationPhoneNumber = organizationPhoneNumber,
        organizationEmail = organizationEmail,
        vehicleInfo = vehicleInfo,
        transportationDescription = transportationDescription,
        createdAt = createdAt.removeSuffix("Z").let { LocalDateTime.parse(it) },
        updatedAt = updatedAt?.removeSuffix("Z")?.let { LocalDateTime.parse(it) },
    )
}

fun RequestStatusDto.toRequestStatus(): RequestStatus {
    return RequestStatus(
        id = id,
        name = name
    )
}