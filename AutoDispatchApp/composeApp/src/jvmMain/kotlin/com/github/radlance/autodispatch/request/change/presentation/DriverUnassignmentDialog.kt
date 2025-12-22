package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import org.jetbrains.compose.resources.stringResource

@Composable
fun DriverUnassignmentDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onStateError: (String) -> Unit,
    unassignmentState: FetchResultUiState<Unit, RequestError>,
    modifier: Modifier = Modifier
) {
    val isLoading = unassignmentState is FetchResultUiState.Loading
    val error = (unassignmentState as? FetchResultUiState.Error)?.error

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            if (!isLoading) {
                onDismissRequest()
            }
        },
        title = {
            Text(text = "Снятие водителя с заявки")
        },
        text = {
            Column {
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
                            onStateError(error.message)
                        }
                    }
                }
                Text(text = "Вы уверены, что хотите снять водителя с этой заявки?")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, enabled = !isLoading) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                Text(text = "Снять")
            }
        }
    )

}