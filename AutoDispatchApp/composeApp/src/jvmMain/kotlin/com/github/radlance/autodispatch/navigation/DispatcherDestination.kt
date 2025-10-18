package com.github.radlance.autodispatch.navigation

import kotlinx.serialization.Serializable

interface DispatcherDestination

@Serializable
object SignIn : DispatcherDestination

@Serializable
object ControlPanel : DispatcherDestination