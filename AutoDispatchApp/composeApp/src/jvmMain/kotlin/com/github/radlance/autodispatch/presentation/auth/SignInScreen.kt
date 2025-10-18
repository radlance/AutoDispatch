package com.github.radlance.autodispatch.presentation.auth

import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.presentation.common.BaseColumn

@Composable
internal fun SignInScreen(
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loginFieldValue by rememberSaveable { mutableStateOf("") }
    var passwordFieldValue by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

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
                        onSignInClick = onSignInClick,
                        loginFieldValue = loginFieldValue,
                        onLoginFieldChange = { loginFieldValue = it },
                        passwordFieldValue = passwordFieldValue,
                        onPasswordFieldChange = { passwordFieldValue = it },
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