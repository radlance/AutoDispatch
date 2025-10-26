package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.domain.request.PaginatedResult
import com.github.radlance.autodispatch.database.entity.CargoTypeEntity
import com.github.radlance.autodispatch.database.entity.CityEntity
import com.github.radlance.autodispatch.database.entity.RequestStatusEntity
import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.CargoTypeTable
import com.github.radlance.autodispatch.database.table.CityTable
import com.github.radlance.autodispatch.database.table.CustomerTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.request.Filters
import com.github.radlance.autodispatch.domain.request.Request
import com.github.radlance.autodispatch.domain.request.UserFilter
import com.github.radlance.autodispatch.domain.request.VehicleFilter
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.Concat
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.stringLiteral

class RequestRepository {
    suspend fun requests(
        page: Int = 1,
        pageSize: Int = 20,
        searchQuery: String? = null,
        originCityIds: List<Int> = emptyList(),
        destinationCityIds: List<Int> = emptyList(),
        cargoTypeIds: List<Int> = emptyList(),
        statusIds: List<Int> = emptyList(),
        driverIds: List<Int> = emptyList(),
        vehicleIds: List<Int> = emptyList()
    ): PaginatedResult<Request> = loggedTransaction {

        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")

        val vehicleInfo = Case()
            .When(VehicleTable.id.isNull(), stringLiteral(""))
            .Else(
                Concat(
                    " ",
                    VehicleTable.model,
                    stringLiteral("("),
                    VehicleTable.licensePlate,
                    stringLiteral(")")
                )
            )

        val query = RequestTable
            .join(originCity, JoinType.LEFT, RequestTable.originId, originCity[CityTable.id])
            .join(destCity, JoinType.LEFT, RequestTable.destinationId, destCity[CityTable.id])
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id)
            .join(CustomerTable, JoinType.LEFT, RequestTable.customerId, CustomerTable.id)
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .join(UserTable, JoinType.LEFT, AssignmentTable.driverId, UserTable.id)
            .join(VehicleTable, JoinType.LEFT, AssignmentTable.vehicleId, VehicleTable.id)

        val conditions = mutableListOf<Op<Boolean>>()

        if (originCityIds.isNotEmpty()) {
            conditions.add(RequestTable.originId inList originCityIds)
        }
        if (destinationCityIds.isNotEmpty()) {
            conditions.add(RequestTable.destinationId inList destinationCityIds)
        }
        if (cargoTypeIds.isNotEmpty()) {
            conditions.add(RequestTable.cargoTypeId inList cargoTypeIds)
        }
        if (statusIds.isNotEmpty()) {
            conditions.add(RequestTable.statusId inList statusIds)
        }
        if (driverIds.isNotEmpty()) {
            conditions.add(AssignmentTable.driverId inList driverIds)
        }
        if (vehicleIds.isNotEmpty()) {
            conditions.add(AssignmentTable.vehicleId inList vehicleIds)
        }

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"

            val searchConditions = OrOp(
                listOfNotNull(
                    RequestTable.requestNumber.lowerCase() like pattern,
                    RequestStatusTable.name.lowerCase() like pattern,
                    originCity[CityTable.name].lowerCase() like pattern,
                    destCity[CityTable.name].lowerCase() like pattern,
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
            conditions.add(searchConditions)
        }

        val whereClause = AndOp(conditions.ifEmpty { listOf(Op.TRUE) })

        val totalCount = query
            .select(RequestTable.id.countDistinct())
            .where(whereClause)
            .single()[RequestTable.id.countDistinct()]

        val columns = listOf(
            RequestTable.id,
            RequestTable.requestNumber,
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
            RequestTable.startedTripAt,
            RequestTable.endedTripAt,
            UserTable.fullName.alias("driver_full_name"),
            CustomerTable.organizationName,
            CustomerTable.phoneNumber.alias("organization_phone_number"),
            CustomerTable.email.alias("organization_email"),
            vehicleInfo.alias("vehicle_info")
        )

        val offset = (page - 1L) * pageSize

        val results = query.select(columns)
            .where(whereClause)
            .orderBy(RequestTable.createdAt, SortOrder.DESC_NULLS_LAST)
            .limit(pageSize)
            .offset(offset)
            .map { row ->
                Request(
                    id = row[RequestTable.id].value,
                    requestNumber = row[RequestTable.requestNumber],
                    statusName = row[RequestStatusTable.name],
                    origin = row[originCity[CityTable.name].alias("origin_name")],
                    destination = row[destCity[CityTable.name].alias("destination_name")],
                    createdAt = row[RequestTable.createdAt]?.toString(),
                    cargoTypeName = row[CargoTypeTable.name.alias("cargo_type_name")],
                    cargoWeight = row[RequestTable.cargoWeight],
                    cargoVolume = row[RequestTable.cargoVolume],
                    cargoDescription = row[RequestTable.cargoDescription],
                    loadingPoint = row[RequestTable.loadingPoint],
                    unloadingPoint = row[RequestTable.unloadingPoint],
                    startedTripAt = row[RequestTable.startedTripAt]?.toString(),
                    endedTripAt = row[RequestTable.endedTripAt]?.toString(),
                    driverFullName = row[UserTable.fullName.alias("driver_full_name")],
                    organizationName = row[CustomerTable.organizationName],
                    organizationPhoneNumber = row[CustomerTable.phoneNumber.alias("organization_phone_number")],
                    organizationEmail = row[CustomerTable.email.alias("organization_email")],
                    vehicleInfo = row[vehicleInfo.alias("vehicle_info")]
                )
            }

        return@loggedTransaction PaginatedResult(items = results, totalCount = totalCount)
    }

    suspend fun filters(): Filters = loggedTransaction {
        val cities = CityEntity.all().map { it.toCity() }.toList()
        val cargoTypes = CargoTypeEntity.all().map { it.toCargoType() }.toList()
        val statuses = RequestStatusEntity.all().map { it.toRequestStatus() }.toList()
        val users = UserTable
            .select(UserTable.id, UserTable.fullName)
            .map { row ->
                UserFilter(
                    id = row[UserTable.id].value,
                    fullName = row[UserTable.fullName]
                )
            }

        val vehicles = VehicleTable
            .select(VehicleTable.id, VehicleTable.model, VehicleTable.licensePlate)
            .map { row ->
                VehicleFilter(
                    id = row[VehicleTable.id].value,
                    model = row[VehicleTable.model],
                    licencePlate = row[VehicleTable.licensePlate]
                )
            }

        return@loggedTransaction Filters(
            cities = cities,
            cargoTypes = cargoTypes,
            statuses = statuses,
            drivers = users,
            vehicles = vehicles
        )
    }
}