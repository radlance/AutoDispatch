package com.github.radlance.autodispatch.navigation.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.drivers
import autodispatch.composeapp.generated.resources.requests
import autodispatch.composeapp.generated.resources.settings
import autodispatch.composeapp.generated.resources.statistics
import autodispatch.composeapp.generated.resources.users
import autodispatch.composeapp.generated.resources.vehicles
import com.github.radlance.autodispatch.uikit.vector.DeliveryBoltIcon
import com.github.radlance.autodispatch.uikit.vector.DocumentIcon
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

interface DrawerDestination {
    val icon: ImageVector
    val titleRes: StringResource
}

@Serializable
object Requests : DrawerDestination {
    override val icon: ImageVector = DocumentIcon
    override val titleRes: StringResource = Res.string.requests
}

@Serializable
object Vehicles : DrawerDestination {
    override val icon: ImageVector = DeliveryBoltIcon
    override val titleRes: StringResource = Res.string.vehicles
}

@Serializable
object Drivers : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.Group
    override val titleRes: StringResource = Res.string.drivers
}

@Serializable
object Statistic : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.DataUsage
    override val titleRes: StringResource = Res.string.statistics
}

@Serializable
object Settings : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.Settings
    override val titleRes: StringResource = Res.string.settings
}

@Serializable
object Users : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.Person
    override val titleRes: StringResource = Res.string.users
}