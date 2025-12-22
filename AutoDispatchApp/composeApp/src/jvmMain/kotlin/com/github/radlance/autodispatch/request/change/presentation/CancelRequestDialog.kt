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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.cancel_variant
import autodispatch.composeapp.generated.resources.request_cancellation
import autodispatch.composeapp.generated.resources.you_want_to_cancel_request
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import org.jetbrains.compose.resources.stringResource

@Composable
fun CancelRequestDialog(
    onDismissDialog: () -> Unit,
    onConfirm: () -> Unit,
    onStateError: (String) -> Unit,
    cancelState: FetchResultUiState<Unit, RequestError>,
    requestNumber: String,
    modifier: Modifier = Modifier
) {
    val isLoading = cancelState is FetchResultUiState.Loading
    val error = (cancelState as? FetchResultUiState.Error)?.error

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            if (!isLoading) {
                onDismissDialog()
            }
        },
        title = {
            Text(text = stringResource(Res.string.request_cancellation))
        },
        text = {
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
                            onStateError(error.message)
                        }
                    }
                }

                Text(
                    buildAnnotatedString {
                        append("${stringResource(Res.string.you_want_to_cancel_request)} ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(requestNumber)
                        }
                        append("?")
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog, enabled = !isLoading) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                Text(text = stringResource(Res.string.cancel_variant))
            }
        }
    )
}