package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.auth.domain.LoginResponse
import com.github.radlance.autodispatch.common.presentation.BaseColumn
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SignInScreen(
    navigateToControlPanel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel()
) {
    val fieldsUiState by viewModel.fieldsUiState.collectAsStateWithLifecycle()
    val signInResultUiState by viewModel.authResultUiState.collectAsStateWithLifecycle()

    SignInScreen(
        fieldsUiState = fieldsUiState,
        signInResultUiState = signInResultUiState,
        navigateToControlPanel = navigateToControlPanel,
        onEvent = viewModel::reduce,
        modifier = modifier
    )
}

@Composable
private fun SignInScreen(
    fieldsUiState: SignInFieldsUiState,
    signInResultUiState: FetchResultUiState<LoginResponse, String>,
    navigateToControlPanel: () -> Unit,
    onEvent: (SignInEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        signInResultUiState.Reduce(
            onLoading = {
                Dialog(onDismissRequest = {}) {
                    Box(
                        modifier = Modifier.clip(
                            RoundedCornerShape(18.dp)
                        ).background(AlertDialogDefaults.containerColor)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    }
                }
            },
            onSuccess = {
                if (it.roleId == 1) {
                    navigateToControlPanel()
                } else {
                    AlertDialog(
                        onDismissRequest = {
                            onEvent(SignInEvent.ResetState)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null
                            )
                        },
                        title = {
                            Text(text = "Ошибка")
                        },
                        text = {
                            Text(text = "Этот аккаунт принадлежит водителю. Вход в приложение диспетчера с такими данными невозможен.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onEvent(SignInEvent.ResetState)
                                }
                            ) {
                                Text(text = "ОК")
                            }
                        }
                    )
                }
            },
            onError = {
                AlertDialog(
                    onDismissRequest = {
                        onEvent(SignInEvent.ResetState)
                    },
                    icon = {
                        Icon(imageVector = Icons.Outlined.WarningAmber, contentDescription = null)
                    },
                    title = {
                        Text(text = "Ошибка")
                    },
                    text = {
                        Text(text = it)
                    },
                    dismissButton = {},
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onEvent(SignInEvent.ResetState)
                            }
                        ) {
                            Text(text = "ОК")
                        }
                    }
                )
            }
        )

        Box {
            BaseColumn(
                scrollState = scrollState,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .widthIn(max = 900.dp)
                        .fillMaxWidth()
                ) {
                    SignInHeader()
                    Spacer(Modifier.height(36.dp))
                    Row {
                        FeaturesColumn(modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(36.dp))
                        SignInFields(
                            fieldsUiState = fieldsUiState,
                            onEvent = onEvent,
                            buttonEnabled = signInResultUiState !is FetchResultUiState.Loading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(end = 4.dp),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}