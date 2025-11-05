package com.github.radlance.autodispatch.request.assignment.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
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
                    onSuccess = { stats ->
                        DriverAssignmentFields(
                            driversStats = stats,
                            fieldsState = fieldsState,
                            onEvent = viewModel::reduce,
                        )
                    },
                    onError = { error ->

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.3f
                                ),
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Ошибка",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Ошибка загрузки",
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
                                    onClick = viewModel::loadRequestAssignment,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                                ) {
                                    Text("Повторить")
                                }
                            }
                        }
                    }
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
                enabled = fieldsState.selectedDriverStats != null
            ) {
                Text(text = "Назначить")
            }
        },
        modifier = modifier
    )
}