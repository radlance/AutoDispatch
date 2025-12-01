package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.reuqest.core.domain.Cargo
import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.Customer
import com.github.radlance.autodispatch.reuqest.core.domain.Point
import com.github.radlance.autodispatch.reuqest.core.domain.RequestStatus
import com.github.radlance.autodispatch.reuqest.core.domain.VehicleFilter
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessUploadScreen(
    delivery: DeliveryDetailed,
    navigateToDeliveryList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Отправлено")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(100.dp).padding(12.dp)
                )
            }
            Text(
                text = "Документы отправлены",
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Фото документов отправлены диспетчеру на проверку. Ожидайте подтверждения.",
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier.padding(18.dp)
                ) {
                    InfoRow("Доставка") {
                        Text(text = delivery.requestNumber, fontSize = 12.sp)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    InfoRow("Маршрут") {
                        Text(
                            text = "${delivery.origin} → ${delivery.destination}",
                            fontSize = 12.sp
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    InfoRow("Груз") {
                        Text(text = delivery.cargo.weight.formatKg(), fontSize = 12.sp)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    InfoRow("Статус") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = "Нв проверке",
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.clip(CardDefaults.shape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(Modifier.size(24.dp)) {
                                Icon(
                                    Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Диспетчер проверит качество фотографий и подтвердит завершение доставки.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            Column {
                Button(onClick = navigateToDeliveryList, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = "Вернуться к списку")
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp)
        value()
    }
}


@Preview
@Composable
private fun SuccessDeliveryScreenPreview() {
    MaterialTheme {
        SuccessUploadScreen(
            delivery = DeliveryDetailed(
                id = 1,
                status = RequestStatus(
                    id = 4,
                    name = "Завершена"
                ),
                origin = "Москва",
                destination = "Калуга",
                transportationDescription = "",
                cargo = Cargo(
                    type = CargoType(
                        id = 1,
                        name = ""
                    ),
                    weight = 60.0,
                    volume = 0.0,
                    description = ""
                ),
                loadingPoint = Point(
                    address = "",
                    lat = 0.0,
                    lon = 0.0
                ),
                unloadingPoint = Point(
                    address = "",
                    lat = 0.0,
                    lon = 0.0
                ),
                dispatcherFullName = "",
                dispatcherPhoneNumber = "",
                customer = Customer(
                    id = 1,
                    organizationName = "",
                    email = "",
                    phoneNumber = ""
                ),
                vehicle = VehicleFilter(
                    id = 1,
                    model = "",
                    licensePlate = ""
                ),
                createdAt = LocalDateTime(1, 1, 1, 1, 1),
                updatedAt = LocalDateTime(1, 1, 1, 1, 1),
                requestNumber = "d123ge"
            ),
            navigateToDeliveryList = {}
        )
    }
}