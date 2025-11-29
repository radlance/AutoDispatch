package com.github.radlance.autodispatch.delivery.confirmation.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DeliveryConfirmation(
    navigateUp: () -> Unit,
    delivery: DeliveryDetailed,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    viewModel: DeliveryConfirmationViewModel = koinViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val controller = createCameraPermissionController { hasPermission = it }

    val completeDeliveryState by viewModel.completeDeliveryState.collectAsStateWithLifecycle()
    val documents = viewModel.documents
    val cameraLauncher = rememberCameraLauncher { it?.let(documents::add) }

    val context = getPlatformContext()
    val lazyRowState = rememberLazyListState()

    var selectedAddress by remember { mutableStateOf<String?>(null) }
    selectedAddress?.let {
        MapPoint(address = it, onDismiss = { selectedAddress = null })
    }

    var fullscreenIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    val currentNavigateUp =
        if (fullscreenIndex != null) {
            { fullscreenIndex = null }
        } else navigateUp

    BackHandler {
        if (documents.isNotEmpty() && fullscreenIndex == null) {
            showBackDialog = true
        } else {
            currentNavigateUp()
        }
    }

    completeDeliveryState.Reduce(
        onSuccess = {
            viewModel.resetState()
            // TODO
        },
        onLoading = {
            Dialog(onDismissRequest = {}) {
                Box(
                    modifier = Modifier.clip(
                        RoundedCornerShape(18.dp)
                    ).background(AlertDialogDefaults.containerColor)
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                }
            }
        },
        onError = {
            AlertDialog(
                onDismissRequest = viewModel::resetState,
                icon = {
                    Icon(imageVector = Icons.Outlined.WarningAmber, contentDescription = null)
                },
                title = {
                    Text(text = "Ошибка")
                },
                text = {
                    Text(text = it)
                },
                dismissButton = {},
                confirmButton = {
                    TextButton(
                        onClick = viewModel::resetState
                    ) {
                        Text(text = "ОК")
                    }
                }
            )
        }
    )

    if (hasPermission == false) {
        AlertDialog(
            onDismissRequest = { hasPermission = null },
            icon = {
                Icon(
                    Icons.Default.PhotoCamera,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
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

    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            icon = {
                Icon(
                    Icons.Default.ErrorOutline,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Вернуться назад?") },
            text = { Text("У вас есть несохранённые фотографии (${documents.size}) шт. При возврате назад все фотографии будут удалены.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackDialog = false
                        navigateUp()
                    }
                ) { Text("Вернуться назад") }
            },
            dismissButton = {
                TextButton(onClick = { showBackDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            icon = {
                Icon(
                    Icons.Default.ErrorOutline,
                    null
                )
            },
            title = { Text(text = "Подтвердите отправку", textAlign = TextAlign.Center) },
            text = { Text(text = "После завершения доставки изменить данные будет невозможно. Вы уверены, что хотите отправить фото и завершить доставку?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        viewModel.completeDelivery(deliveryId = delivery.id, documents = documents)
                    }
                ) { Text(text = "Подтвердить") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text(text = "Отмена")
                }
            }
        )
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = fullscreenIndex,
            label = "basic_transition",
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        ) { targetIndex ->

            if (targetIndex == null) {
                Column(
                    modifier = modifier
                        .verticalScroll(scrollState)
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 24.dp),
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
                    Spacer(Modifier.height(24.dp))
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
                    if (documents.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                    }
                    AnimatedVisibility(
                        visible = documents.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        LazyRow(
                            state = lazyRowState,
                            contentPadding = PaddingValues(end = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            itemsIndexed(
                                items = documents,
                                key = { idx, _ -> idx }
                            ) { idx, image ->

                                val sharedKey = remember(image) { "image_${idx}" }
                                val sharedState = rememberSharedContentState(key = sharedKey)

                                Box(
                                    modifier = Modifier
                                        .animateItem()
                                        .width(80.dp)
                                        .fillMaxSize()
                                        .clickable { fullscreenIndex = idx }
                                ) {
                                    Image(
                                        bitmap = image.decodeToImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .sharedElement(
                                                sharedState,
                                                animatedVisibilityScope = this@AnimatedContent
                                            )
                                    )
                                    IconButton(
                                        onClick = { viewModel.documents.remove(image) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 8.dp, y = (-8).dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                                                    alpha = 0.8f
                                                ),
                                                shape = CircleShape
                                            )
                                            .size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(if (documents.isEmpty()) 24.dp else 8.dp))
                    when {
                        documents.isEmpty() -> {
                            Button(
                                onClick = {
                                    if (controller.hasPermission()) cameraLauncher.capture()
                                    else controller.askPermission()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Outlined.AddAPhoto, contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Text(text = "Сделать фото документа")
                            }
                        }

                        else -> {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        if (controller.hasPermission()) cameraLauncher.capture()
                                        else controller.askPermission()
                                    },
                                    enabled = documents.size < 10,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Outlined.AddAPhoto, contentDescription = null)
                                    Spacer(Modifier.width(12.dp))
                                    Text(text = "Добавить")
                                }
                                Spacer(Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        if (documents.isNotEmpty()) {
                                            showConfirmationDialog = true
                                        }
                                    }, modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.Send,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(text = "Отправить")
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Фото будет сделано через камеру устройства",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                }

            } else {
                if (targetIndex < documents.size) {
                    val sharedState = rememberSharedContentState(key = "image_$targetIndex")

                    val bmp = remember(documents, targetIndex) {
                        documents[targetIndex].decodeToImageBitmap()
                    }
                    val bmpPainter = remember(bmp) { BitmapPainter(bmp) }
                    val zoomState = rememberZoomState(contentSize = bmpPainter.intrinsicSize)

                    Box(Modifier.fillMaxSize()) {
                        Image(
                            painter = bmpPainter,
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
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
