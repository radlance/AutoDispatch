package com.github.radlance.autodispatch.navigation.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.radlance.autodispatch.platform.Destination
import kotlinx.serialization.Serializable

interface HomeDestination

interface BottomDestination : HomeDestination {
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
    val label: String
}

@Destination
@Serializable
object Deliveries : BottomDestination {

    override val selectedIcon: ImageVector = Icons.Filled.Home

    override val unselectedIcon: ImageVector = Icons.Outlined.Home

    override val label: String = "Доставки"
}

@Destination
@Serializable
object History : BottomDestination {

    override val selectedIcon: ImageVector = Icons.Filled.History

    override val unselectedIcon: ImageVector = Icons.Outlined.History

    override val label: String = "История"
}

@Destination
@Serializable
object Profile : BottomDestination {

    override val selectedIcon: ImageVector = Icons.Filled.Person

    override val unselectedIcon: ImageVector = Icons.Outlined.Person

    override val label: String = "Профиль"
}

@Destination
@Serializable
object DeliveryList : HomeDestination

@Destination
@Serializable
data class DeliveryDetails(
    val deliveryId: Int,
    val deliveryNumber: String
) : HomeDestination

@Destination
@Serializable
data class DeliveryRoute(
    val deliveryId: Int,
    val deliveryNumber: String
) : HomeDestination

@Destination
@Serializable
data class DeliveryConfirmation(val deliveryId: Int) : HomeDestination

@Destination
@Serializable
data class SuccessUpload(val deliveryDetailedJson: String) : HomeDestination
