package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.exception.DeliveryStateException
import com.github.radlance.autodispatch.util.loggedTransaction
import io.ktor.server.plugins.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update

class VehicleRepository {

    suspend fun unassignedVehicles(): List<Vehicle> = loggedTransaction {
        VehicleTable
            .join(DriverTable, JoinType.LEFT, VehicleTable.id, DriverTable.vehicleId)
            .select(
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity
            )
            .where(DriverTable.vehicleId.isNull())
            .orderBy(VehicleTable.model, SortOrder.ASC)
            .map { row ->
                Vehicle(
                    id = row[VehicleTable.id].value,
                    model = row[VehicleTable.model],
                    licensePlate = row[VehicleTable.licensePlate],
                    payloadCapacity = row[VehicleTable.payloadCapacity]
                )
            }
    }

    suspend fun setDriverVehicle(driverId: Int, vehicleId: Int) = loggedTransaction {
        val isVehicleTaken = DriverTable
            .select(DriverTable.userId)
            .where {
                (DriverTable.vehicleId eq vehicleId) and (DriverTable.userId neq driverId)
            }
            .any()

        if (isVehicleTaken) {
            throw DeliveryStateException("Выбранный автомобиль уже занят другим водителем")
        }

        val updatedRows = DriverTable.update({ DriverTable.userId eq driverId }) {
            it[this.vehicleId] = EntityID(vehicleId, VehicleTable)
        }

        if (updatedRows == 0) {
            throw NotFoundException("Водитель с ID $driverId не найден")
        }
    }
}