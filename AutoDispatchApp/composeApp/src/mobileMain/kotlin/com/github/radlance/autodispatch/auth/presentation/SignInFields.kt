package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.login
import autodispatch.composeapp.generated.resources.password
import autodispatch.composeapp.generated.resources.sign_in
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInFields(
    fieldsUiState: SignInFieldsUiState,
    onEvent: (SignInEvent) -> Unit,
    buttonEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = fieldsUiState.loginFieldValue,
            onValueChange = { onEvent(SignInEvent.ChangeLogin(it)) },
            label = {
                val label = if (
                    fieldsUiState.loginFieldValue.isEmpty() || fieldsUiState.loginErrorMessage.isEmpty()
                ) stringResource(Res.string.login) else {
                    fieldsUiState.loginErrorMessage
                }

                Text(text = label)
            },
            isError = fieldsUiState.loginErrorMessage.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = fieldsUiState.passwordFieldValue,
            onValueChange = { onEvent(SignInEvent.ChangePassword(it)) },
            label = {
                val label = if (
                    fieldsUiState.passwordFieldValue.isEmpty() || fieldsUiState.passwordErrorMessage.isEmpty()
                ) stringResource(Res.string.password) else {
                    fieldsUiState.passwordErrorMessage
                }

                Text(text = label)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (!showPassword) {
                PasswordVisualTransformation()
            } else VisualTransformation.None,
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Default)
                ) {
                    val icon = if (showPassword) {
                        Icons.Default.VisibilityOff
                    } else Icons.Default.Visibility
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            isError = fieldsUiState.passwordErrorMessage.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                keyboardController?.hide()
                onEvent(
                    SignInEvent.ClickSignIn(
                        login = fieldsUiState.loginFieldValue,
                        password = fieldsUiState.passwordFieldValue
                    )
                )
            },
            enabled = buttonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(Res.string.sign_in))
        }
    }
}