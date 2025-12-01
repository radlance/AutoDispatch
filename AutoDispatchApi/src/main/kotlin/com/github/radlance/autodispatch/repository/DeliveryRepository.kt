package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.*
import com.github.radlance.autodispatch.domain.delivery.Delivery
import com.github.radlance.autodispatch.domain.delivery.DeliveryDetailed
import com.github.radlance.autodispatch.domain.request.*
import com.github.radlance.autodispatch.exception.DeliveryCanceledException
import com.github.radlance.autodispatch.exception.DeliveryForbiddenException
import com.github.radlance.autodispatch.exception.DeliveryNotFoundException
import com.github.radlance.autodispatch.exception.DriverBusyException
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentTimestampWithTimeZone

class DeliveryRepository {

    private fun mapDeliveryRow(row: ResultRow): Delivery = Delivery(
        id = row[RequestTable.id].value,
        requestNumber = row[RequestTable.requestNumber],
        status = RequestStatus(
            id = row[RequestStatusTable.id].value,
            name = row[RequestStatusTable.name]
        ),
        loadingPoint = Point(
            address = row[RequestTable.loadingAddress],
            lat = row[RequestTable.loadingLat],
            lon = row[RequestTable.loadingLon]
        ),
        unloadingPoint = Point(
            address = row[RequestTable.unloadingAddress],
            lat = row[RequestTable.unloadingLat],
            lon = row[RequestTable.unloadingLon]
        ),
        cargoWeight = row[RequestTable.cargoWeight],
        cargoVolume = row[RequestTable.cargoVolume],
        cargoTypeName = row[CargoTypeTable.name.alias("cargo_type_name")],
        createdAt = row[RequestTable.createdAt]?.toString(),
        updatedAt = row[RequestTable.updatedAt]?.toString()
    )

    suspend fun deliveries(driverLogin: String): List<Delivery> = loggedTransaction {
        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val joinQuery = RequestTable
            .join(AssignmentTable, JoinType.INNER, RequestTable.id, AssignmentTable.requestId)
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id) // Добавлено

        val selectCols = listOf(
            RequestTable.id,
            RequestTable.requestNumber,
            RequestStatusTable.id,
            RequestStatusTable.name,
            RequestTable.loadingAddress,
            RequestTable.loadingLat,
            RequestTable.loadingLon,
            RequestTable.unloadingAddress,
            RequestTable.unloadingLat,
            RequestTable.unloadingLon,
            RequestTable.cargoWeight,
            RequestTable.cargoVolume,
            CargoTypeTable.name.alias("cargo_type_name"),
            RequestTable.createdAt,
            RequestTable.updatedAt
        )

        joinQuery
            .select(selectCols)
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (RequestTable.statusId inList listOf(2, 3, 6, 7))
            }
            .orderBy(Coalesce(RequestTable.updatedAt, RequestTable.createdAt), SortOrder.DESC_NULLS_LAST)
            .map { mapDeliveryRow(it) }
    }

    suspend fun delivery(driverLogin: String, deliveryId: Int): DeliveryDetailed = loggedTransaction {
        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")
        val dispatcherUser = UserTable.alias("dispatcher_user")

        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val query = RequestTable
            .join(originCity, JoinType.LEFT, RequestTable.originId, originCity[CityTable.id])
            .join(destCity, JoinType.LEFT, RequestTable.destinationId, destCity[CityTable.id])
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id)
            .join(CustomerTable, JoinType.LEFT, RequestTable.customerId, CustomerTable.id)
            .join(dispatcherUser, JoinType.LEFT, RequestTable.createdById, dispatcherUser[UserTable.id])
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .join(UserTable, JoinType.LEFT, AssignmentTable.driverId, UserTable.id)
            .join(DriverTable, JoinType.LEFT, UserTable.id, DriverTable.userId)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)

        val row = query
            .select(
                RequestTable.id,
                RequestTable.requestNumber,
                RequestTable.transportationDescription,
                RequestStatusTable.id,
                RequestStatusTable.name,
                originCity[CityTable.name].alias("origin_name"),
                destCity[CityTable.name].alias("destination_name"),
                RequestTable.loadingAddress,
                RequestTable.loadingLat,
                RequestTable.loadingLon,
                RequestTable.unloadingAddress,
                RequestTable.unloadingLat,
                RequestTable.unloadingLon,
                CargoTypeTable.id,
                CargoTypeTable.name.alias("cargo_type_name"),
                RequestTable.cargoWeight,
                RequestTable.cargoVolume,
                RequestTable.cargoDescription,
                RequestTable.createdAt,
                RequestTable.updatedAt,
                CustomerTable.id,
                CustomerTable.organizationName,
                CustomerTable.email,
                CustomerTable.phoneNumber,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                dispatcherUser[UserTable.fullName].alias("dispatcher_full_name"),
                dispatcherUser[UserTable.phoneNumber].alias("dispatcher_phone_number"),
                AssignmentTable.driverId
            )
            .where { RequestTable.id eq deliveryId }
            .limit(1)
            .firstOrNull()

        if (row == null) {
            throw DeliveryNotFoundException("Доставка не найдена")
        }

        val assignedDriverId = row.getOrNull(AssignmentTable.driverId)?.value

        if (assignedDriverId != driverId) {
            throw DeliveryForbiddenException("Доставка ${row[RequestTable.requestNumber]} недоступна")
        }

        val delivery = DeliveryDetailed(
            id = row[RequestTable.id].value,
            status = RequestStatus(
                id = row[RequestStatusTable.id].value,
                name = row[RequestStatusTable.name]
            ),
            origin = row[originCity[CityTable.name].alias("origin_name")],
            destination = row[destCity[CityTable.name].alias("destination_name")],
            transportationDescription = row[RequestTable.transportationDescription],
            cargo = Cargo(
                type = CargoType(
                    id = row[CargoTypeTable.id].value,
                    name = row[CargoTypeTable.name.alias("cargo_type_name")]
                ),
                weight = row[RequestTable.cargoWeight],
                volume = row[RequestTable.cargoVolume],
                description = row[RequestTable.cargoDescription]
            ),
            loadingPoint = Point(
                address = row[RequestTable.loadingAddress],
                lat = row[RequestTable.loadingLat],
                lon = row[RequestTable.loadingLon]
            ),
            unloadingPoint = Point(
                address = row[RequestTable.unloadingAddress],
                lat = row[RequestTable.unloadingLat],
                lon = row[RequestTable.unloadingLon]
            ),
            dispatcherFullName = row[dispatcherUser[UserTable.fullName].alias("dispatcher_full_name")],
            dispatcherPhoneNumber = row[dispatcherUser[UserTable.phoneNumber].alias("dispatcher_phone_number")],
            customer = Customer(
                id = row[CustomerTable.id].value,
                organizationName = row[CustomerTable.organizationName],
                email = row[CustomerTable.email],
                phoneNumber = row[CustomerTable.phoneNumber]
            ),
            vehicle = row.getOrNull(VehicleTable.id)?.let {
                VehicleFilter(
                    id = it.value,
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate]
                )
            },
            createdAt = row[RequestTable.createdAt]?.toString(),
            updatedAt = row[RequestTable.updatedAt]?.toString(),
            requestNumber = row[RequestTable.requestNumber]
        )

        return@loggedTransaction delivery
    }

    suspend fun startDelivery(deliveryId: Int, driverLogin: String) = loggedTransaction {
        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val activeAssignmentsCount = AssignmentTable
            .select(AssignmentTable.id)
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (AssignmentTable.startedAt.isNotNull()) and
                        (AssignmentTable.completedAt.isNull())
            }
            .count()

        if (activeAssignmentsCount > 0) {
            throw DriverBusyException()
        }

        val requestData = RequestTable
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .select(RequestTable.statusId, RequestTable.requestNumber, AssignmentTable.driverId)
            .where { RequestTable.id eq deliveryId }
            .firstOrNull()

        if (requestData == null) {
            throw DeliveryNotFoundException("Доставка не найдена")
        }

        val assignedDriverId = requestData.getOrNull(AssignmentTable.driverId)?.value
        if (assignedDriverId != driverId) {
            throw DeliveryForbiddenException("Доставка ${requestData[RequestTable.requestNumber]} недоступна")
        }

        val currentStatusId = requestData[RequestTable.statusId].value
        val requestNumber = requestData[RequestTable.requestNumber]!!
        if (currentStatusId == 5) {
            throw DeliveryCanceledException("Доставка $requestNumber отменена.")
        } else if (currentStatusId != 2) {
            throw DeliveryCanceledException("Доставка $requestNumber отменена или недоступна.")
        }

        RequestTable.update({ RequestTable.id eq deliveryId }) {
            it[statusId] = 3
        }

        AssignmentTable.update({ AssignmentTable.requestId eq deliveryId }) {
            it[startedAt] = CurrentTimestampWithTimeZone
        }
    }

    suspend fun uploadDeliveryDocuments(deliveryId: Int, driverLogin: String, imageUrls: List<String>) = loggedTransaction {
        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val requestData = RequestTable
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .select(
                RequestTable.statusId,
                RequestTable.requestNumber,
                AssignmentTable.driverId,
                AssignmentTable.id
            )
            .where { RequestTable.id eq deliveryId }
            .firstOrNull()

        if (requestData == null) {
            throw DeliveryNotFoundException("Доставка не найдена")
        }

        val assignedDriverId = requestData.getOrNull(AssignmentTable.driverId)?.value
        if (assignedDriverId != driverId) {
            throw DeliveryForbiddenException("Доставка ${requestData[RequestTable.requestNumber]} недоступна")
        }

        val currentStatusId = requestData[RequestTable.statusId].value
        val requestNumber = requestData[RequestTable.requestNumber]!!
        if (currentStatusId == 5) {
            throw DeliveryCanceledException("Доставка $requestNumber отменена.")
        } else if (currentStatusId != 3) {
            throw DeliveryCanceledException("Доставка $requestNumber отменена или недоступна.")
        }

        RequestTable.update({ RequestTable.id eq deliveryId }) {
            it[statusId] = 6
        }

        AssignmentTable.update({ AssignmentTable.requestId eq deliveryId }) {
            it[completedAt] = CurrentTimestampWithTimeZone
        }
        val assignmentId = requestData[AssignmentTable.id].value

        DeliveryDocumentTable.batchInsert(imageUrls) { url ->
            this[DeliveryDocumentTable.assignmentId] = assignmentId
            this[DeliveryDocumentTable.imageUrl] = url
        }
    }
}

// TODO переименовать эндпоинт и сделать не завершение заявки а статус проверки у нее
// TODO дописать промпт на статус необходимости проверки
// TODO показывать у водителя отклоненные заявки и заявки с ожиданием
// TODO переделать скрин "успеха"
// TODO сделать проверку документов у диспетчера