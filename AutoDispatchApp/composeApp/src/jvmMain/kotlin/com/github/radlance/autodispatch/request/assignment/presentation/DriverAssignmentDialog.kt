package com.github.radlance.autodispatch.request.assignment.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import com.github.radlance.autodispatch.request.core.domain.Request
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DriverAssignmentDialog(
    onDismiss: () -> Unit,
    request: Request,
    modifier: Modifier = Modifier,
    viewModel: AssignmentViewModel = koinViewModel()
) {
    val requestAssignmentState by viewModel.requestAssignmentState.collectAsState()
    val fieldsState by viewModel.assignmentFieldsState.collectAsState()

    val onDismissAction = {
        onDismiss()
        viewModel.reduce(AssignmentEvent.ResetFieldsState)
    }

    AlertDialog(
        onDismissRequest = onDismissAction,
        title = {
            Text(text = "Назначение водителя и автомобиля")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                Card {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Text(
                            text = "Заявка ${request.requestNumber}",
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(text = "${request.origin} → ${request.destination}", fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "${request.cargoTypeName} • ${request.createdAt.date}",
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))

                requestAssignmentState.Reduce(
                    onLoading = {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    onSuccess = { request ->
                        DriverAssignmentFields(
                            driversStats = request.driversStats,
                            vehiclesStats = request.vehiclesStats,
                            fieldsState = fieldsState,
                            onEvent = viewModel::reduce
                        )
                    },
                    onError = {}
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissAction) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = {},
                enabled = fieldsState.selectedDriverStats != null && fieldsState.selectedVehicleStats != null
            ) {
                Text(text = "Назначить")
            }
        },
        modifier = modifier
    )
}