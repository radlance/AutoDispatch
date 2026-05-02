package com.github.radlance.autodispatch.admin.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.extended_search
import autodispatch.composeapp.generated.resources.role
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.admin.core.domain.UserStatus
import com.github.radlance.autodispatch.common.domain.UserRole
import com.github.radlance.autodispatch.request.core.presentation.FilterDialogSelector
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserManagementFilters(
    selectedStatuses: List<String>,
    selectedRoles: List<String>,
    statuses: List<UserStatus>,
    roles: List<UserRole>,
    onStatusesChanged: (List<String>) -> Unit,
    onRolesChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(Res.string.extended_search),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterDialogSelector(
                    title = stringResource(Res.string.status),
                    options = statuses.map { it.title },
                    selected = selectedStatuses,
                    onSelectionChanged = onStatusesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.role),
                    options = roles.map { it.title },
                    selected = selectedRoles,
                    onSelectionChanged = onRolesChanged
                )
            }
        }
    }
}