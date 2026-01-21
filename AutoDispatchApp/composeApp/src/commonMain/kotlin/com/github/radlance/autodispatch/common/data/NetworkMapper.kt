package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.auth.data.LoginResponseDto
import com.github.radlance.autodispatch.auth.domain.LoginResponse
import com.github.radlance.autodispatch.common.domain.toRequestStatus
import com.github.radlance.autodispatch.di.CurrentIp
import com.github.radlance.autodispatch.profile.data.DeliveriesStatsDto
import com.github.radlance.autodispatch.profile.data.UserDto
import com.github.radlance.autodispatch.profile.domain.DeliveriesStats
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.request.core.data.CargoDto
import com.github.radlance.autodispatch.request.core.data.CargoTypeDto
import com.github.radlance.autodispatch.request.core.data.CustomerDto
import com.github.radlance.autodispatch.request.core.data.DeliveryDocumentDto
import com.github.radlance.autodispatch.request.core.data.PointDto
import com.github.radlance.autodispatch.request.core.data.RequestDto
import com.github.radlance.autodispatch.request.core.data.VehicleDto
import com.github.radlance.autodispatch.request.core.domain.Cargo
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.DeliveryDocument
import com.github.radlance.autodispatch.request.core.domain.Point
import com.github.radlance.autodispatch.request.core.domain.Request
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun List<ByteArray>.createImageFormData(): List<PartData> = formData {
    this@createImageFormData.forEachIndexed { index, bytes ->
        append(
            "file$index",
            bytes,
            Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(
                    HttpHeaders.ContentDisposition,
                    "form-data; name=\"file$index\"; filename=\"photo_$index.jpg\""
                )
            }
        )
    }
}

fun ByteArray.createImageFormData(
    fieldName: String = "file",
    fileName: String = "photo.jpg",
    contentType: String = "image/jpeg"
): List<PartData> = formData {
    append(
        fieldName,
        this@createImageFormData,
        Headers.build {
            append(HttpHeaders.ContentType, contentType)
            append(
                HttpHeaders.ContentDisposition,
                "form-data; name=\"$fieldName\"; filename=\"$fileName\""
            )
        }
    )
}


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
        status = status.id.toRequestStatus(),
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
        imageUrl = imageUrl.asImageUrl(),
        uploadedAt = uploadedAt.toLocalDateTimeFromUtc()
    )
}

fun String.asImageUrl(): String {
    return "http://$CurrentIp/$this"
}

fun DeliveriesStatsDto.toDeliveriesStats(): DeliveriesStats {
    return DeliveriesStats(
        totalCount = totalCount,
        activeCount = activeCount,
        completedCount = completedCount,
        canceledCount = canceledCount,
        onCheckCount = onCheckCount,
        rejectedCount = rejectedCount
    )
}