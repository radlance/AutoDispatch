package com.github.radlance.autodispatch.home.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface HomeDestination {
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
    val label: String
}

@Serializable
object Requests : HomeDestination {

    override val selectedIcon: ImageVector = Icons.Filled.Home

    override val unselectedIcon: ImageVector = Icons.Outlined.Home

    override val label: String = "Доставки"
}

@Serializable
object History : HomeDestination {

    override val selectedIcon: ImageVector = Icons.Filled.History

    override val unselectedIcon: ImageVector = Icons.Outlined.History

    override val label: String = "История"
}

@Serializable
object Profile : HomeDestination {
    override val selectedIcon: ImageVector = Icons.Filled.Person

    override val unselectedIcon: ImageVector = Icons.Outlined.Person

    override val label: String = "Профиль"
}