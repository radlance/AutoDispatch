package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.auto_request
import autodispatch.composeapp.generated.resources.control_panel
import com.github.radlance.autodispatch.common.presentation.AppIconBox
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInHeader(modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        AppIconBox()

        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = stringResource(Res.string.auto_request), fontSize = 28.sp)
            Text(text = stringResource(Res.string.control_panel), modifier = Modifier.alpha(0.5f))
        }
    }
}