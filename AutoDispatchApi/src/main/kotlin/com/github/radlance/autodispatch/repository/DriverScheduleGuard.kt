package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.DriverShiftTable
import com.github.radlance.autodispatch.exception.WorkScheduleException
import java.lang.Math.floorMod
import java.time.Clock
import java.time.LocalTime
import java.time.ZonedDateTime

class DriverScheduleGuard(
    private val clock: Clock
) {
    fun ensureDriverWorkingNow(
        driverId: Int,
        buildMessage: (ScheduleEvaluation) -> String
    ) {
        val shifts = DriverShiftTable
            .select(DriverShiftTable.dayOfWeek, DriverShiftTable.startTime, DriverShiftTable.endTime)
            .where { DriverShiftTable.driverId eq driverId }
            .map {
                ShiftWindow(
                    dayOfWeek = it[DriverShiftTable.dayOfWeek].toInt(),
                    startTime = it[DriverShiftTable.startTime],
                    endTime = it[DriverShiftTable.endTime]
                )
            }

        if (shifts.isEmpty()) return

        val now = ZonedDateTime.now(clock)
        val evaluation = evaluate(shifts, now)

        if (!evaluation.isWorkingNow) {
            throw WorkScheduleException(buildMessage(evaluation))
        }
    }

    fun evaluate(shifts: List<ShiftWindow>): ScheduleEvaluation {
        return evaluate(shifts, ZonedDateTime.now(clock))
    }

    private fun evaluate(shifts: List<ShiftWindow>, now: ZonedDateTime): ScheduleEvaluation {
        if (shifts.isEmpty()) {
            return ScheduleEvaluation(isWorkingNow = true, hint = "График не задан")
        }

        val nowMinute = toWeekMinute(now.dayOfWeek.value, now.toLocalTime())
        val intervals = toLinearIntervals(shifts)
        val isWorkingNow = intervals.any { nowMinute in it.startMinute until it.endMinute }

        val nextShift = findNextShift(shifts, nowMinute)
        val hint = nextShift?.let {
            "Ближайшая смена ${dayName(it.dayOfWeek)}, ${formatTime(it.startTime)}-${formatTime(it.endTime)}."
        } ?: "Ближайшей смены нет."

        return ScheduleEvaluation(
            isWorkingNow = isWorkingNow,
            hint = hint
        )
    }

    private fun toLinearIntervals(shifts: List<ShiftWindow>): List<LinearInterval> {
        val baseIntervals = shifts.flatMap { shift ->
            val dayOffset = (shift.dayOfWeek - 1) * MINUTES_PER_DAY
            val start = dayOffset + shift.startTime.hour * 60 + shift.startTime.minute
            val end = dayOffset + shift.endTime.hour * 60 + shift.endTime.minute
            if (start < end) {
                listOf(LinearInterval(start, end))
            } else {
                listOf(
                    LinearInterval(start, dayOffset + MINUTES_PER_DAY),
                    LinearInterval(
                        (dayOffset + MINUTES_PER_DAY) % MINUTES_PER_WEEK,
                        ((dayOffset + MINUTES_PER_DAY) % MINUTES_PER_WEEK) + (shift.endTime.hour * 60 + shift.endTime.minute)
                    )
                )
            }
        }

        return (baseIntervals + baseIntervals.map {
            LinearInterval(
                startMinute = it.startMinute + MINUTES_PER_WEEK,
                endMinute = it.endMinute + MINUTES_PER_WEEK
            )
        }).sortedBy { it.startMinute }
    }

    private fun toWeekMinute(dayOfWeek: Int, time: LocalTime): Int {
        return (dayOfWeek - 1) * MINUTES_PER_DAY + time.hour * 60 + time.minute
    }

    private fun formatTime(time: LocalTime): String {
        val hour = time.hour.toString()
        val minute = time.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    private fun dayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "ПН"
            2 -> "ВТ"
            3 -> "СР"
            4 -> "ЧТ"
            5 -> "ПТ"
            6 -> "СБ"
            7 -> "ВС"
            else -> dayOfWeek.toString()
        }
    }

    private fun findNextShift(shifts: List<ShiftWindow>, nowMinute: Int): ShiftWindow? {
        return shifts.minByOrNull { shift ->
            val shiftStart = toWeekMinute(shift.dayOfWeek, shift.startTime)
            floorMod(shiftStart - nowMinute, MINUTES_PER_WEEK)
        }
    }

    data class ShiftWindow(
        val dayOfWeek: Int,
        val startTime: LocalTime,
        val endTime: LocalTime
    )

    data class ScheduleEvaluation(
        val isWorkingNow: Boolean,
        val hint: String
    )

    private data class LinearInterval(
        val startMinute: Int,
        val endMinute: Int
    )

    private companion object {
        private const val MINUTES_PER_DAY: Int = 24 * 60
        private const val MINUTES_PER_WEEK: Int = 7 * MINUTES_PER_DAY
    }
}
