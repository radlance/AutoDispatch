package com.github.radlance.autodispatch.driver.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.ITEM_GAP
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.LabeledValue
import com.github.radlance.autodispatch.common.presentation.SECTION_GAP
import com.github.radlance.autodispatch.common.presentation.Section
import com.github.radlance.autodispatch.driver.domain.Driver
import com.github.radlance.autodispatch.request.assignment.presentation.DriverStatusWithColor

// TODO сделать clickable или selectable данные в деталях
@Composable
fun DriverDetailsSections(
    scrollState: ScrollState,
    driver: Driver,
    modifier: Modifier = Modifier
) {
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

        Section(header = "Закреплённый автомобиль") {
            driver.vehicle?.let { vehicle ->
                LabeledValue(label = "Модель", value = vehicle.model)
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(label = "Гос. номер", value = vehicle.licensePlate)
                Spacer(modifier = Modifier.height(ITEM_GAP))
                LabeledValue(label = "Грузоподъёмность", value = "${vehicle.payloadCapacity} кг")
            } ?: Text(
                text = "Аввтомобиль не закреплён",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )
        Section(header = "Статистика") {
            LabeledValue(label = "Доставок выполнено", value = driver.deliveryCount.toString())

        }
    }
}