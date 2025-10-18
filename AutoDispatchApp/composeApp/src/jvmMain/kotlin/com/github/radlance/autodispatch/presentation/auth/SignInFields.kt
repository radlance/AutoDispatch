package com.github.radlance.autodispatch.presentation.auth

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
import java.awt.Cursor

@Composable
fun SignInFields(
    loginFieldValue: String,
    onLoginFieldChange: (String) -> Unit,
    passwordFieldValue: String,
    onPasswordFieldChange: (String) -> Unit,
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
                    value = loginFieldValue,
                    onValueChange = onLoginFieldChange,
                    label = { Text(text = stringResource(Res.string.login)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = passwordFieldValue,
                    onValueChange = onPasswordFieldChange,
                    label = { Text(text = stringResource(Res.string.password)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (!showPassword) {
                        PasswordVisualTransformation()
                    } else VisualTransformation.None,
                    trailingIcon = {
                        IconButton(
                            onClick = { showPassword = !showPassword },
                            modifier = Modifier.pointerHoverIcon(
                                PointerIcon(
                                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                                )
                            )
                        ) {
                            val icon = if (showPassword) {
                                Icons.Default.VisibilityOff
                            } else Icons.Default.Visibility
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(Res.string.sign_in))
                }
            }
        }
    }
}