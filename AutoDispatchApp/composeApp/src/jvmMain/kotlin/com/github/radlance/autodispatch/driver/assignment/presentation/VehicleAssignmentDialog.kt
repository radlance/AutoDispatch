package com.github.radlance.autodispatch.driver.assignment.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.attach
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.loading_error
import autodispatch.composeapp.generated.resources.reassign
import autodispatch.composeapp.generated.resources.retry
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.driver.core.domain.Driver
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VehicleAssignmentDialog(
    onDismiss: () -> Unit,
    driver: Driver,
    onSuccessAssignDriver: () -> Unit,
    modifier: Modifier = Modifier,
    isReassign: Boolean,
    assignedVehicleId: Int?,
    viewModel: VehicleAssignmentViewModel = koinViewModel()
) {
    val vehicleAssignmentsState by viewModel.vehicleAssignmentsState.collectAsState()
    val assignDriverState by viewModel.assignDriverState.collectAsState()
    val assignRequestState by viewModel.assignDriverState.collectAsState()
    val fieldsState by viewModel.vehicleAssignmentFieldsState.collectAsState()
    val isLoading = assignRequestState is FetchResultUiState.Loading
    val error = (assignRequestState as? FetchResultUiState.Error<String>)?.error

    val onDismissAction = {
        onDismiss()
        viewModel.reduce(VehicleAssignmentEvent.ResetStates)
    }

    LaunchedEffect(Unit) {
        viewModel.loadVehicleAssignments()
    }

    LaunchedEffect(assignDriverState) {
        if (assignDriverState is FetchResultUiState.Success) {
            onDismissAction()
            onSuccessAssignDriver()
        }
    }

    val isDriverSelected = fieldsState.selectedVehicle != null
    val hasDriverChanged = fieldsState.selectedVehicle?.id != assignedVehicleId
    val isButtonEnabled =
        isDriverSelected && !isLoading && (!isReassign || hasDriverChanged) && (!isReassign || driver.status.id == 1)

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismissAction()
        },
        title = {
            Text(text = "Назначение автомобиля")
        },
        text = {

            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    error?.let {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Card {
                        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                            Text("Водитель", modifier = Modifier.alpha(0.7f))
                            Spacer(Modifier.height(12.dp))
                            Text(driver.fullName, fontSize = 16.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                driver.phoneNumber,
                                modifier = Modifier.alpha(0.7f)
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    driver.vehicle?.let {
                        Card {
                            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                                Text("Текущий автомобиль", modifier = Modifier.alpha(0.7f))
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "${driver.vehicle.model} • ${driver.vehicle.licensePlate}",
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                    vehicleAssignmentsState.Reduce(
                        onLoading = {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(86.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        onSuccess = { vehicles ->
                            VehicleAssignmentFields(
                                vehicles = vehicles,
                                fieldsState = fieldsState,
                                onEvent = viewModel::reduce
                            )
                        },
                        onError = { error ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.2f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(Res.string.loading_error),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = error,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = viewModel::loadVehicleAssignments,
                                        contentPadding = PaddingValues(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        )
                                    ) {
                                        Text(stringResource(Res.string.retry))
                                    }
                                }
                            }
                        }
                    )
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(AlertDialogDefaults.containerColor)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },

        dismissButton = {
            TextButton(onClick = onDismissAction, enabled = !isLoading) {
                Text(text = stringResource(Res.string.cancel))
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    viewModel.reduce(
                        VehicleAssignmentEvent.AssignVehicleClick(
                            driverId = driver.id,
                            vehicleId = fieldsState.selectedVehicle!!.id,
                            isReassign = isReassign
                        )
                    )
                },
                enabled = isButtonEnabled
            ) {
                val text = if (isReassign) {
                    Res.string.reassign
                } else Res.string.attach
                Text(text = stringResource(text))
            }
        },
        modifier = modifier
    )
}