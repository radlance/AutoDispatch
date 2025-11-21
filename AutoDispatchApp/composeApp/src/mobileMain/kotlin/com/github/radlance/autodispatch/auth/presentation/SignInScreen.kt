package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.log_in_to_the_system
import com.github.radlance.autodispatch.common.presentation.BaseColumn
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
        onEvent = viewModel::reduce,
        modifier = modifier
    )
}

@Composable
private fun SignInScreen(
    fieldsUiState: SignInFieldsUiState,
    signInResultUiState: FetchResultUiState<String, String>,
    navigateToHomeScreen: () -> Unit,
    onEvent: (SignInEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarMessage = stringResource(Res.string.log_in_to_the_system)

    signInResultUiState.Reduce(
        onLoading = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    snackBarMessage,
                    duration = SnackbarDuration.Indefinite
                )
            }
        },
        onSuccess = {
            snackbarHostState.currentSnackbarData?.dismiss()
            navigateToHomeScreen()
        },
        onError = {
            LaunchedEffect(signInResultUiState) {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(it)
                }
            }
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
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