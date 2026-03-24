package com.github.radlance.autodispatch.repository

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
import com.github.radlance.autodispatch.domain.statistics.ReportDateRange
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.count
import java.time.OffsetDateTime

data class RequestReportRow(
    val requestNumber: String?,
    val status: String,
    val createdAt: OffsetDateTime?,
    val plannedLoadingAt: OffsetDateTime?,
    val plannedUnloadingAt: OffsetDateTime?,
    val originCity: String?,
    val destinationCity: String?,
    val cargoType: String?,
    val cargoWeight: Double?,
    val cargoVolume: Double?,
    val customerName: String?,
    val customerPhone: String?,
    val driverName: String?,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehicleRegionCode: String?
)

data class DriverReportRow(
    val fullName: String,
    val phoneNumber: String?,
    val status: String,
    val vehicleModel: String?,
    val vehicleLicensePlate: String?,
    val vehicleRegionCode: String?,
    val vehiclePayloadCapacity: Int?,
    val assignedRequests: Long
)

data class VehicleReportRow(
    val model: String,
    val licensePlate: String,
    val regionCode: String,
    val payloadCapacity: Int,
    val currentDriverName: String?,
    val assignedRequests: Long
)

class ReportRepository {

    suspend fun requests(range: ReportDateRange): List<RequestReportRow> = loggedTransaction {
        val originCity = CityTable.alias("origin_city")
        val destCity = CityTable.alias("dest_city")

        val periodCondition = requestPeriodCondition(range)

        RequestTable
            .join(originCity, JoinType.LEFT, RequestTable.originId, originCity[CityTable.id])
            .join(destCity, JoinType.LEFT, RequestTable.destinationId, destCity[CityTable.id])
            .join(RequestStatusTable, JoinType.INNER, RequestTable.statusId, RequestStatusTable.id)
            .join(CargoTypeTable, JoinType.LEFT, RequestTable.cargoTypeId, CargoTypeTable.id)
            .join(CustomerTable, JoinType.LEFT, RequestTable.customerId, CustomerTable.id)
            .join(AssignmentTable, JoinType.LEFT, RequestTable.id, AssignmentTable.requestId)
            .join(UserTable, JoinType.LEFT, AssignmentTable.driverId, UserTable.id)
            .join(VehicleTable, JoinType.LEFT, AssignmentTable.vehicleId, VehicleTable.id)
            .select(
                RequestTable.requestNumber,
                RequestStatusTable.name,
                RequestTable.createdAt,
                RequestTable.plannedLoadingAt,
                RequestTable.plannedUnloadingAt,
                originCity[CityTable.name].alias("origin_name"),
                destCity[CityTable.name].alias("destination_name"),
                CargoTypeTable.name,
                RequestTable.cargoWeight,
                RequestTable.cargoVolume,
                CustomerTable.organizationName,
                CustomerTable.phoneNumber,
                UserTable.fullName,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode
            )
            .where(periodCondition)
            .orderBy(Coalesce(RequestTable.updatedAt, RequestTable.createdAt), SortOrder.DESC_NULLS_LAST)
            .map { row ->
                RequestReportRow(
                    requestNumber = row[RequestTable.requestNumber],
                    status = row[RequestStatusTable.name],
                    createdAt = row[RequestTable.createdAt],
                    plannedLoadingAt = row[RequestTable.plannedLoadingAt],
                    plannedUnloadingAt = row[RequestTable.plannedUnloadingAt],
                    originCity = row[originCity[CityTable.name].alias("origin_name")],
                    destinationCity = row[destCity[CityTable.name].alias("destination_name")],
                    cargoType = row.getOrNull(CargoTypeTable.name),
                    cargoWeight = row[RequestTable.cargoWeight],
                    cargoVolume = row[RequestTable.cargoVolume],
                    customerName = row.getOrNull(CustomerTable.organizationName),
                    customerPhone = row.getOrNull(CustomerTable.phoneNumber),
                    driverName = row.getOrNull(UserTable.fullName),
                    vehicleModel = row.getOrNull(VehicleTable.model),
                    vehicleLicensePlate = row.getOrNull(VehicleTable.licensePlate),
                    vehicleRegionCode = row.getOrNull(VehicleTable.regionCode)
                )
            }
    }

    suspend fun drivers(range: ReportDateRange): List<DriverReportRow> = loggedTransaction {
        val assignedCount = AssignmentTable.id.count()

        DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .select(
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity,
                assignedCount
            )
            .where(assignmentPeriodCondition(range))
            .groupBy(
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity
            )
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .map { row ->
                DriverReportRow(
                    fullName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    status = row[DriverStatusTable.name],
                    vehicleModel = row.getOrNull(VehicleTable.model),
                    vehicleLicensePlate = row.getOrNull(VehicleTable.licensePlate),
                    vehicleRegionCode = row.getOrNull(VehicleTable.regionCode),
                    vehiclePayloadCapacity = row.getOrNull(VehicleTable.payloadCapacity),
                    assignedRequests = row[assignedCount]
                )
            }
    }

    suspend fun vehicles(range: ReportDateRange): List<VehicleReportRow> = loggedTransaction {
        val assignedCount = AssignmentTable.id.count()

        VehicleTable
            .join(AssignmentTable, JoinType.LEFT, VehicleTable.id, AssignmentTable.vehicleId)
            .join(DriverTable, JoinType.LEFT, VehicleTable.id, DriverTable.vehicleId)
            .join(UserTable, JoinType.LEFT, DriverTable.userId, UserTable.id)
            .select(
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity,
                UserTable.fullName,
                assignedCount
            )
            .where(assignmentPeriodCondition(range))
            .groupBy(
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity,
                UserTable.fullName
            )
            .orderBy(VehicleTable.model, SortOrder.ASC)
            .map { row ->
                VehicleReportRow(
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate],
                    regionCode = row[VehicleTable.regionCode],
                    payloadCapacity = row[VehicleTable.payloadCapacity],
                    currentDriverName = row.getOrNull(UserTable.fullName),
                    assignedRequests = row[assignedCount]
                )
            }
    }

    private fun assignmentPeriodCondition(range: ReportDateRange): Op<Boolean> {
        val period = AssignmentTable.assignedAt
        return period.between(range.start, range.end)
    }

    private fun requestPeriodCondition(range: ReportDateRange): Op<Boolean> {
        val createdAt = RequestTable.createdAt
        val updatedAt = RequestTable.updatedAt
        val createdInRange = createdAt.between(range.start, range.end)
        val updatedInRange = AndOp(listOf(createdAt.isNull(), updatedAt.between(range.start, range.end)))
        return OrOp(listOf(createdInRange, updatedInRange))
    }
}
