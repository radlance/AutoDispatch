package com.github.radlance.autodispatch.navigation.core

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
import com.github.radlance.autodispatch.common.presentation.AppIconBox
import kotlinx.coroutines.delay

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
        Text(text = "АвтоЗаявка", fontSize = 40.sp)
    }
}