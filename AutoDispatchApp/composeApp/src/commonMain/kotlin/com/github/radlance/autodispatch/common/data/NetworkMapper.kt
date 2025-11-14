package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.profile.data.UserDto
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.reuqest.core.data.CargoDto
import com.github.radlance.autodispatch.reuqest.core.data.CargoTypeDto
import com.github.radlance.autodispatch.reuqest.core.data.CustomerDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestStatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleFilterDto
import com.github.radlance.autodispatch.reuqest.core.domain.Cargo
import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.Customer
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import com.github.radlance.autodispatch.reuqest.core.domain.VehicleFilter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
        cargo = cargo.toCargo(),
        loadingPoint = loadingPoint,
        unloadingPoint = unloadingPoint,
        driverId = driverId,
        driverFullName = driverFullName,
        customer = customer.toCustomer(),
        vehicleInfo = vehicleInfo,
        transportationDescription = transportationDescription,
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt?.toLocalDateTimeFromUtc(),
    )
}

fun RequestStatusDto.toRequestStatus(): RequestStatus {
    return RequestStatus(
        id = id,
        name = name
    )
}

fun CustomerDto.toCustomer(): Customer {
    return Customer(
        id = id,
        organizationName = organizationName,
        email = email,
        phoneNumber = phoneNumber
    )
}

fun CargoDto.toCargo(): Cargo {
    return Cargo(
        type = type.toCargoType(),
        weight = weight,
        volume = volume,
        description = description
    )
}

fun CargoTypeDto.toCargoType(): CargoType {
    return CargoType(
        id = id,
        name = name
    )
}

fun VehicleFilterDto.toVehicleFilter(): VehicleFilter {
    return VehicleFilter(
        id = id,
        model = model,
        licensePlate = licensePlate,
    )
}

@OptIn(ExperimentalTime::class)
fun String.toLocalDateTimeFromUtc(): LocalDateTime {
    val instant = Instant.parse(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}