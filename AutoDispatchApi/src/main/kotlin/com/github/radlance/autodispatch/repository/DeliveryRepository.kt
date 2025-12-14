package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.CargoTypeTable
import com.github.radlance.autodispatch.database.table.CityTable
import com.github.radlance.autodispatch.database.table.CustomerTable
import com.github.radlance.autodispatch.database.table.DeliveryDocumentTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.ListPaginatedResult
import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.delivery.Delivery
import com.github.radlance.autodispatch.domain.delivery.DeliveryDetailed
import com.github.radlance.autodispatch.domain.delivery.DeliveryDocument
import com.github.radlance.autodispatch.domain.request.Cargo
import com.github.radlance.autodispatch.domain.request.CargoType
import com.github.radlance.autodispatch.domain.request.Customer
import com.github.radlance.autodispatch.domain.request.Point
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.exception.DeliveryCanceledException
import com.github.radlance.autodispatch.exception.DeliveryForbiddenException
import com.github.radlance.autodispatch.exception.DeliveryNotFoundException
import com.github.radlance.autodispatch.exception.DriverBusyException
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.update

class DeliveryRepository {

    private fun mapDeliveryRow(row: ResultRow): Delivery = Delivery(
        id = row[RequestTable.id].value,
        requestNumber = row[RequestTable.requestNumber],
        status = Status(
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

    suspend fun deliveries(
        driverLogin: String,
        page: Int,
        pageSie: Int
    ): ListPaginatedResult<Delivery> = fetchDeliveries(
        driverLogin = driverLogin,
        statusIds = listOf(2, 3, 6, 7),
        pageSize = pageSie,
        page = page
    )

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
            .join(VehicleTable, JoinType.LEFT, AssignmentTable.vehicleId, VehicleTable.id)

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
                RequestTable.rejectionReason,
                CustomerTable.id,
                CustomerTable.organizationName,
                CustomerTable.email,
                CustomerTable.phoneNumber,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
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

        val documents = DeliveryDocumentTable
            .innerJoin(AssignmentTable)
            .select(
                DeliveryDocumentTable.id,
                DeliveryDocumentTable.imageUrl,
                DeliveryDocumentTable.uploadedAt
            )
            .where { AssignmentTable.requestId eq deliveryId }
            .orderBy(DeliveryDocumentTable.uploadedAt, SortOrder.DESC)
            .map { docRow ->
                DeliveryDocument(
                    id = docRow[DeliveryDocumentTable.id].value,
                    imageUrl = docRow[DeliveryDocumentTable.imageUrl],
                    uploadedAt = docRow[DeliveryDocumentTable.uploadedAt]?.toString()
                )
            }

        val delivery = DeliveryDetailed(
            id = row[RequestTable.id].value,
            status = Status(
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
                Vehicle(
                    id = it.value,
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate],
                    payloadCapacity = row[VehicleTable.payloadCapacity]
                )
            },
            createdAt = row[RequestTable.createdAt]?.toString(),
            updatedAt = row[RequestTable.updatedAt]?.toString(),
            requestNumber = row[RequestTable.requestNumber],
            rejectionReason = row[RequestTable.rejectionReason],
            documents = documents
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

    suspend fun uploadDeliveryDocuments(deliveryId: Int, driverLogin: String, imageUrls: List<String>) =
        loggedTransaction {

            val assignmentId = validateAndGetAssignmentId(
                deliveryId,
                driverLogin,
                allowedStatuses = listOf(3),
                canceledStatus = 5,
                unexpectedStatusMessage = { number, _ ->
                    "Доставка $number отменена или недоступна."
                }
            )

            RequestTable.update({ RequestTable.id eq deliveryId }) {
                it[statusId] = 6
            }

            AssignmentTable.update({ AssignmentTable.requestId eq deliveryId }) {
                it[completedAt] = CurrentTimestampWithTimeZone
            }

            DeliveryDocumentTable.batchInsert(imageUrls) { url ->
                this[DeliveryDocumentTable.assignmentId] = assignmentId
                this[DeliveryDocumentTable.imageUrl] = url
            }
        }

    suspend fun retakeDeliveryDocuments(deliveryId: Int, driverLogin: String, imageUrls: List<String>) =
        loggedTransaction {

            val assignmentId = validateAndGetAssignmentId(
                deliveryId,
                driverLogin,
                allowedStatuses = listOf(7),
                canceledStatus = 5,
                unexpectedStatusMessage = { number, status ->
                    "Невозможно пересдать документы для доставки $number. Текущий статус: $status"
                }
            )

            DeliveryDocumentTable.deleteWhere {
                this.assignmentId eq assignmentId
            }

            DeliveryDocumentTable.batchInsert(imageUrls) { url ->
                this[DeliveryDocumentTable.assignmentId] = assignmentId
                this[DeliveryDocumentTable.imageUrl] = url
            }

            RequestTable.update({ RequestTable.id eq deliveryId }) {
                it[statusId] = 6
                it[rejectionReason] = null
            }

            AssignmentTable.update({ AssignmentTable.requestId eq deliveryId }) {
                it[completedAt] = CurrentTimestampWithTimeZone
            }
        }

    suspend fun deliveryHistory(
        driverLogin: String,
        pageSize: Int,
        page: Int
    ): ListPaginatedResult<Delivery> = fetchDeliveries(
        driverLogin = driverLogin,
        statusIds = listOf(4, 5),
        pageSize = pageSize,
        page = page
    )

    private suspend fun validateAndGetAssignmentId(
        deliveryId: Int,
        driverLogin: String,
        allowedStatuses: List<Int>,
        canceledStatus: Int,
        unexpectedStatusMessage: (requestNumber: String, status: Int) -> String
    ): Int = loggedTransaction {

        val driverId = UserTable
            .select(UserTable.id)
            .where { UserTable.login eq driverLogin }
            .first()[UserTable.id].value

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
            ?: throw DeliveryNotFoundException("Доставка не найдена")

        val assignedDriverId = requestData.getOrNull(AssignmentTable.driverId)?.value
        if (assignedDriverId != driverId) {
            throw DeliveryForbiddenException("Доставка ${requestData[RequestTable.requestNumber]} недоступна")
        }

        val currentStatus = requestData[RequestTable.statusId].value
        val requestNumber = requestData[RequestTable.requestNumber]!!

        if (currentStatus == canceledStatus) {
            throw DeliveryCanceledException("Доставка $requestNumber отменена.")
        }

        if (currentStatus !in allowedStatuses) {
            throw DeliveryCanceledException(unexpectedStatusMessage(requestNumber, currentStatus))
        }

        requestData[AssignmentTable.id].value
    }

    private suspend fun fetchDeliveries(
        driverLogin: String,
        statusIds: List<Int>,
        page: Int,
        pageSize: Int
    ): ListPaginatedResult<Delivery> = loggedTransaction {
        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val joinQuery = RequestTable
            .join(AssignmentTable, JoinType.INNER, RequestTable.id, AssignmentTable.requestId)
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id)

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

        val offset = (page - 1L) * pageSize

        val deliveriesRaw = joinQuery
            .select(selectCols)
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (RequestTable.statusId inList statusIds)
            }
            .orderBy(
                Coalesce(
                    RequestTable.updatedAt,
                    RequestTable.createdAt
                ),
                SortOrder.DESC_NULLS_LAST
            )
            .limit(pageSize + 1)
            .offset(offset)
            .map { mapDeliveryRow(it) }
        val hasMore = deliveriesRaw.size > pageSize
        val deliveries = if (hasMore) {
            deliveriesRaw.dropLast(1)
        } else {
            deliveriesRaw
        }
        ListPaginatedResult(
            items = deliveries,
            hasMore = hasMore
        )
    }
}