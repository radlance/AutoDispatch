package com.github.radlance.autodispatch.navigation.core

import kotlinx.serialization.Serializable

interface DispatcherDestination

@Serializable
object SignIn : DispatcherDestination

@Serializable
data class ControlPanel(
    val userRoleId: Int
) : DispatcherDestination