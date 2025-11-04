package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.Event
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.assignment.domain.VehicleStats

interface AssignmentEvent : Event {

    fun apply(action: AssignmentAction)

    class ChangeDriverStats(private val driverStats: DriverStats) : AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.changeDriversStats(driverStats)
        }
    }

    class ChangeVehicleStats(private val vehicleStats: VehicleStats) : AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.changeVehicleStats(vehicleStats)
        }
    }

    object ResetFieldsState : AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.resetFieldsState()
        }
    }
}

interface AssignmentAction {

    fun changeDriversStats(stats: DriverStats)

    fun changeVehicleStats(stats: VehicleStats)

    fun resetFieldsState()
}