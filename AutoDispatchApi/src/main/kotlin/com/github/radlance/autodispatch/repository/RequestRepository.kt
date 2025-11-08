package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.entity.CargoTypeEntity
import com.github.radlance.autodispatch.database.entity.CityEntity
import com.github.radlance.autodispatch.database.entity.CustomerEntity
import com.github.radlance.autodispatch.database.entity.RequestStatusEntity
import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.CargoTypeTable
import com.github.radlance.autodispatch.database.table.CityTable
import com.github.radlance.autodispatch.database.table.CustomerTable
import com.github.radlance.autodispatch.database.table.DriverStatusTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.request.CreateRequest
import com.github.radlance.autodispatch.domain.request.Customer
import com.github.radlance.autodispatch.domain.request.DriverStats
import com.github.radlance.autodispatch.domain.request.Filters
import com.github.radlance.autodispatch.domain.request.PaginatedResult
import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.domain.request.RequestStatus
import com.github.radlance.autodispatch.domain.request.UserFilter
import com.github.radlance.autodispatch.domain.request.VehicleFilter
import com.github.radlance.autodispatch.exception.MissingCredentialException
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.Concat
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert

class RequestRepository {

    private fun vehicleInfoExpr(): Expression<String> =
        Case()
            .When(VehicleTable.id.isNull(), stringLiteral(""))
            .Else(
                Concat(
                    "",
                    VehicleTable.model,
                    stringLiteral(" ("),
                    VehicleTable.licensePlate,
                    stringLiteral(")")
                )
            )

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
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)

    private fun selectColumns(
        originCity: Alias<CityTable>,
        destCity: Alias<CityTable>,
        vehicleInfo: Expression<String>
    ): List<Expression<*>> = listOf(
        RequestTable.id,
        RequestTable.requestNumber,
        RequestTable.transportationDescription,
        RequestStatusTable.id,
        RequestStatusTable.name,
        originCity[CityTable.name].alias("origin_name"),
        destCity[CityTable.name].alias("destination_name"),
        RequestTable.createdAt,
        CargoTypeTable.name.alias("cargo_type_name"),
        RequestTable.cargoWeight,
        RequestTable.cargoVolume,
        RequestTable.cargoDescription,
        RequestTable.loadingPoint,
        RequestTable.unloadingPoint,
        AssignmentTable.startedAt.alias("started_trip_at"),
        AssignmentTable.completedAt.alias("completed_trip_at"),
        UserTable.id.alias("driver_id"),
        UserTable.fullName.alias("driver_full_name"),
        CustomerTable.organizationName,
        CustomerTable.phoneNumber.alias("organization_phone_number"),
        CustomerTable.email.alias("organization_email"),
        vehicleInfo.alias("vehicle_info")
    )

    private fun mapRequestRow(
        row: ResultRow,
        originCity: Alias<CityTable>,
        destCity: Alias<CityTable>,
        vehicleInfo: Expression<String>
    ): Request = Request(
        id = row[RequestTable.id].value,
        requestNumber = row[RequestTable.requestNumber],
        status = RequestStatus(
            id = row[RequestStatusTable.id].value,
            name = row[RequestStatusTable.name]
        ),
        transportationDescription = row[RequestTable.transportationDescription],
        origin = row[originCity[CityTable.name].alias("origin_name")],
        destination = row[destCity[CityTable.name].alias("destination_name")],
        createdAt = row[RequestTable.createdAt]?.toString(),
        cargoTypeName = row[CargoTypeTable.name.alias("cargo_type_name")],
        cargoWeight = row[RequestTable.cargoWeight],
        cargoVolume = row[RequestTable.cargoVolume],
        cargoDescription = row[RequestTable.cargoDescription],
        loadingPoint = row[RequestTable.loadingPoint],
        unloadingPoint = row[RequestTable.unloadingPoint],
        startedTripAt = row[AssignmentTable.startedAt.alias("started_trip_at")]?.toString(),
        endedTripAt = row[AssignmentTable.completedAt.alias("completed_trip_at")]?.toString(),
        driverId = row.getOrNull(UserTable.id.alias("driver_id"))?.value,
        driverFullName = row[UserTable.fullName.alias("driver_full_name")],
        organizationName = row[CustomerTable.organizationName],
        organizationPhoneNumber = row[CustomerTable.phoneNumber.alias("organization_phone_number")],
        organizationEmail = row[CustomerTable.email.alias("organization_email")],
        vehicleInfo = row[vehicleInfo.alias("vehicle_info")]
    )

    private fun buildSearchConditions(q: String): Op<Boolean> {
        val pattern = "%${q.trim().lowercase()}%"
        return OrOp(
            listOf(
                RequestTable.requestNumber.lowerCase() like pattern,
                RequestTable.transportationDescription.lowerCase() like pattern,
                RequestStatusTable.name.lowerCase() like pattern,
                CityTable.name.lowerCase() like pattern,
                CargoTypeTable.name.lowerCase() like pattern,
                RequestTable.cargoDescription.lowerCase() like pattern,
                RequestTable.loadingPoint.lowerCase() like pattern,
                RequestTable.unloadingPoint.lowerCase() like pattern,
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
    ): PaginatedResult<Request> = loggedTransaction {

        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")
        val vehicleInfo = vehicleInfoExpr()

        val query = joinBaseQuery(originCity, destCity)

        val conditions = mutableListOf<Op<Boolean>>()

        if (originCityIds.isNotEmpty()) conditions += RequestTable.originId inList originCityIds
        if (destinationCityIds.isNotEmpty()) conditions += RequestTable.destinationId inList destinationCityIds
        if (cargoTypeIds.isNotEmpty()) conditions += RequestTable.cargoTypeId inList cargoTypeIds
        if (statusIds.isNotEmpty()) conditions += RequestTable.statusId inList statusIds
        if (driverIds.isNotEmpty()) conditions += AssignmentTable.driverId inList driverIds
        if (vehicleIds.isNotEmpty()) conditions += DriverTable.vehicleId inList vehicleIds
        if (!searchQuery.isNullOrBlank()) conditions += buildSearchConditions(searchQuery)

        val where = AndOp(conditions.ifEmpty { listOf(Op.TRUE) })

        val total = query.select(RequestTable.id.countDistinct()).where(where)
            .single()[RequestTable.id.countDistinct()]

        val offset = (page - 1L) * pageSize

        val items = query
            .select(selectColumns(originCity, destCity, vehicleInfo))
            .where(where)
            .orderBy(Coalesce(RequestTable.updatedAt, RequestTable.createdAt), SortOrder.DESC_NULLS_LAST)
            .limit(pageSize)
            .offset(offset)
            .map { row -> mapRequestRow(row, originCity, destCity, vehicleInfo) }

        PaginatedResult(items = items, totalCount = total)
    }

    suspend fun filters(): Filters = loggedTransaction {
        val cities = CityEntity.all().map { it.toCity() }
        val cargoTypes = CargoTypeEntity.all().map { it.toCargoType() }
        val statuses = RequestStatusEntity.all().map { it.toRequestStatus() }

        val drivers = UserTable
            .select(UserTable.id, UserTable.fullName)
            .map { UserFilter(it[UserTable.id].value, it[UserTable.fullName]) }

        val vehicles = VehicleTable
            .select(VehicleTable.id, VehicleTable.model, VehicleTable.licensePlate)
            .map {
                VehicleFilter(
                    it[VehicleTable.id].value,
                    it[VehicleTable.model],
                    it[VehicleTable.licensePlate]
                )
            }

        Filters(cities, cargoTypes, statuses, drivers, vehicles)
    }

    private fun getOrganizationId(customerName: String, email: String, phone: String?): EntityID<Int> {
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

    suspend fun createRequest(createdByLogin: String, req: CreateRequest) = loggedTransaction {
        val userId = UserTable.select(UserTable.id).where {
            UserTable.login eq createdByLogin
        }.first()[UserTable.id].value

        RequestTable.insert { row ->
            row[statusId] = 1
            row[this.createdById] = userId
            row[loadingPoint] = req.loadingPoint
            row[unloadingPoint] = req.unloadingPoint
            row[cargoTypeId] = req.cargoTypeId
            row[cargoWeight] = req.cargoWeight
            row[cargoVolume] = req.cargoVolume
            row[cargoDescription] = req.cargoDescription
            row[this.customerId] = getOrganizationId(req.customerName, req.customerEmail, req.customerPhone)
            row[originId] = req.originId
            row[destinationId] = req.destinationId
            row[transportationDescription] = req.transportationDescription
        }
    }

    suspend fun customers(query: String): List<Customer> = loggedTransaction {
        CustomerEntity
            .find { CustomerTable.organizationName.lowerCase() like "%${query.lowercase()}%" }
            .limit(4)
            .map { it.toCustomer() }
    }

    suspend fun editRequest(createdByLogin: String, requestId: Int, req: CreateRequest) = loggedTransaction {
        val userId = UserTable.select(UserTable.id).where {
            UserTable.login eq createdByLogin
        }.first()[UserTable.id].value

        RequestTable.update({ RequestTable.id eq requestId }) { row ->
            row[this.createdById] = userId
            row[loadingPoint] = req.loadingPoint
            row[unloadingPoint] = req.unloadingPoint
            row[cargoTypeId] = req.cargoTypeId
            row[cargoWeight] = req.cargoWeight
            row[cargoVolume] = req.cargoVolume
            row[cargoDescription] = req.cargoDescription
            row[this.customerId] = getOrganizationId(req.customerName, req.customerEmail, req.customerPhone)
            row[originId] = req.originId
            row[destinationId] = req.destinationId
            row[transportationDescription] = req.transportationDescription
        }
    }

    suspend fun cancelRequest(requestId: Int) = loggedTransaction {
        RequestTable.update({ RequestTable.id eq requestId }) { it[statusId] = 5 }
    }

    suspend fun cancelAssignment(requestId: Int) = loggedTransaction {
        RequestTable.update({ RequestTable.id eq requestId }) { it[statusId] = 5 }
        AssignmentTable.update({ AssignmentTable.requestId eq requestId }) { it[completedAt] = CurrentTimestamp }
    }

    suspend fun requestAssignment(): List<DriverStats> = loggedTransaction {
        val requestCount = Case()
            .When(RequestTable.statusId inList listOf(1, 2, 3), longLiteral(1))
            .Else(longLiteral(0))
            .sum()

        val statusOrder = Case()
            .When(DriverStatusTable.name eq "Свободен", intLiteral(1))
            .When(DriverStatusTable.name eq "В рейсе", intLiteral(2))
            .When(DriverStatusTable.name eq "Не на смене", intLiteral(3))
            .Else(intLiteral(4))

        DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                requestCount
            )
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate
            )
            .orderBy(statusOrder, SortOrder.ASC)
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .map { row ->
                DriverStats(
                    driverId = row[UserTable.id].value,
                    driverName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    status = row[DriverStatusTable.name],
                    vehicleModel = row[VehicleTable.model],
                    vehicleLicensePlate = row[VehicleTable.licensePlate],
                    totalAssignedRequests = row[requestCount] ?: 0L
                )
            }
    }

    suspend fun assignRequestToDriver(requestId: Int, driverId: Int) = loggedTransaction {
        val exists = AssignmentTable
            .select(AssignmentTable.id)
            .where { AssignmentTable.requestId eq requestId }
            .firstOrNull()

        if (exists != null) throw MissingCredentialException("Заявка уже назначена водителю")

        AssignmentTable.insert {
            it[this.requestId] = EntityID(requestId, RequestTable)
            it[this.driverId] = EntityID(driverId, UserTable)
        }

        RequestTable.update({ RequestTable.id eq requestId }) { it[statusId] = 2 }
    }

    suspend fun reassignRequestToDriver(requestId: Int, driverId: Int) = loggedTransaction {
        AssignmentTable.upsert(AssignmentTable.requestId) {
            it[this.requestId] = EntityID(requestId, RequestTable)
            it[this.driverId] = EntityID(driverId, UserTable)
        }
        RequestTable.update({ RequestTable.id eq requestId }) { it[statusId] = 2 }
    }

    suspend fun myRequests(driverLogin: String): List<Request> = loggedTransaction {
        val driverId = UserTable.select(UserTable.id).where {
            UserTable.login eq driverLogin
        }.first()[UserTable.id].value

        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")
        val vehicleInfo = vehicleInfoExpr()

        joinBaseQuery(originCity, destCity)
            .select(selectColumns(originCity, destCity, vehicleInfo))
            .where {
                (AssignmentTable.driverId eq driverId) and
                        (RequestTable.statusId inList listOf(2, 3))
            }
            .orderBy(RequestTable.id, SortOrder.DESC)
            .map { mapRequestRow(it, originCity, destCity, vehicleInfo) }
    }
}
