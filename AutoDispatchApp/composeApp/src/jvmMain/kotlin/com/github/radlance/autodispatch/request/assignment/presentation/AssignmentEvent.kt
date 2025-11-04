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

    object ResetFieldsState : AssignmentEvent {

        override fun apply(action: AssignmentAction) {
            action.resetFieldsState()
        }
    }
}

interface AssignmentAction {

    fun changeDriversStats(stats: DriverStats)

    fun resetFieldsState()
}