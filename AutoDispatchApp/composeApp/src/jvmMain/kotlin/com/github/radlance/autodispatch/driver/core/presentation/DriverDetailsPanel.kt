package com.github.radlance.autodispatch.driver.core.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.close_panel
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer
import com.github.radlance.autodispatch.driver.assignment.presentation.VehicleAssignmentDialog
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.driver.request.presentation.DriverRequestAssignmentDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

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
    val scope = rememberCoroutineScope()
    var isReassign by remember { mutableStateOf(driver.vehicle != null) }
    var showReassignErrorDialog by remember { mutableStateOf(false) }
    var reassignErrorMessage by remember { mutableStateOf("") }

    if (showReassignErrorDialog) {
        val onDismiss: () -> Unit = {
            showReassignErrorDialog = false
            onSuccessAssignDriver()
            scope.launch {
                scrollState.animateScrollTo(0)
            }
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null
                )
            },
            title = {
                Text(text = "Ошибка")
            },
            text = {
                Text(text = reassignErrorMessage)
            },
            dismissButton = {},
            confirmButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(text = "ОК")
                }
            }
        )
    }

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
            onSuccessAssignDriver = {
                onSuccessAssignDriver()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            },
            onStateReassignError = {
                showDriverRequestAssignmentDialog = false
                showReassignErrorDialog = true
                reassignErrorMessage = it
            },
            driver = driver
        )
    }

    DefaultPointerSelectionContainer {
        Column(modifier = modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Профиль водителя",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onClosePanel) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.close_panel)
                    )
                }
            }
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