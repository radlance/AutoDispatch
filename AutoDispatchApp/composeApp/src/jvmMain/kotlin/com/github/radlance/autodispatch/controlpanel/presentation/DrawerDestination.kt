package com.github.radlance.autodispatch.controlpanel.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.requests
import autodispatch.composeapp.generated.resources.vehicles
import autodispatch.composeapp.generated.resources.destinations
import autodispatch.composeapp.generated.resources.drivers
import autodispatch.composeapp.generated.resources.maintenance
import autodispatch.composeapp.generated.resources.notifications
import autodispatch.composeapp.generated.resources.refills
import autodispatch.composeapp.generated.resources.reports
import com.github.radlance.autodispatch.uikit.vector.DeliveryBoltIcon
import com.github.radlance.autodispatch.uikit.vector.DocumentIcon
import com.github.radlance.autodispatch.uikit.vector.FinanceIcon
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
object Destinations : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.CalendarToday
    override val titleRes: StringResource = Res.string.destinations
}

@Serializable
object Cars : DrawerDestination {
    override val icon: ImageVector = DeliveryBoltIcon
    override val titleRes: StringResource = Res.string.vehicles
}

@Serializable
object Drivers : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.Group
    override val titleRes: StringResource = Res.string.drivers
}

@Serializable
object Refills : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.LocalGasStation
    override val titleRes: StringResource = Res.string.refills
}

@Serializable
object Maintenance : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.Build
    override val titleRes: StringResource = Res.string.maintenance
}

@Serializable
object Reports : DrawerDestination {
    override val icon: ImageVector = FinanceIcon
    override val titleRes: StringResource = Res.string.reports
}

@Serializable
object Notifications : DrawerDestination {
    override val icon: ImageVector = Icons.Outlined.Notifications
    override val titleRes: StringResource = Res.string.notifications
}