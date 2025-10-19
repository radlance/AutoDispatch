package com.github.radlance.autodispatch.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.analytic_and_reports
import autodispatch.composeapp.generated.resources.app_management
import autodispatch.composeapp.generated.resources.assign_track_and_schedule_maintenance
import autodispatch.composeapp.generated.resources.create_view_and_manage_transport
import autodispatch.composeapp.generated.resources.fleet_control
import autodispatch.composeapp.generated.resources.get_reports_mileage_and_efficiency
import com.github.radlance.autodispatch.uikit.vector.DeliveryBoltIcon
import com.github.radlance.autodispatch.uikit.vector.FinanceIcon
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeaturesColumn(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        FeatureCard(
            title = stringResource(Res.string.app_management),
            subtitle = stringResource(Res.string.create_view_and_manage_transport),
            icon = Icons.Outlined.Person,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        FeatureCard(
            title = stringResource(Res.string.fleet_control),
            subtitle = stringResource(Res.string.assign_track_and_schedule_maintenance),
            icon = DeliveryBoltIcon,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        FeatureCard(
            title = stringResource(Res.string.analytic_and_reports),
            subtitle = stringResource(Res.string.get_reports_mileage_and_efficiency),
            icon = FinanceIcon,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconTint = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}