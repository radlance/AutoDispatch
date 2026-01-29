package com.github.radlance.autodispatch.navigation.core

import kotlinx.serialization.Serializable

interface DriverDestination

@Serializable
object SignIn : DriverDestination

@Serializable
object Home : DriverDestination