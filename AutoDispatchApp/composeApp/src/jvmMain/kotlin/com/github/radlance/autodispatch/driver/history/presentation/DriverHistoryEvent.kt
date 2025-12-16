package com.github.radlance.autodispatch.driver.history.presentation

import com.github.radlance.autodispatch.common.presentation.Event

interface DriverHistoryEvent : Event {

    fun apply(action: DriverHistoryAction)

    class DriverHistoryClick(private val driverId: Int) : DriverHistoryEvent {

        override fun apply(action: DriverHistoryAction) {
            action.applyDriverHistoryState(driverId)
        }
    }

    object CloseHistoryDialogClick : DriverHistoryEvent {
        override fun apply(action: DriverHistoryAction) {
            action.releaseDriverHistoryState()
        }
    }
}

interface DriverHistoryAction {

    fun applyDriverHistoryState(driverId: Int)

    fun releaseDriverHistoryState()
}