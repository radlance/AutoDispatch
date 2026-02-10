package com.github.radlance.autodispatch.driver.unassignment.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import com.github.radlance.autodispatch.common.presentation.CustomDialog
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.driver.core.domain.Driver
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VehicleUnassignmentDialog(
    onDismiss: () -> Unit,
    driver: Driver,
    onSuccessUnassignVehicle: () -> Unit,
    onStateError: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleUnassignmentViewModel = koinViewModel()
) {
    val unassignmentState by viewModel.unassignmentState.collectAsState()
    val isLoading = unassignmentState is FetchResultUiState.Loading
    val error = (unassignmentState as? FetchResultUiState.Error)?.error
    var callOnSuccess by remember { mutableStateOf(false) }
    CustomDialog(
        modifier = modifier,
        allowDismiss = !isLoading,
        onDismissRequest = onDismiss,
        onFinish = {
            viewModel.resetState()
            if (callOnSuccess) {
                onSuccessUnassignVehicle()
                callOnSuccess = false
            }
        },
        title = {
            Text(
                text = "Открепление автомобиля",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        content = { requestDismiss ->
            Column(modifier = Modifier.fillMaxWidth()) {
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

                        if (error is RequestError.BaseError) {
                            Text(
                                text = error.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            viewModel.resetState()
                            requestDismiss()
                            onStateError(error.message)
                        }
                    }
                }
                Text(text = "Вы уверены чтто хотите открепить автомобиль от водителя?")
                Spacer(Modifier.height(12.dp))
                Card {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Text("Автомобиль", modifier = Modifier.alpha(0.7f))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "${driver.vehicle!!.model} • ${driver.vehicle.licensePlate} • г/п: ${driver.vehicle.payloadCapacity} кг",
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
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
            }
            LaunchedEffect(unassignmentState) {
                if (unassignmentState is FetchResultUiState.Success) {
                    callOnSuccess = true
                    requestDismiss()
                }
            }
        },
        buttons = { requestDismiss ->
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { if (!isLoading) requestDismiss() }) {
                Text(text = stringResource(Res.string.cancel))
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = {
                    viewModel.unassignVehicle(driver.id)
                }
            ) {
                Text(text = "Открепить")
            }
        }
    )
}