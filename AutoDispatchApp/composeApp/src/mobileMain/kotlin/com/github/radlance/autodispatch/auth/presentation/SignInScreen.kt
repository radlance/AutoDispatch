package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.auth.domain.LoginResponse
import com.github.radlance.autodispatch.common.domain.UserRole
import com.github.radlance.autodispatch.common.presentation.BaseColumn
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel()
) {
    val fieldsUiState by viewModel.fieldsUiState.collectAsStateWithLifecycle()
    val signInResultUiState by viewModel.authResultUiState.collectAsStateWithLifecycle()

    SignInScreen(
        fieldsUiState = fieldsUiState,
        signInResultUiState = signInResultUiState,
        navigateToHomeScreen = navigateToHomeScreen,
        clearInvalidRoleToken = viewModel::clearInvalidRoleToken,
        onEvent = viewModel::reduce,
        modifier = modifier
    )
}

@Composable
private fun SignInScreen(
    fieldsUiState: SignInFieldsUiState,
    signInResultUiState: FetchResultUiState<LoginResponse, String>,
    navigateToHomeScreen: () -> Unit,
    clearInvalidRoleToken: () -> Unit,
    onEvent: (SignInEvent) -> Unit,
    modifier: Modifier = Modifier
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
            if (it.role == UserRole.Driver) {
                navigateToHomeScreen()
            } else {
                AlertDialog(
                    onDismissRequest = {
                        clearInvalidRoleToken()
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
                        Text(text = "Этот аккаунт принадлежит диспетчеру. Вход в приложение диспетчера с такими данными невозможен.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                clearInvalidRoleToken()
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        BaseColumn(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            AppIconBox()
            Spacer(Modifier.height(16.dp))

            Text(
                text = "АвтоЗаявка",
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Приложение водителя",
                modifier = Modifier.alpha(0.5f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(32.dp))

            SignInFields(
                fieldsUiState = fieldsUiState,
                buttonEnabled = signInResultUiState !is FetchResultUiState.Loading,
                onEvent = onEvent
            )
            Spacer(Modifier.weight(1.5f))
        }
    }
}
