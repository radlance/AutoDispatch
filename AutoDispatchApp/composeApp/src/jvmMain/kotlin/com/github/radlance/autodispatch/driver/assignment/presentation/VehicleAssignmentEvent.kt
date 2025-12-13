package com.github.radlance.autodispatch.driver.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.Event
import com.github.radlance.autodispatch.reuqest.core.domain.Vehicle

interface VehicleAssignmentEvent : Event {

    fun apply(action: VehicleAssignmentAction)

    class ChangeVehicle(private val vehicle: Vehicle) : VehicleAssignmentEvent {

        override fun apply(action: VehicleAssignmentAction) {
            action.changeVehicle(vehicle)
        }
    }

    class AssignVehicleClick(
        private val vehicleId: Int,
        private val driverId: Int,
        private val isReassign: Boolean
    ) : VehicleAssignmentEvent {

        override fun apply(action: VehicleAssignmentAction) {
            action.assignVehicle(vehicleId, driverId, isReassign)
        }
    }

    object ResetStates : VehicleAssignmentEvent {

        override fun apply(action: VehicleAssignmentAction) {
            action.resetStates()
        }
    }
}

interface VehicleAssignmentAction {

    fun changeVehicle(vehicle: Vehicle)

    fun assignVehicle(vehicleId: Int, driverId: Int, isReassign: Boolean)

    fun resetStates()
}