package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.entity.UserEntity
import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.auth.User
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.domain.profile.DeliveriesStats
import com.github.radlance.autodispatch.domain.profile.ProfileDetails
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.count

class ProfileRepository {
    suspend fun profile(login: String): User = loggedTransaction {
        return@loggedTransaction UserEntity.find { UserTable.login eq login }.limit(1).first().toUser()
    }

    suspend fun profileDetails(login: String): ProfileDetails = loggedTransaction {
        val countExpression = AssignmentTable.id.count()

        val rows = UserTable
            .join(DriverTable, JoinType.INNER, UserTable.id, DriverTable.userId)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(AssignmentTable, JoinType.LEFT, UserTable.id, AssignmentTable.driverId)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
                RequestStatusTable.name,
                countExpression
            )
            .where { UserTable.login eq login }
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.payloadCapacity,
                RequestStatusTable.name
            )
            .toList()

        if (rows.isEmpty()) throw IllegalArgumentException("User not found or is not a driver")

        val first = rows.first()

        val statsMap = rows.associate { row ->
            row[RequestStatusTable.name] to row[countExpression].toInt()
        }

        val deliveriesStats = DeliveriesStats(
            totalCount = statsMap.values.sum(),
            activeCount = (statsMap["Назначена"] ?: 0) + (statsMap["В пути"] ?: 0),
            completedCount = statsMap["Завершена"] ?: 0,
            canceledCount = statsMap["Отменена"] ?: 0,
            onCheckCount = statsMap["На проверке"] ?: 0,
            rejectedCount = statsMap["Отклонена"] ?: 0
        )

        val vehicle = first.getOrNull(VehicleTable.id)?.value?.let { id ->
            Vehicle(
                id = id,
                model = first[VehicleTable.model],
                licensePlate = first[VehicleTable.licensePlate],
                payloadCapacity = first[VehicleTable.payloadCapacity]
            )
        }

        ProfileDetails(
            fullName = first[UserTable.fullName],
            phoneNumber = first[UserTable.phoneNumber],
            deliveriesStats = deliveriesStats,
            vehicle = vehicle
        )
    }
}