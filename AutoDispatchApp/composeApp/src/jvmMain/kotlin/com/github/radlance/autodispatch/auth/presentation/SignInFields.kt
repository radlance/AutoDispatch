package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.log_in_to_control_panel
import autodispatch.composeapp.generated.resources.login
import autodispatch.composeapp.generated.resources.password
import autodispatch.composeapp.generated.resources.sign_in
import autodispatch.composeapp.generated.resources.welcome
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInFields(
    fieldsUiState: SignInFieldsUiState,
    onEvent: (SignInEvent) -> Unit,
    buttonEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Card(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = stringResource(Res.string.welcome), fontSize = 22.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.log_in_to_control_panel),
                    fontSize = 14.sp,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(Modifier.height(24.dp))
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
                    singleLine = true,
                    isError = fieldsUiState.loginErrorMessage.isNotEmpty(),
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
                    singleLine = true,
                    isError = fieldsUiState.passwordErrorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
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
    }
}