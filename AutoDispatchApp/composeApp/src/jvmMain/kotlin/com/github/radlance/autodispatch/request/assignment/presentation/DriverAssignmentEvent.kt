package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.Event
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

interface DriverAssignmentEvent : Event {

    fun apply(action: DriverAssignmentAction)

    class ChangeDriverStats(private val driverStats: DriverStats) : DriverAssignmentEvent {

        override fun apply(action: DriverAssignmentAction) {
            action.changeDriversStats(driverStats)
        }
    }

    class AssignRequestClick(private val requestId: Int, private val driverId: Int, private  val isReassign: Boolean) :
        DriverAssignmentEvent {

        override fun apply(action: DriverAssignmentAction) {
            action.assignRequest(requestId, driverId, isReassign)
        }
    }

    object ResetStates : DriverAssignmentEvent {

        override fun apply(action: DriverAssignmentAction) {
            action.resetStates()
        }
    }
}

interface DriverAssignmentAction {

    fun changeDriversStats(stats: DriverStats)

    fun assignRequest(requestId: Int, driverId: Int, isReassign: Boolean)

    fun resetStates()
}