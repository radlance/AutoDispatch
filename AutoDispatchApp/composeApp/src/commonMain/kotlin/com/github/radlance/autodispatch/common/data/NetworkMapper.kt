package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.auth.data.LoginResponseDto
import com.github.radlance.autodispatch.auth.domain.LoginResponse
import com.github.radlance.autodispatch.di.CurrentIp
import com.github.radlance.autodispatch.profile.data.UserDto
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.reuqest.core.data.CargoDto
import com.github.radlance.autodispatch.reuqest.core.data.CargoTypeDto
import com.github.radlance.autodispatch.reuqest.core.data.CustomerDto
import com.github.radlance.autodispatch.reuqest.core.data.DeliveryDocumentDto
import com.github.radlance.autodispatch.reuqest.core.data.PointDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestDto
import com.github.radlance.autodispatch.reuqest.core.data.RequestStatusDto
import com.github.radlance.autodispatch.reuqest.core.data.VehicleDto
import com.github.radlance.autodispatch.reuqest.core.domain.Cargo
import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.Customer
import com.github.radlance.autodispatch.reuqest.core.domain.DeliveryDocument
import com.github.radlance.autodispatch.reuqest.core.domain.Point
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle
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

fun LoginResponseDto.toLoginResponse(): LoginResponse {
    return LoginResponse(
        accessToken = accessToken,
        roleId = roleId
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
        loadingPoint = loadingPoint.toPoint(),
        unloadingPoint = unloadingPoint.toPoint(),
        driverId = driverId,
        driverFullName = driverFullName,
        customer = customer.toCustomer(),
        vehicle = vehicle?.toVehicle(),
        transportationDescription = transportationDescription,
        createdAt = createdAt.toLocalDateTimeFromUtc(),
        updatedAt = updatedAt?.toLocalDateTimeFromUtc(),
        documents = documents.map { it.toDeliveryDocument() }
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

fun VehicleDto.toVehicle(): Vehicle {
    return Vehicle(
        id = id,
        model = model,
        licensePlate = licensePlate,
        payloadCapacity = payloadCapacity
    )
}

@OptIn(ExperimentalTime::class)
fun String.toLocalDateTimeFromUtc(): LocalDateTime {
    val instant = Instant.parse(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun PointDto.toPoint(): Point {
    return Point(
        address = address,
        lat = lat,
        lon = lon
    )
}

fun DeliveryDocumentDto.toDeliveryDocument(): DeliveryDocument {
    return DeliveryDocument(
        id = id,
        imageUrl = "http://$CurrentIp/$imageUrl",
        uploadedAt = uploadedAt.toLocalDateTimeFromUtc()
    )
}