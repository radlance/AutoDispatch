package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproveDocumentDialog(
    onDismissRequest: () -> Unit,
    onApprove: () -> Unit,
    approveState: FetchResultUiState<Unit, String>,
    modifier: Modifier = Modifier
) {
    val isLoading = approveState is FetchResultUiState.Loading
    val error = (approveState as? FetchResultUiState.Error<String>)?.error

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            if (!isLoading) {
                onDismissRequest()
            }
        },
        title = {
            Text(text = "Одобрить документы")
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
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Text(text = "Вы уверены, что хотите одобрить документы по этой заявке? После одобрения заявка будет завершена.")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, enabled = !isLoading) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onApprove,
                enabled = !isLoading
            ) {
                Text(text = "Одобрить")
            }
        }
    )

}