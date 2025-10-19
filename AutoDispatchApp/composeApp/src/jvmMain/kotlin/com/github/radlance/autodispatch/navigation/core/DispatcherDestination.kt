package com.github.radlance.autodispatch.navigation.core

import kotlinx.serialization.Serializable

interface DispatcherDestination

@Serializable
data object Splash : DispatcherDestination

@Serializable
object SignIn : DispatcherDestination

@Serializable
object ControlPanel : DispatcherDestination