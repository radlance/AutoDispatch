package com.github.radlance.autodispatch.request.presentation.create

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.create_request
import autodispatch.composeapp.generated.resources.creating_new_request
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.City
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRequestDialog(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRequestViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val fieldsUiState by viewModel.fieldsUiState.collectAsState()

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(Res.string.creating_new_request),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                CreateRequestFields(
                    cities = cities,
                    cargoTypes = cargoTypes,
                    onEvent = viewModel::reduce,
                    fieldsUiState = fieldsUiState
                )
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .offset(x = 10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    with(fieldsUiState) {
                        viewModel.reduce(
                            CreateRequestEvent.ClickCreate(
                                companyName = companyNameFieldValue,
                                companyEmail = companyEmailFieldValue,
                                companyPhone = companyPhoneFieldValue,
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
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}