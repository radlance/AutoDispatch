package com.github.radlance.autodispatch

import androidx.compose.ui.window.ComposeUIViewController
import com.github.radlance.autodispatch.core.App

fun MainViewController() = ComposeUIViewController { App() }