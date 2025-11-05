package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.Event
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

interface AssignmentEvent : Event {

    fun apply(action: AssignmentAction)

    class ChangeDriverStats(private val driverStats: DriverStats) : AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.changeDriversStats(driverStats)
        }
    }

    class AssignRequestButtonClick(private val requestId: Int, private val driverId: Int) :
        AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.assignRequest(requestId, driverId)
        }
    }

    object ResetStates : AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.resetStates()
        }
    }
}

interface AssignmentAction {

    fun changeDriversStats(stats: DriverStats)

    fun assignRequest(requestId: Int, driverId: Int)

    fun resetStates()
}