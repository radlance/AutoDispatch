package com.github.radlance.autodispatch.driver.core.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.ITEM_GAP
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.LabeledValue
import com.github.radlance.autodispatch.common.presentation.SECTION_GAP
import com.github.radlance.autodispatch.common.presentation.Section
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.driver.history.presentation.DriverHistoryDialog

@Composable
fun DriverDetailsSections(
    scrollState: ScrollState,
    driver: Driver,
    onShowVehicleAssignmentDialog: (reassign: Boolean) -> Unit,
    onShowDriverRequestAssignmentDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDriverHistoryDialog by remember { mutableStateOf(false) }

    if (showDriverHistoryDialog) {
        DriverHistoryDialog(
            driver = driver,
            onDismiss = { showDriverHistoryDialog = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(SECTION_GAP))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        Text(
            text = driver.fullName,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = ITEM_GAP)
        )

        DriverStatusWithColor(status = driver.status.name, fontSize = 12.sp)
        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )
        Section(header = "Контакты") {
            InfoRow(
                icon = Icons.Outlined.Call,
                iconDesc = null,
                text = driver.phoneNumber
            )
        }
        Spacer(modifier = Modifier.height(SECTION_GAP))

        Section(header = "Автомобиль") {
            driver.vehicle?.let { vehicle ->
                LabeledValue(label = "Модель", value = vehicle.model)
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(label = "Гос. номер", value = vehicle.licensePlate)
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(label = "Грузоподъёмность", value = "${vehicle.payloadCapacity} кг")
                Spacer(modifier = Modifier.height(ITEM_GAP))
                if (driver.deliveriesStats.activeCount + driver.deliveriesStats.onCheckCount + driver.deliveriesStats.rejectedCount == 0) {
                    OutlinedButton(
                        onClick = { onShowVehicleAssignmentDialog(true) },
                        modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
                    ) {
                        Text(text = "Переназначить автомобиль")
                    }
                } else {
                    Text(
                        text = "Переназначение автомобиля недоступно, пока у водителя есть незавершенные заявки (активные, на проверке, отклонённые)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            } ?: run {
                Text(
                    text = "Аввтомобиль не закреплён",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(ITEM_GAP))
                OutlinedButton(
                    onClick = { onShowVehicleAssignmentDialog(false) },
                    modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
                ) {
                    Text(text = "Закрепить автомобиль")
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )
        Section(header = "Статистика доставок") {
            val stats = driver.deliveriesStats

            StatsRow("Всего", stats.totalCount)
            StatsRow("Активные", stats.activeCount)
            StatsRow("На проверке", stats.onCheckCount)
            StatsRow("Завершённые", stats.completedCount)
            StatsRow("Отменённые", stats.canceledCount)
            StatsRow("Отклонённые", stats.rejectedCount)
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )
        OutlinedButton(
            onClick = { showDriverHistoryDialog = true },
            modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
        ) {
            Icon(imageVector = Icons.Outlined.History, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text = "История доставок")
        }
        driver.vehicle?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onShowDriverRequestAssignmentDialog,
                modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
            ) {
                Text(text = "Назначить рейс")
            }
        }
    }

}

@Composable
fun StatsRow(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(end = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(modifier = Modifier.height(ITEM_GAP))
}


@Composable
fun DriverStatusWithColor(status: String?, fontSize: TextUnit = TextUnit.Unspecified) {
    val (bgColor, textColor) = when (status) {

        "Свободен" ->
            MaterialTheme.colorScheme.primaryContainer to
                    MaterialTheme.colorScheme.onPrimaryContainer

        "В рейсе" ->
            MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer

        "Не на смене" ->
            MaterialTheme.colorScheme.surfaceVariant to
                    MaterialTheme.colorScheme.onSurfaceVariant

        else ->
            MaterialTheme.colorScheme.surface to
                    MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = status ?: "-",
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
