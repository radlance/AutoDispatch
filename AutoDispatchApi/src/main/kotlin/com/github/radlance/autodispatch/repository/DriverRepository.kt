package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.AssignmentTable
import com.github.radlance.autodispatch.database.table.DriverShiftTable
import com.github.radlance.autodispatch.database.table.DriverStatusTable
import com.github.radlance.autodispatch.database.table.DriverTable
import com.github.radlance.autodispatch.database.table.RequestStatusTable
import com.github.radlance.autodispatch.database.table.RequestTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.database.table.VehicleTable
import com.github.radlance.autodispatch.domain.common.ListPaginatedResult
import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.common.TablePaginatedResult
import com.github.radlance.autodispatch.domain.driver.Driver
import com.github.radlance.autodispatch.domain.driver.DriverStats
import com.github.radlance.autodispatch.domain.driver.DriverWithoutCar
import com.github.radlance.autodispatch.domain.driver.DriverWorkShift
import com.github.radlance.autodispatch.domain.profile.DeliveriesStats
import com.github.radlance.autodispatch.domain.request.Vehicle
import com.github.radlance.autodispatch.exception.StateConflictException
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Case
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.sum
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DriverRepository(
    private val driverScheduleGuard: DriverScheduleGuard
) {

    suspend fun drivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): TablePaginatedResult<Driver> = loggedTransaction {

        val baseQuery = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)
            .join(RequestStatusTable, JoinType.LEFT, RequestTable.statusId, RequestStatusTable.id)

        val condition: Op<Boolean> =
            if (!searchQuery.isNullOrBlank()) {
                val pattern = "%${searchQuery.trim().lowercase()}%"
                OrOp(
                    listOf(
                        UserTable.fullName.lowerCase() like pattern,
                        UserTable.phoneNumber.lowerCase() like pattern,
                        VehicleTable.model.lowerCase() like pattern,
                        VehicleTable.licensePlate.lowerCase() like pattern
                    )
                )
            } else {
                Op.TRUE
            }

        val total = baseQuery
            .select(UserTable.id.countDistinct())
            .where(condition)
            .single()[UserTable.id.countDistinct()]

        val offset = (page - 1L) * pageSize
        val countExpression = AssignmentTable.id.count()

        val rows = baseQuery
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.avatarUrl,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity,
                RequestStatusTable.name,
                countExpression
            )
            .where(condition)
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.id,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity,
                RequestStatusTable.name,
                DriverTable.updatedAt,
                DriverTable.createdAt
            )
            .orderBy(
                Coalesce(
                    DriverTable.updatedAt,
                    DriverTable.createdAt
                ), SortOrder.DESC_NULLS_LAST
            )
            .limit(pageSize)
            .offset(offset)
            .toList()

        val items = rows
            .groupBy { it[UserTable.id].value }
            .map { (_, driverRows) ->

                val first = driverRows.first()

                val statsMap = driverRows
                    .filter { it.getOrNull(RequestStatusTable.name) != null }
                    .associate {
                        it[RequestStatusTable.name] to it[countExpression].toInt()
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
                        regionCode = first[VehicleTable.regionCode],
                        payloadCapacity = first[VehicleTable.payloadCapacity]
                    )
                }

                Driver(
                    id = first[UserTable.id].value,
                    fullName = first[UserTable.fullName],
                    avatarUrl = first[UserTable.avatarUrl],
                    phoneNumber = first[UserTable.phoneNumber],
                    status = Status(
                        id = first[DriverStatusTable.id].value,
                        name = first[DriverStatusTable.name]
                    ),
                    vehicle = vehicle,
                    deliveriesStats = deliveriesStats,
                    workSchedule = emptyList()
                )
            }

        val scheduleByDriverId = loadScheduleBundlesByDriverIds(items.map { it.id })
        val itemsWithSchedule = items.map { driver ->
            driver.copy(
                workSchedule = scheduleByDriverId[driver.id]?.shifts.orEmpty()
            )
        }

        TablePaginatedResult(
            items = itemsWithSchedule,
            totalCount = total
        )
    }


    suspend fun driverStats(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResult<DriverStats> = loggedTransaction {

        val requestCount = Case()
            .When(RequestTable.statusId inList listOf(1, 2, 3), longLiteral(1))
            .Else(longLiteral(0))
            .sum()

        val statusOrder = Case()
            .When(DriverStatusTable.id eq 1, intLiteral(1))
            .When(DriverStatusTable.id eq 2, intLiteral(2))
            .When(DriverStatusTable.id eq 3, intLiteral(3))
            .Else(intLiteral(4))

        val joinQuery = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(DriverStatusTable, JoinType.INNER, DriverTable.statusId, DriverStatusTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)
            .join(VehicleTable, JoinType.LEFT, DriverTable.vehicleId, VehicleTable.id)
            .join(RequestTable, JoinType.LEFT, AssignmentTable.requestId, RequestTable.id)

        val conditions = mutableListOf<Op<Boolean>>()

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            conditions += OrOp(
                listOf(
                    UserTable.fullName.lowerCase() like pattern,
                    UserTable.phoneNumber.lowerCase() like pattern,
                    DriverStatusTable.name.lowerCase() like pattern,
                    VehicleTable.model.lowerCase() like pattern,
                    VehicleTable.licensePlate.lowerCase() like pattern
                )
            )
        }

        val offset = (page - 1L) * pageSize

        val rawStats = joinQuery
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity,
                requestCount
            )
            .where(
                if (conditions.isEmpty()) Op.TRUE else AndOp(conditions)
            )
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                DriverStatusTable.id,
                DriverStatusTable.name,
                VehicleTable.model,
                VehicleTable.licensePlate,
                VehicleTable.regionCode,
                VehicleTable.payloadCapacity
            )
            .orderBy(statusOrder, SortOrder.ASC)
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .limit(pageSize + 1)
            .offset(offset)
            .toList()

        val hasMore = rawStats.size > pageSize
        val rows = if (hasMore) rawStats.dropLast(1) else rawStats
        val scheduleByDriverId = loadScheduleBundlesByDriverIds(rows.map { it[UserTable.id].value })

        val stats = rows.map { row ->
            val driverId = row[UserTable.id].value
            val scheduleBundle = scheduleByDriverId[driverId]
            val evaluation = driverScheduleGuard.evaluate(scheduleBundle?.windows.orEmpty())

            DriverStats(
                driverId = driverId,
                driverName = row[UserTable.fullName],
                phoneNumber = row[UserTable.phoneNumber],
                driverStatus = Status(
                    id = row[DriverStatusTable.id].value,
                    name = row[DriverStatusTable.name]
                ),
                vehicleModel = row[VehicleTable.model],
                vehicleLicensePlate = row[VehicleTable.licensePlate],
                vehicleRegionCode = row[VehicleTable.regionCode],
                vehiclePayloadCapacity = row[VehicleTable.payloadCapacity],
                totalAssignedRequests = row[requestCount] ?: 0L,
                workSchedule = scheduleBundle?.shifts.orEmpty(),
                isWorkingNow = evaluation.isWorkingNow,
                scheduleHint = evaluation.hint
            )
        }

        ListPaginatedResult(
            items = stats,
            hasMore = hasMore
        )
    }

    private fun loadScheduleBundlesByDriverIds(driverIds: List<Int>): Map<Int, DriverScheduleBundle> {
        if (driverIds.isEmpty()) return emptyMap()

        return DriverShiftTable
            .select(
                DriverShiftTable.driverId,
                DriverShiftTable.dayOfWeek,
                DriverShiftTable.startTime,
                DriverShiftTable.endTime
            )
            .where { DriverShiftTable.driverId inList driverIds }
            .toList()
            .groupBy { it[DriverShiftTable.driverId].value }
            .mapValues { (_, rows) ->
                val orderedRows = rows.sortedWith(
                    compareBy<ResultRow>(
                        { it[DriverShiftTable.dayOfWeek] },
                        { it[DriverShiftTable.startTime] }
                    )
                )
                val shifts = orderedRows.map { row ->
                    DriverWorkShift(
                        dayOfWeek = row[DriverShiftTable.dayOfWeek].toInt(),
                        startTime = row[DriverShiftTable.startTime].format(TIME_FORMAT),
                        endTime = row[DriverShiftTable.endTime].format(TIME_FORMAT)
                    )
                }
                val windows = orderedRows.map { row ->
                    DriverScheduleGuard.ShiftWindow(
                        dayOfWeek = row[DriverShiftTable.dayOfWeek].toInt(),
                        startTime = row[DriverShiftTable.startTime],
                        endTime = row[DriverShiftTable.endTime]
                    )
                }
                DriverScheduleBundle(
                    shifts = shifts,
                    windows = windows
                )
            }
    }

    private data class DriverScheduleBundle(
        val shifts: List<DriverWorkShift>,
        val windows: List<DriverScheduleGuard.ShiftWindow>
    )

    suspend fun driversWithoutCar(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): ListPaginatedResult<DriverWithoutCar> = loggedTransaction {

        val baseQuery = DriverTable
            .join(UserTable, JoinType.INNER, DriverTable.userId, UserTable.id)
            .join(AssignmentTable, JoinType.LEFT, DriverTable.userId, AssignmentTable.driverId)

        val conditions = mutableListOf<Op<Boolean>>()

        conditions.add(DriverTable.vehicleId.isNull())

        if (!searchQuery.isNullOrBlank()) {
            val pattern = "%${searchQuery.trim().lowercase()}%"
            conditions.add(
                OrOp(
                    listOf(
                        UserTable.fullName.lowerCase() like pattern,
                        UserTable.phoneNumber.lowerCase() like pattern
                    )
                )
            )
        }

        val finalCondition = if (conditions.isEmpty()) Op.TRUE else AndOp(conditions)
        val deliveriesCount = AssignmentTable.id.count()
        val offset = (page - 1L) * pageSize

        val rows = baseQuery
            .select(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber,
                deliveriesCount
            )
            .where(finalCondition)
            .groupBy(
                UserTable.id,
                UserTable.fullName,
                UserTable.phoneNumber
            )
            .orderBy(UserTable.fullName, SortOrder.ASC)
            .limit(pageSize + 1)
            .offset(offset)
            .map { row ->
                DriverWithoutCar(
                    id = row[UserTable.id].value,
                    fullName = row[UserTable.fullName],
                    phoneNumber = row[UserTable.phoneNumber],
                    totalDeliveries = row[deliveriesCount].toInt()
                )
            }

        val hasMore = rows.size > pageSize
        val items = if (hasMore) rows.dropLast(1) else rows

        ListPaginatedResult(
            items = items,
            hasMore = hasMore
        )
    }

    suspend fun driverSchedule(driverId: Int): List<DriverWorkShift> = loggedTransaction {
        ensureDriverExists(driverId)
        DriverShiftTable
            .select(
                DriverShiftTable.dayOfWeek,
                DriverShiftTable.startTime,
                DriverShiftTable.endTime
            )
            .where { DriverShiftTable.driverId eq driverId }
            .orderBy(DriverShiftTable.dayOfWeek, SortOrder.ASC)
            .orderBy(DriverShiftTable.startTime, SortOrder.ASC)
            .map { row ->
                DriverWorkShift(
                    dayOfWeek = row[DriverShiftTable.dayOfWeek].toInt(),
                    startTime = row[DriverShiftTable.startTime].format(TIME_FORMAT),
                    endTime = row[DriverShiftTable.endTime].format(TIME_FORMAT)
                )
            }
    }

    suspend fun replaceDriverSchedule(driverId: Int, shifts: List<DriverWorkShift>) = loggedTransaction {
        ensureDriverExists(driverId)
        validateShifts(shifts)

        DriverShiftTable.deleteWhere { DriverShiftTable.driverId eq driverId }
        if (shifts.isEmpty()) return@loggedTransaction

        DriverShiftTable.batchInsert(shifts) { shift ->
            this[DriverShiftTable.driverId] = driverId
            this[DriverShiftTable.dayOfWeek] = shift.dayOfWeek.toShort()
            this[DriverShiftTable.startTime] = shift.startTime.toLocalTime()
            this[DriverShiftTable.endTime] = shift.endTime.toLocalTime()
        }
    }

    private fun ensureDriverExists(driverId: Int) {
        val exists = DriverTable
            .select(DriverTable.userId)
            .where { DriverTable.userId eq driverId }
            .any()
        if (!exists) throw StateConflictException("Водитель с ID $driverId не найден")
    }

    private fun validateShifts(shifts: List<DriverWorkShift>) {
        val intervals = mutableListOf<TimeInterval>()

        shifts.forEach { shift ->
            if (shift.dayOfWeek !in 1..7) {
                throw StateConflictException("dayOfWeek должен быть в диапазоне 1..7")
            }
            val start = shift.startTime.toLocalTime()
            val end = shift.endTime.toLocalTime()
            if (start == end) {
                throw StateConflictException(
                    "Некорректный интервал смены для дня ${shift.dayOfWeek}: startTime и endTime не должны совпадать"
                )
            }

            val dayOffsetMinutes = (shift.dayOfWeek - 1) * MINUTES_PER_DAY
            val startMinutes = start.hour * 60 + start.minute
            val endMinutes = end.hour * 60 + end.minute

            if (start < end) {
                intervals += TimeInterval(
                    startMinute = dayOffsetMinutes + startMinutes,
                    endMinute = dayOffsetMinutes + endMinutes
                )
            } else {
                intervals += TimeInterval(
                    startMinute = dayOffsetMinutes + startMinutes,
                    endMinute = dayOffsetMinutes + MINUTES_PER_DAY
                )
                intervals += TimeInterval(
                    startMinute = ((dayOffsetMinutes + MINUTES_PER_DAY) % MINUTES_PER_WEEK),
                    endMinute = ((dayOffsetMinutes + MINUTES_PER_DAY) % MINUTES_PER_WEEK) + endMinutes
                )
            }
        }

        val duplicatedIntervals = (intervals + intervals.map {
            TimeInterval(
                startMinute = it.startMinute + MINUTES_PER_WEEK,
                endMinute = it.endMinute + MINUTES_PER_WEEK
            )
        }).sortedBy { it.startMinute }

        for (i in 1 until duplicatedIntervals.size) {
            val prev = duplicatedIntervals[i - 1]
            val current = duplicatedIntervals[i]
            if (current.startMinute < prev.endMinute) {
                throw StateConflictException("Пересекающиеся смены в расписании")
            }
        }
    }

    private fun String.toLocalTime(): LocalTime {
        return try {
            LocalTime.parse(this, TIME_FORMAT)
        } catch (_: Exception) {
            LocalTime.parse(this)
        }
    }

    private companion object {
        private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private const val MINUTES_PER_DAY: Int = 24 * 60
        private const val MINUTES_PER_WEEK: Int = 7 * MINUTES_PER_DAY
    }
}

private data class TimeInterval(
    val startMinute: Int,
    val endMinute: Int
)
