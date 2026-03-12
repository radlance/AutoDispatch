package com.github.radlance.autodispatch.driver.core.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.DriverStatusWithColor
import com.github.radlance.autodispatch.common.presentation.ITEM_GAP
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.LabeledValue
import com.github.radlance.autodispatch.common.presentation.LoadableImage
import com.github.radlance.autodispatch.common.presentation.SECTION_GAP
import com.github.radlance.autodispatch.common.presentation.Section
import com.github.radlance.autodispatch.common.utils.avatarInitials
import com.github.radlance.autodispatch.common.utils.formatLicensePlate
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.driver.history.presentation.DriverHistoryDialog
import com.github.radlance.autodispatch.request.core.presentation.FullScreenImageDialog
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DriverDetailsSections(
    scrollState: ScrollState,
    driver: Driver,
    onShowVehicleAssignmentDialog: (reassign: Boolean) -> Unit,
    onShowDriverRequestAssignmentDialog: () -> Unit,
    onShowVehicleUnassignmentDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var showDriverHistoryDialog by remember { mutableStateOf(false) }
    var lastImageRetryAttempt by rememberSaveable { mutableStateOf(0L) }

    selectedImageUrl?.let {
        FullScreenImageDialog(
            onDismissRequest = { selectedImageUrl = null },
            selectedImageUrl = selectedImageUrl,
            onChangeImageIconClick = { selectedImageUrl = it }
        )
    }

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
            modifier = Modifier.align(Alignment.CenterHorizontally).size(100.dp)
                .clip(CircleShape)
                .background(CardDefaults.cardColors().containerColor)
        ) {
            driver.avatarUrl?.let { avatarUrl ->
                LoadableImage(
                    documentUrl = avatarUrl,
                    onRetry = { lastImageRetryAttempt = Clock.System.now().toEpochMilliseconds() },
                    lastRetryAttempt = lastImageRetryAttempt,
                    onImageSelected = { selectedImageUrl = it },
                    modifier = Modifier.fillMaxSize(),
                    showLoading = false
                )
            } ?: Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(text = avatarInitials(driver.fullName), fontSize = 24.sp)
            }
        }

        Text(
            text = driver.fullName,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = ITEM_GAP)
        )

        DriverStatusWithColor(status = driver.status, fontSize = 12.sp)
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
                LabeledValue(
                    label = "Гос. номер",
                    value = formatLicensePlate(vehicle.licensePlate, vehicle.regionCode)
                )
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(label = "Грузоподъёмность", value = "${vehicle.payloadCapacity} кг")
                Spacer(modifier = Modifier.height(ITEM_GAP))
                if (driver.deliveriesStats.activeCount + driver.deliveriesStats.onCheckCount + driver.deliveriesStats.rejectedCount == 0) {
                    Row {
                        OutlinedButton(
                            onClick = onShowVehicleUnassignmentDialog,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        ) {
                            Text(text = "Открепить")
                        }
                        OutlinedButton(
                            onClick = { onShowVehicleAssignmentDialog(true) },
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        ) {
                            Text(text = "Переназначить")
                        }
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
