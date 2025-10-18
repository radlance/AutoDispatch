package com.github.radlance.autodispatch.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.radlance.autodispatch.uikit.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            PlatformApp()
        }
    }
}