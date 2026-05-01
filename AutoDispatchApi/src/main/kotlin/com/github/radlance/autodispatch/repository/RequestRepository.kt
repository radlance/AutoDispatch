package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.entity.CargoTypeEntity
import com.github.radlance.autodispatch.database.entity.CityEntity
import com.github.radlance.autodispatch.database.entity.CustomerEntity
import com.github.radlance.autodispatch.database.entity.RequestStatusEntity
import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.CargoTypeTable
import com.github.radlance.autodispatch.database.table.CityTable
import com.github.radlance.autodispatch.database.table.CustomerTable
import com.github.radlance.autodispatch.database.table.DeliveryDocumentTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.ListPaginatedResult
import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.common.TablePaginatedResult
import com.github.radlance.autodispatch.domain.delivery.DeliveryDocument
import com.github.radlance.autodispatch.domain.request.Cargo
import com.github.radlance.autodispatch.domain.request.CargoType
import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.domain.request.Customer
import com.github.radlance.autodispatch.domain.request.DriverRequest
import com.github.radlance.autodispatch.domain.request.RequestFilters
import com.github.radlance.autodispatch.domain.request.NotificationContacts
import com.github.radlance.autodispatch.domain.request.Point
import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.domain.request.RequestEmailView
import com.github.radlance.autodispatch.domain.request.UserFilter
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.exception.StateConflictException
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.time.format.DateTimeFormatter

class RequestRepository(
    private val driverScheduleGuard: DriverScheduleGuard
) {

    private fun joinBaseQuery(originCity: Alias<CityTable>, destCity: Alias<CityTable>): Join =
        RequestTable
            .join(originCity, JoinType.LEFT, RequestTable.originId, originCity[CityTable.id])
            .join(destCity, JoinType.LEFT, RequestTable.destinationId, destCity[CityTable.id])
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id)
            .join(CustomerTable, JoinType.LEFT, RequestTable.customerId, CustomerTable.id)
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .join(UserTable, JoinType.LEFT, AssignmentTable.driverId, UserTable.id)
            .join(DriverTable, JoinType.LEFT, UserTable.id, DriverTable.userId)
            .join(VehicleTable, JoinType.LEFT, AssignmentTable.vehicleId, VehicleTable.id)

    private fun selectColumns(
        originCity: Alias<CityTable>,
        destCity: Alias<CityTable>
    ): List<Expression<*>> = listOf(
        RequestTable.id,
        RequestTable.requestNumber,
        RequestTable.transportationDescription,
        RequestTable.plannedLoadingAt,
        RequestTable.plannedUnloadingAt,
        RequestTable.arrivedLoadingAt,
        RequestTable.arrivedUnloadingAt,
        RequestTable.actualLoadingAt,
        RequestTable.actualUnloadingAt,
        RequestStatusTable.id,
        RequestStatusTable.name,
        originCity[CityTable.name].alias("origin_name"),
        destCity[CityTable.name].alias("destination_name"),
        RequestTable.createdAt,
        RequestTable.updatedAt,
        CargoTypeTable.id,
        CargoTypeTable.name.alias("cargo_type_name"),
        RequestTable.cargoWeight,
        RequestTable.cargoVolume,
        RequestTable.cargoDescription,
        RequestTable.loadingAddress,
        RequestTable.loadingLat,
        RequestTable.loadingLon,
        RequestTable.unloadingAddress,
        RequestTable.unloadingLat,
        RequestTable.unloadingLon,
        UserTable.id.alias("driver_id"),
        UserTable.fullName.alias("driver_full_name"),
        CustomerTable.id,
        CustomerTable.organizationName,
        CustomerTable.phoneNumber.alias("organization_phone_number"),
        CustomerTable.email.alias("organization_email"),
        VehicleTable.id,
        VehicleTable.model,
        VehicleTable.licensePlate,
        VehicleTable.regionCode,
        VehicleTable.payloadCapacity
    )

    private fun mapRequestRow(
        row: ResultRow,
        originCity: Alias<CityTable>,
        destCity: Alias<CityTable>
    ): Request {
        val vehicle = row.getOrNull(VehicleTable.id)?.let {
            Vehicle(
                id = it.value,
                model = row[VehicleTable.model],
                licensePlate = row[VehicleTable.licensePlate],
                regionCode = row[VehicleTable.regionCode],
                payloadCapacity = row[VehicleTable.payloadCapacity]
            )
        }
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return Request(
            id = row[RequestTable.id].value,
            requestNumber = row[RequestTable.requestNumber],
            plannedLoadingAt = row[RequestTable.plannedLoadingAt]?.format(formatter),
            plannedUnloadingAt = row[RequestTable.plannedUnloadingAt]?.format(formatter),
            actualLoadingAt = row[RequestTable.actualLoadingAt]?.format(formatter),
            actualUnloadingAt = row[RequestTable.actualUnloadingAt]?.format(formatter),
            arrivedLoadingAt = row[RequestTable.arrivedLoadingAt]?.format(formatter),
            arrivedUnloadingAt = row[RequestTable.arrivedUnloadingAt]?.format(formatter),
            status = Status(
                id = row[RequestStatusTable.id].value,
                name = row[RequestStatusTable.name]
            ),
            transportationDescription = row[RequestTable.transportationDescription],
            origin = row[originCity[CityTable.name].alias("origin_name")],
            destination = row[destCity[CityTable.name].alias("destination_name")],
            createdAt = row[RequestTable.createdAt]?.toString(),
            updatedAt = row[RequestTable.updatedAt]?.toString(),
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
            driverId = row.getOrNull(UserTable.id.alias("driver_id"))?.value,
            driverFullName = row[UserTable.fullName.alias("driver_full_name")],
            customer = Customer(
                id = row[CustomerTable.id].value,
                organizationName = row[CustomerTable.organizationName],
                email = row[CustomerTable.email],
                phoneNumber = row[CustomerTable.phoneNumber]
            ),
            vehicle = vehicle
        )
    }

    private fun buildSearchConditions(
        q: String,
        originCity: Alias<CityTable>,
        destCity: Alias<CityTable>
    ): Op<Boolean> {
        val pattern = "%${q.trim().lowercase()}%"
        return OrOp(
            listOf(
                RequestTable.requestNumber.lowerCase() like pattern,
                RequestTable.transportationDescription.lowerCase() like pattern,
                RequestStatusTable.name.lowerCase() like pattern,
                originCity[CityTable.name].lowerCase() like pattern,
                destCity[CityTable.name].lowerCase() like pattern,
                CargoTypeTable.name.lowerCase() like pattern,
                RequestTable.cargoDescription.lowerCase() like pattern,
                RequestTable.loadingAddress.lowerCase() like pattern,
                RequestTable.unloadingAddress.lowerCase() like pattern,
                UserTable.fullName.lowerCase() like pattern,
                CustomerTable.organizationName.lowerCase() like pattern,
                CustomerTable.phoneNumber.lowerCase() like pattern,
                CustomerTable.email.lowerCase() like pattern,
                VehicleTable.model.lowerCase() like pattern,
                VehicleTable.licensePlate.lowerCase() like pattern
            )
        )
    }

    suspend fun requests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        originCityIds: List<Int>,
        destinationCityIds: List<Int>,
        cargoTypeIds: List<Int>,
        statusIds: List<Int>,
        driverIds: List<Int>,
        vehicleIds: List<Int>
    ): TablePaginatedResult<Request> = loggedTransaction {

        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")

        val query = joinBaseQuery(originCity, destCity)

        val conditions = mutableListOf<Op<Boolean>>()

        if (originCityIds.isNotEmpty()) conditions += RequestTable.originId inList originCityIds
        if (destinationCityIds.isNotEmpty()) conditions += RequestTable.destinationId inList destinationCityIds
        if (cargoTypeIds.isNotEmpty()) conditions += RequestTable.cargoTypeId inList cargoTypeIds
        if (statusIds.isNotEmpty()) conditions += RequestTable.statusId inList statusIds
        if (driverIds.isNotEmpty()) conditions += AssignmentTable.driverId inList driverIds
        if (vehicleIds.isNotEmpty()) conditions += AssignmentTable.vehicleId inList vehicleIds
        if (!searchQuery.isNullOrBlank()) conditions += buildSearchConditions(searchQuery, originCity, destCity)

        val where = AndOp(conditions.ifEmpty { listOf(Op.TRUE) })

        val total = query.select(RequestTable.id.countDistinct()).where(where)
            .single()[RequestTable.id.countDistinct()]

        val offset = (page - 1L) * pageSize

        val requestsWithoutDocs = query
            .select(selectColumns(originCity, destCity))
            .where(where)
            .orderBy(Coalesce(RequestTable.updatedAt, RequestTable.createdAt), SortOrder.DESC_NULLS_LAST)
            .limit(pageSize)
            .offset(offset)
            .map { row -> mapRequestRow(row, originCity, destCity) }

        val requestIds = requestsWithoutDocs.map { it.id }
        val documentsMap = if (requestIds.isNotEmpty()) {
            DeliveryDocumentTable
                .innerJoin(AssignmentTable)
                .select(
                    AssignmentTable.requestId,
                    DeliveryDocumentTable.id,
                    DeliveryDocumentTable.imageUrl,
                    DeliveryDocumentTable.uploadedAt,
                    DeliveryDocumentTable.typeId
                )
                .where { AssignmentTable.requestId inList requestIds }
                .orderBy(DeliveryDocumentTable.uploadedAt, SortOrder.DESC)
                .map { row ->
                    val requestId = row[AssignmentTable.requestId].value

                    val doc = DeliveryDocument(
                        id = row[DeliveryDocumentTable.id].value,
                        imageUrl = row[DeliveryDocumentTable.imageUrl],
                        uploadedAt = row[DeliveryDocumentTable.uploadedAt]?.toString(),
                        typeId = row[DeliveryDocumentTable.typeId].value
                    )

                    requestId to doc
                }
                .groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        val items = requestsWithoutDocs.map { req ->
            req.copy(documents = documentsMap[req.id] ?: emptyList())
        }

        TablePaginatedResult(items = items, totalCount = total)
    }

    suspend fun filters(): RequestFilters = loggedTransaction {
        val cities = CityEntity.all().map { it.toCity() }
        val cargoTypes = CargoTypeEntity.all().map { it.toCargoType() }
        val statuses = RequestStatusEntity.all().map { it.toRequestStatus() }

        val drivers = UserTable
            .select(UserTable.id, UserTable.fullName)
            .map { UserFilter(it[UserTable.id].value, it[UserTable.fullName]) }

        val vehicles = VehicleTable
            .select(
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity
            )
            .map {
                Vehicle(
                    it[VehicleTable.id].value,
                    it[VehicleTable.model],
                    it[VehicleTable.licensePlate],
                    it[VehicleTable.regionCode],
                    it[VehicleTable.payloadCapacity]
                )
            }

        RequestFilters(cities, cargoTypes, statuses, drivers, vehicles)
    }

    private fun getOrganizationId(customerName: String, email: String, phone: String): EntityID<Int> {
        val existing = CustomerTable
            .select(CustomerTable.id)
            .where { CustomerTable.organizationName eq customerName }
            .firstOrNull()?.get(CustomerTable.id)

        return if (existing == null) {
            CustomerTable.insert {
                it[organizationName] = customerName
                it[CustomerTable.email] = email
                it[phoneNumber] = phone
            } get CustomerTable.id
        } else {
            CustomerTable.update({ CustomerTable.id eq existing }) {
                it[CustomerTable.email] = email
                it[phoneNumber] = phone
            }
            existing
        }
    }

    suspend fun createRequest(createdByLogin: String, req: CreateRequest): Int = loggedTransaction {
        val userId = UserTable
            .select(UserTable.id)
            .where { UserTable.login eq createdByLogin }
            .first()[UserTable.id].value

        val insertedId = RequestTable.insert { row ->
            row[statusId] = 1
            setRequestFields(row, req, userId)
        } get RequestTable.id

        insertedId.value
    }

    suspend fun customers(query: String): List<Customer> = loggedTransaction {
        CustomerEntity
            .find { CustomerTable.organizationName.lowerCase() like "%${query.lowercase()}%" }
            .limit(4)
            .map { it.toCustomer() }
    }

    suspend fun editRequest(createdByLogin: String, requestId: Int, req: CreateRequest) = loggedTransaction {
        val requestData = RequestTable
            .select(RequestTable.statusId)
            .where { RequestTable.id eq requestId }
            .singleOrNull() ?: throw StateConflictException("Заявка не найдена")

        val currentStatus = requestData[RequestTable.statusId].value

        if (currentStatus !in 1..2) {
            throw StateConflictException("Редактирование запрещено в текущем статусе")
        }

        val userId = UserTable
            .select(UserTable.id)
            .where { UserTable.login eq createdByLogin }
            .firstOrNull()?.get(UserTable.id)?.value
            ?: throw StateConflictException("Пользователь не найден")

        RequestTable.update({ RequestTable.id eq requestId }) { row ->
            setRequestFields(row, req, userId)
            row[updatedAt] = CurrentTimestampWithTimeZone
        }
    }

    suspend fun cancelAssignment(requestId: Int) = loggedTransaction {
        val currentStatus = RequestTable.select(RequestTable.statusId)
            .where { RequestTable.id eq requestId }
            .singleOrNull()
            ?.get(RequestTable.statusId)?.value
            ?: throw StateConflictException("Заявка не найдена")

        when (currentStatus) {
            5 -> return@loggedTransaction
            !in 1..3 -> throw StateConflictException("Статус заявки не позволяет отмену")
        }

        RequestTable.update({ RequestTable.id eq requestId }) {
            it[statusId] = 5
        }

        AssignmentTable.update({ AssignmentTable.requestId eq requestId }) {
            it[completedAt] = CurrentTimestampWithTimeZone
        }
    }

    suspend fun removeRequest(requestId: Int) = loggedTransaction {
        val currentStatus = RequestTable.select(RequestTable.statusId)
            .where { RequestTable.id eq requestId }
            .singleOrNull()
            ?.get(RequestTable.statusId)?.value
            ?: throw StateConflictException("Заявка не найдена")

        if (currentStatus != 1) {
            throw StateConflictException("Удаление возможно только в статусе 'Ожидает'")
        }

        AssignmentTable.deleteWhere { AssignmentTable.requestId eq requestId }
        RequestTable.deleteWhere { RequestTable.id eq requestId }
    }

    suspend fun assignRequestToDriver(requestId: Int, driverId: Int) = loggedTransaction {
        val exists = AssignmentTable
            .select(AssignmentTable.id)
            .where { AssignmentTable.requestId eq requestId }
            .firstOrNull()

        if (exists != null) throw StateConflictException("Заявка уже назначена водителю")

        val currentVehicleId = DriverTable
            .select(DriverTable.vehicleId)
            .where { DriverTable.userId eq driverId }
            .singleOrNull()
            ?.get(DriverTable.vehicleId)
            ?: throw StateConflictException("У водителя не выбран автомобиль, назначение невозможно")

        driverScheduleGuard.ensureDriverWorkingNow(
            driverId = driverId
        ) { evaluation ->
            "Нельзя назначить заявку: водитель сейчас вне рабочего графика. ${evaluation.hint}"
        }

        AssignmentTable.insert {
            it[this.requestId] = EntityID(requestId, RequestTable)
            it[this.driverId] = EntityID(driverId, UserTable)
            it[this.vehicleId] = currentVehicleId
        }

        RequestTable.update({ RequestTable.id eq requestId }) { it[statusId] = 2 }
    }

    suspend fun reassignRequestToDriver(requestId: Int, driverId: Int) = loggedTransaction {
        val row = RequestTable
            .select(RequestTable.statusId, RequestTable.requestNumber)
            .where { RequestTable.id eq requestId }
            .first()
        if (row[RequestTable.statusId].value != 2) {
            throw StateConflictException(
                "Невозможно назначить заявку ${row[RequestTable.requestNumber]}. " +
                        "Она уже находится в пути, завершена или отменена."
            )
        }
        val currentVehicleId = DriverTable
            .select(DriverTable.vehicleId)
            .where { DriverTable.userId eq driverId }
            .singleOrNull()
            ?.get(DriverTable.vehicleId)
            ?: throw StateConflictException("У водителя не выбран автомобиль")

        driverScheduleGuard.ensureDriverWorkingNow(
            driverId = driverId
        ) { evaluation ->
            "Нельзя назначить заявку: водитель сейчас вне рабочего графика. ${evaluation.hint}"
        }

        AssignmentTable.upsert(AssignmentTable.requestId) {
            it[this.requestId] = EntityID(requestId, RequestTable)
            it[this.driverId] = EntityID(driverId, UserTable)
            it[this.vehicleId] = currentVehicleId
        }
        RequestTable.update({ RequestTable.id eq requestId }) { it[statusId] = 2 }
    }

    suspend fun availableRequests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
    ): ListPaginatedResult<DriverRequest> = loggedTransaction {

        val query = RequestTable
            .join(CustomerTable, JoinType.INNER, RequestTable.customerId, CustomerTable.id)
            .join(CargoTypeTable, JoinType.INNER, RequestTable.cargoTypeId, CargoTypeTable.id)

        val conditions = mutableListOf<Op<Boolean>>()

        conditions += RequestTable.statusId eq 1

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            conditions += OrOp(
                listOf(
                    RequestTable.requestNumber.lowerCase() like pattern,
                    RequestTable.loadingAddress.lowerCase() like pattern,
                    RequestTable.unloadingAddress.lowerCase() like pattern,
                    CargoTypeTable.name.lowerCase() like pattern,
                    CustomerTable.organizationName.lowerCase() like pattern
                )
            )
        }

        val offset = (page - 1L) * pageSize
        val limit = pageSize + 1

        val resultRows = query
            .select(
                RequestTable.id,
                RequestTable.requestNumber,
                RequestTable.createdAt,
                RequestTable.updatedAt,
                CustomerTable.id,
                CustomerTable.organizationName,
                CustomerTable.email,
                CustomerTable.phoneNumber,
                CargoTypeTable.id,
                CargoTypeTable.name,
                RequestTable.cargoWeight,
                RequestTable.cargoVolume,
                RequestTable.cargoDescription,
                RequestTable.loadingAddress,
                RequestTable.loadingLat,
                RequestTable.loadingLon,
                RequestTable.unloadingAddress,
                RequestTable.unloadingLat,
                RequestTable.unloadingLon
            )
            .where(AndOp(conditions))
            .orderBy(RequestTable.createdAt, SortOrder.DESC)
            .limit(limit)
            .offset(offset)
            .map { row ->
                DriverRequest(
                    id = row[RequestTable.id].value,
                    requestNumber = row[RequestTable.requestNumber]!!,
                    customer = Customer(
                        id = row[CustomerTable.id].value,
                        organizationName = row[CustomerTable.organizationName],
                        email = row[CustomerTable.email],
                        phoneNumber = row[CustomerTable.phoneNumber]
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
                    cargo = Cargo(
                        type = CargoType(
                            id = row[CargoTypeTable.id].value,
                            name = row[CargoTypeTable.name]
                        ),
                        weight = row[RequestTable.cargoWeight],
                        volume = row[RequestTable.cargoVolume],
                        description = row[RequestTable.cargoDescription]
                    ),
                    createdAt = row[RequestTable.createdAt]!!.toString(),
                    updatedAt = row[RequestTable.updatedAt]?.toString()
                )
            }

        val hasMore = resultRows.size > pageSize
        val items = if (hasMore) {
            resultRows.dropLast(1)
        } else {
            resultRows
        }

        ListPaginatedResult(
            items = items,
            hasMore = hasMore
        )
    }

    suspend fun unassignDriver(requestId: Int) = loggedTransaction {
        val currentStatus = RequestTable.select(RequestTable.statusId)
            .where { RequestTable.id eq requestId }
            .singleOrNull()
            ?.get(RequestTable.statusId)?.value
            ?: throw StateConflictException("Заявка не найдена")

        if (currentStatus !in 2..3) {
            throw StateConflictException("Нет активного назначения для отмены")
        }

        RequestTable.update({ RequestTable.id eq requestId }) {
            it[statusId] = 1
        }

        AssignmentTable.deleteWhere { AssignmentTable.requestId eq requestId }
    }

    suspend fun getNotificationData(requestId: Int): NotificationContacts? = loggedTransaction {
        RequestTable
            .join(CustomerTable, JoinType.INNER, RequestTable.customerId, CustomerTable.id)
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .join(
                UserTable,
                JoinType.LEFT,
                onColumn = AssignmentTable.driverId,
                otherColumn = UserTable.id
            )
            .select(
                RequestTable.requestNumber,
                CustomerTable.email,
                UserTable.email,
                UserTable.login,
                UserTable.fullName,
                UserTable.phoneNumber
            )
            .where { RequestTable.id eq requestId }
            .map {
                NotificationContacts(
                    reqNumber = it[RequestTable.requestNumber] ?: "Б/Н",
                    customerEmail = it[CustomerTable.email],
                    driverEmail = it.getOrNull(UserTable.email),
                    driverLogin = it.getOrNull(UserTable.login),
                    driverFullName = it.getOrNull(UserTable.fullName),
                    driverPhoneNumber = it.getOrNull(UserTable.phoneNumber)
                )
            }.singleOrNull()
    }

    suspend fun getRequestEmailView(requestId: Int): RequestEmailView? = loggedTransaction {
        RequestTable
            .join(CustomerTable, JoinType.INNER, RequestTable.customerId, CustomerTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id)
            .select(
                RequestTable.id,
                RequestTable.requestNumber,
                CustomerTable.email,
                CargoTypeTable.name,
                RequestTable.transportationDescription,
                RequestTable.cargoDescription,
                RequestTable.loadingAddress,
                RequestTable.unloadingAddress
            )
            .where { RequestTable.id eq requestId }
            .map {
                RequestEmailView(
                    requestId = it[RequestTable.id].value,
                    requestNumber = it[RequestTable.requestNumber] ?: "Б/Н",
                    customerEmail = it[CustomerTable.email],
                    cargoTypeName = it.getOrNull(CargoTypeTable.name),
                    transportationDescription = it[RequestTable.transportationDescription],
                    cargoDescription = it[RequestTable.cargoDescription],
                    loadingAddress = it[RequestTable.loadingAddress],
                    unloadingAddress = it[RequestTable.unloadingAddress]
                )
            }
            .singleOrNull()
    }

    private fun <T : Any> setRequestFields(
        row: UpdateBuilder<T>,
        req: CreateRequest,
        userId: Int
    ) {
        row[RequestTable.plannedLoadingAt] = java.time.OffsetDateTime.parse(req.plannedLoadingAt)
        row[RequestTable.plannedUnloadingAt] = java.time.OffsetDateTime.parse(req.plannedUnloadingAt)
        row[RequestTable.createdById] = userId
        row[RequestTable.loadingAddress] = req.loadingAddress
        row[RequestTable.loadingLat] = req.loadingLat
        row[RequestTable.loadingLon] = req.loadingLon
        row[RequestTable.unloadingAddress] = req.unloadingAddress
        row[RequestTable.unloadingLat] = req.unloadingLat
        row[RequestTable.unloadingLon] = req.unloadingLon
        row[RequestTable.cargoTypeId] = req.cargoTypeId
        row[RequestTable.cargoWeight] = req.cargoWeight
        row[RequestTable.cargoVolume] = req.cargoVolume
        row[RequestTable.cargoDescription] = req.cargoDescription
        row[RequestTable.customerId] = getOrganizationId(req.customerName, req.customerEmail, req.customerPhone)
        row[RequestTable.originId] = req.originId
        row[RequestTable.destinationId] = req.destinationId
        row[RequestTable.transportationDescription] = req.transportationDescription
    }
}
