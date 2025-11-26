package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
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
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
// TODO lazyRow + image scroll
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DeliveryConfirmation(
    navigateUp: () -> Unit,
    delivery: DeliveryDetailed,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val controller = createCameraPermissionController { hasPermission = it }

    val images = remember { mutableStateListOf<ByteArray>() }
    val cameraLauncher = rememberCameraLauncher { it?.let(images::add) }

    val context = getPlatformContext()

    var selectedAddress by remember { mutableStateOf<String?>(null) }
    selectedAddress?.let {
        MapPoint(address = it, onDismiss = { selectedAddress = null })
    }

    var fullscreenIndex by remember { mutableStateOf<Int?>(null) }
    var currentIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(fullscreenIndex) {
        if (fullscreenIndex != null) {
            currentIndex = fullscreenIndex
        }
    }

    val currentNavigateUp =
        if (fullscreenIndex != null) { { fullscreenIndex = null } }
        else navigateUp

    BackHandler(onBack = currentNavigateUp)

    if (hasPermission == false) {
        AlertDialog(
            onDismissRequest = { hasPermission = null },
            icon = {
                Icon(Icons.Default.PhotoCamera, null, tint = MaterialTheme.colorScheme.primary)
            },
            title = { Text("Доступ к камере") },
            text = { Text("Разрешите доступ к камере для корректной работы приложения.") },
            confirmButton = {
                TextButton(onClick = {
                    openAppSettings(context)
                    hasPermission = null
                }) { Text("Настройки") }
            },
            dismissButton = {
                TextButton(onClick = { hasPermission = null }) { Text("Отмена") }
            }
        )
    } else if (hasPermission == true) {
        LaunchedEffect(hasPermission) { cameraLauncher.capture() }
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = fullscreenIndex != null,
            label = "basic_transition"
        ) { isFullscreen ->

            if (!isFullscreen) {
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
                        Column(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(24.dp)) {
                                        Icon(
                                            Icons.Outlined.ErrorOutline,
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
                            Column(
                                modifier = Modifier.padding(horizontal = 18.dp)
                                    .padding(bottom = 18.dp)
                            ) {
                                Text(
                                    text = "Сделайте фото документа о доставке (накладная, ТТН). Фотография должна быть чёткой и читаемой.",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Card {
                        Column(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(24.dp)) {
                                        Icon(
                                            Icons.Outlined.LocationOn,
                                            contentDescription = null
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text("Точка разгрузки")
                                }
                            }
                            Column(Modifier.padding(horizontal = 18.dp)) {
                                Text(
                                    text = delivery.unloadingPoint.toStringAddress(),
                                    fontSize = 14.sp
                                )
                                OutlinedButton(
                                    onClick = {
                                        selectedAddress = delivery.unloadingPoint.toStringAddress()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 18.dp)
                                ) {
                                    Icon(GlobalLocationPinIcon, null)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Открыть на карте")
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            images.forEachIndexed { idx, image ->
                                val sharedState = rememberSharedContentState(key = "image_$idx")

                                Image(
                                    bitmap = image.decodeToImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .width(100.dp)
                                        .clickable { fullscreenIndex = idx }
                                        .sharedElement(
                                            sharedState,
                                            animatedVisibilityScope = this@AnimatedContent
                                        )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (controller.hasPermission()) cameraLauncher.capture()
                                else controller.askPermission()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.AddAPhoto, null)
                            Spacer(Modifier.width(12.dp))
                            Text("Сделать фото документа")
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

            } else {
                val index = currentIndex ?: return@AnimatedContent
                val sharedState = rememberSharedContentState(key = "image_$index")

                val bmp = remember(images, index) {
                    BitmapPainter(images[index].decodeToImageBitmap())
                }
                val zoomState = rememberZoomState(contentSize = bmp.intrinsicSize)

                Box(Modifier.fillMaxSize()) {
                    Image(
                        painter = bmp,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(zoomState)
                            .sharedElement(
                                sharedState,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                    )

                    IconButton(
                        onClick = { fullscreenIndex = null },
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }
        }
    }
}
