package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.platform.MapPoint
import com.github.radlance.autodispatch.platform.createCameraPermissionController
import com.github.radlance.autodispatch.platform.getPlatformContext
import com.github.radlance.autodispatch.platform.openAppSettings
import com.github.radlance.autodispatch.platform.rememberCameraLauncher
import com.github.radlance.autodispatch.uikit.vector.GlobalLocationPinIcon

@Composable
fun DeliveryConfirmation(
    delivery: DeliveryDetailed,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }

    val controller = createCameraPermissionController {
        hasPermission = it
    }

    val images = remember { mutableStateListOf<ByteArray>() }
    val cameraLauncher = rememberCameraLauncher {
        it?.let { element -> images.add(element) }
    }
    val context = getPlatformContext()
    var selectedAddress by remember { mutableStateOf<String?>(null) }
    selectedAddress?.let {
        MapPoint(address = it, onDismiss = { selectedAddress = null })
    }
    if (hasPermission == false) {
        AlertDialog(
            onDismissRequest = { hasPermission = null },
            icon = {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Доступ к камере") },
            text = { Text("Разрешите доступ к камере для корректной работы приложения.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        openAppSettings(context)
                        hasPermission = null
                    }
                ) {
                    Text("Настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = { hasPermission = null }) { Text("Отмена") }
            }
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Вы на месте!",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 18.dp)) {
                    Text(
                        text = "Сделайте фото документа о доставке (накладная, товарно-транспортная накладная). Фотография должна быть чёткой и читаемой.",
                        fontSize = 14.sp
                    )
                }
            }
        }
        Card {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Точка разгрузки"
                        )
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = delivery.unloadingPoint.toStringAddress(),
                        fontSize = 14.sp
                    )

                    OutlinedButton(
                        onClick = { selectedAddress = delivery.unloadingPoint.toStringAddress() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                    ) {
                        Icon(imageVector = GlobalLocationPinIcon, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Открыть на карте")
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            images.forEach { image ->
                Image(
                    image.decodeToImageBitmap(),
                    null,
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }
            Button(
                onClick = {
                    if (controller.hasPermission()) {
                        cameraLauncher.capture()
                    } else {
                        controller.askPermission()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Outlined.AddAPhoto, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Сделать фото документа")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Фото будет сделано через камеру устройства",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}