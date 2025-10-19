package com.github.radlance.autodispatch.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.radlance.autodispatch.navigation.core.NavGraph
import com.github.radlance.autodispatch.uikit.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            NavGraph(rememberNavController())
        }
    }
}