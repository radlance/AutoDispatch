package com.github.radlance.autodispatch.splash.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.auto_request
import com.github.radlance.autodispatch.auth.presentation.AppIconBox
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen(
    onDelayFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(timeMillis = 500)
        onDelayFinish()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppIconBox(
            boxSize = 100.dp,
            clipAngle = 18.dp,
            iconSize = 70.dp
        )
        Spacer(Modifier.height(32.dp))
        Text(text = stringResource(Res.string.auto_request), fontSize = 40.sp)
    }
}