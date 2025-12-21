package com.github.radlance.autodispatch.vehicle.core.presentation

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
import com.github.radlance.autodispatch.vehicle.assignment.presentation.DriverVehicleAssignmentDialog
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleDetailed
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun VehicleDetailsPanel(
    vehicle: VehicleDetailed,
    onClosePanel: () -> Unit,
    onSuccessAssignVehicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showDriverAssignmentDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showStateErrorDialog by remember { mutableStateOf(false) }
    var stateErrorMessage by remember { mutableStateOf("") }

    if (showStateErrorDialog) {
        val onDismiss: () -> Unit = {
            showStateErrorDialog = false
            onSuccessAssignVehicle()
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
                Text(text = stateErrorMessage)
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

    if (showDriverAssignmentDialog) {
        DriverVehicleAssignmentDialog(
            onDismiss = { showDriverAssignmentDialog = false },
            vehicle = vehicle,
            onSuccessAssignVehicle = {
                onSuccessAssignVehicle()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            },
            onStateError = {
                showDriverAssignmentDialog = false
                showStateErrorDialog = true
                stateErrorMessage = it
            }
        )
    }

    DefaultPointerSelectionContainer {
        Column(modifier = modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Информация об автомобиле",
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
                VehicleDetailsSections(
                    scrollState = scrollState,
                    vehicle = vehicle,
                    onShowDriverAssignmentDialog = { showDriverAssignmentDialog = true }
                )
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}