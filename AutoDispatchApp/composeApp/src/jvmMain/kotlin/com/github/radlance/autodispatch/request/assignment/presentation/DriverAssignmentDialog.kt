package com.github.radlance.autodispatch.request.assignment.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import autodispatch.composeapp.generated.resources.assign
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.driver_assignment
import autodispatch.composeapp.generated.resources.loading_error
import autodispatch.composeapp.generated.resources.reassign
import autodispatch.composeapp.generated.resources.retry
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DriverAssignmentDialog(
    onDismiss: () -> Unit,
    request: Request,
    onSuccessAssignRequest: () -> Unit,
    onStateReassignError: (String) -> Unit,
    modifier: Modifier = Modifier,
    isReassign: Boolean,
    assignedDriverId: Int?,
    viewModel: AssignmentViewModel = koinViewModel()
) {
    val requestAssignmentState by viewModel.requestAssignmentState.collectAsState()
    val assignRequestState by viewModel.assignRequestState.collectAsState()
    val fieldsState by viewModel.assignmentFieldsState.collectAsState()
    val isLoading = assignRequestState is FetchResultUiState.Loading
    val error = (assignRequestState as? FetchResultUiState.Error<DeliveryError>)?.error

    val onDismissAction = {
        onDismiss()
        viewModel.reduce(AssignmentEvent.ResetStates)
    }

    LaunchedEffect(Unit) {
        viewModel.loadRequestAssignment()
    }

    LaunchedEffect(assignRequestState) {
        if (assignRequestState is FetchResultUiState.Success) {
            onDismissAction()
            onSuccessAssignRequest()
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismissAction()
        },
        title = {
            Text(text = stringResource(Res.string.driver_assignment))
        },
        text = {
            Box(Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    AnimatedVisibility(visible = error != null) {
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
                            if (error!! is DeliveryError.BaseError) {
                                Text(
                                    text = error.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                viewModel.reduce(AssignmentEvent.ResetStates)
                                onStateReassignError(error.message)
                            }
                        }
                    }
                    Card {
                        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                            Text("Заявка ${request.requestNumber}", modifier = Modifier.alpha(0.7f))
                            Spacer(Modifier.height(12.dp))
                            Text("${request.origin} → ${request.destination}", fontSize = 16.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("${request.cargo.type.name} • ${request.createdAt.date}", modifier = Modifier.alpha(0.7f))
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    requestAssignmentState.Reduce(
                        onLoading = {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(86.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        onSuccess = { stats ->
                            LaunchedEffect(assignedDriverId) {
                                if (isReassign) {
                                    viewModel.reduce(
                                        AssignmentEvent.ChangeDriverStats(
                                            stats.first {
                                                it.driverId == assignedDriverId!!
                                            }
                                        )
                                    )
                                }
                            }
                            DriverAssignmentFields(
                                driversStats = stats,
                                fieldsState = fieldsState,
                                onEvent = viewModel::reduce
                            )
                        },
                        onError = { error ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
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
                                        Text(text = error, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = viewModel::loadRequestAssignment,
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
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
            val isDriverSelected = fieldsState.selectedDriverStats != null
            val hasDriverChanged = fieldsState.selectedDriverStats?.driverId != assignedDriverId
            val isButtonEnabled =
                isDriverSelected && !isLoading && (!isReassign || hasDriverChanged) && fieldsState.selectedDriverStats?.vehicleModel != null

            Button(
                onClick = {
                    viewModel.reduce(
                        AssignmentEvent.AssignRequestClick(
                            requestId = request.id,
                            driverId = fieldsState.selectedDriverStats!!.driverId,
                            isReassign = isReassign
                        )
                    )
                },
                enabled = isButtonEnabled
            ) {
                val text = if (isReassign) {
                    Res.string.reassign
                } else Res.string.assign
                Text(text = stringResource(text))
            }
        },
        modifier = modifier
    )
}