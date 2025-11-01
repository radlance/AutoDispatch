package com.github.radlance.autodispatch.request.create.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.create_request
import autodispatch.composeapp.generated.resources.creating_new_request
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRequestDialog(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    onDismiss: () -> Unit,
    onSuccessCreateRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRequestViewModel = koinViewModel()
) {
    val fieldsUiState by viewModel.fieldsUiState.collectAsState()
    val customers by viewModel.customersState.collectAsState()
    val createRequestState by viewModel.createRequestState.collectAsState()

    val scrollState = rememberScrollState()
    val screenHeight = LocalWindowInfo.current.containerSize.height
    val maxDialogHeight = screenHeight * 0.6f

    LaunchedEffect(createRequestState) {
        if (createRequestState is FetchResultUiState.Success) {
            onSuccessCreateRequest()
            viewModel.reduce(CreateRequestEvent.ResetState)
            onDismiss()
        }
    }

    val isLoading = createRequestState is FetchResultUiState.Loading
    val error = (createRequestState as? FetchResultUiState.Error<String>)?.error

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        title = {
            Text(
                stringResource(Res.string.creating_new_request),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = maxDialogHeight.dp)) {
                    CreateRequestFields(
                        cities = cities,
                        cargoTypes = cargoTypes,
                        customers = customers,
                        onEvent = viewModel::reduce,
                        scrollState = scrollState,
                        fieldsUiState = fieldsUiState
                    )
                    if (!isLoading) {
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState),
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .offset(x = 10.dp)
                        )
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    AlertDialogDefaults.containerColor
                                ).clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {},
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = with(fieldsUiState) {
                    departureCity != null
                            && destinationCity != null
                            && cargoType != null
                            && companyNameFieldValue.isNotBlank()
                            && companyEmailFieldValue.isNotBlank()
                            && cargoWeightFieldValue.isNotBlank()
                            && loadingFieldValue.isNotBlank()
                            && unloadingFieldValue.isNotBlank()
                            && !isLoading
                },
                onClick = {
                    with(fieldsUiState) {
                        viewModel.reduce(
                            CreateRequestEvent.ClickCreate(
                                originId = departureCity!!.id,
                                destinationId = destinationCity!!.id,
                                companyName = companyNameFieldValue,
                                companyEmail = companyEmailFieldValue,
                                companyPhone = companyPhoneFieldValue,
                                cargoTypeId = cargoType!!.id,
                                cargoWeight = cargoWeightFieldValue,
                                cargoVolume = cargoVolumeFieldValue,
                                cargoDescription = cargoDescriptionFieldValue,
                                cargoLoading = loadingFieldValue,
                                cargoUnloading = unloadingFieldValue,
                                additionalInfo = additionalInfoFieldValue
                            )
                        )
                    }
                }
            ) {
                Text(text = stringResource(Res.string.create_request))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}