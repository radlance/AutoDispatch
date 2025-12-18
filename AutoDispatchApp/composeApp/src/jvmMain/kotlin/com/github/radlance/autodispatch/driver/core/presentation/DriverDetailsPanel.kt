package com.github.radlance.autodispatch.driver.core.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.driver.assignment.presentation.VehicleAssignmentDialog
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.driver.request.presentation.DriverRequestAssignmentDialog
import kotlinx.coroutines.launch

@Composable
fun DriverDetailsPanel(
    driver: Driver,
    onClosePanel: () -> Unit,
    onSuccessAssignDriver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showVehicleAssignmentDialog by remember { mutableStateOf(false) }
    var showDriverRequestAssignmentDialog by remember { mutableStateOf(false) }
    var isReassign by remember { mutableStateOf(driver.vehicle != null) }
    val scope = rememberCoroutineScope()

    if (showVehicleAssignmentDialog) {
        VehicleAssignmentDialog(
            onDismiss = { showVehicleAssignmentDialog = false },
            driver = driver,
            onSuccessAssignDriver = {
                onSuccessAssignDriver()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            },
            isReassign = isReassign,
            assignedVehicleId = driver.vehicle?.id
        )
    }

    if (showDriverRequestAssignmentDialog) {
        DriverRequestAssignmentDialog(
            onDismiss = { showDriverRequestAssignmentDialog = false },
            driver = driver
        )
    }

    SelectionContainer(
        modifier = Modifier.pointerHoverIcon(
            PointerIcon.Default,
            overrideDescendants = true
        )
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            DriverPanelHeader(onClose = onClosePanel)

            Box {
                DriverDetailsSections(
                    scrollState = scrollState,
                    driver = driver,
                    onShowVehicleAssignmentDialog = {
                        isReassign = it
                        showVehicleAssignmentDialog = true
                    },
                    onShowDriverRequestAssignmentDialog = {
                        showDriverRequestAssignmentDialog = true
                    }
                )
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}