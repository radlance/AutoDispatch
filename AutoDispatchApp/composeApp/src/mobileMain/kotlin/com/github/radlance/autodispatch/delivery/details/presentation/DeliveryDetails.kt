package com.github.radlance.autodispatch.delivery.details.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import coil3.size.Size
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.LoadableImage
import com.github.radlance.autodispatch.common.presentation.SectionHeader
import com.github.radlance.autodispatch.common.presentation.WarningCard
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.common.utils.toSimpleDateWithTimeString
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryRoute
import com.github.radlance.autodispatch.delivery.core.presentation.deliveryStatusColors
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.platform.getPlatformContext
import com.github.radlance.autodispatch.platform.openDialer
import com.github.radlance.autodispatch.request.core.domain.Cargo
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import com.github.radlance.autodispatch.uikit.vector.AppIcon
import com.github.radlance.autodispatch.uikit.vector.ConversionPathIcon
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.Package2Icon
import kotlinx.datetime.LocalDateTime
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTime::class
)
@Composable
fun DeliveryDetails(
    scrollState: ScrollState,
    delivery: DeliveryDetailed,
    onContinueDeliveryClick: () -> Unit,
    onRetakeDocumentsClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onCloseError: () -> Unit,
    navigateUp: () -> Unit,
    fetchDeliveryDetails: () -> Unit,
    acceptDeliveryState: FetchResultUiState<Unit, RequestError>,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = deliveryStatusColors(delivery.status)
    val context = getPlatformContext()
    val coilContext = LocalPlatformContext.current
    val lazyRowState = rememberLazyListState()
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var fullscreenIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var lastImageRetryAttempt by remember { mutableStateOf(0L) }

    val isLoading = acceptDeliveryState is FetchResultUiState.Loading
    val error = (acceptDeliveryState as? FetchResultUiState.Error)?.error

    BackHandler {
        val currentNavigateUp =
            if (fullscreenIndex != null) {
                { fullscreenIndex = null }
            } else navigateUp

        currentNavigateUp()
    }

    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier.clip(
                    RoundedCornerShape(18.dp)
                ).background(AlertDialogDefaults.containerColor)
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp), color = contentColor)
            }
        }
    }

    error?.let {
        val onDismiss: () -> Unit = {
            when (it) {
                is RequestError.BaseError -> {
                    onCloseError()
                }

                is RequestError.DeliveryCanceledError -> {
                    onCloseError()
                    fetchDeliveryDetails()
                }

                is RequestError.DriverBusyError -> {
                    onCloseError()
                }

                is RequestError.GenericStateError -> {
                    onCloseError()
                    fetchDeliveryDetails()
                }

                is RequestError.InternalError -> {
                    onCloseError()
                    navigateUp()
                }
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(imageVector = Icons.Outlined.WarningAmber, contentDescription = null)
            },
            title = {
                Text(text = "Ошибка")
            },
            text = {
                Text(text = it.message)
            },
            dismissButton = {},
            confirmButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(text = "ОК", color = contentColor)
                }
            }
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
            },
            title = {
                Text(
                    text = "Подтвердить действие"
                )
            },
            text = {
                Text(text = "Проверьте информацию о доставке перед подтверждением")
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
                ) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onAcceptClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = contentColor,
                        containerColor = backgroundColor
                    )
                ) {
                    Text(text = "Подтвердить")
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
                val customTextSelectionColors = TextSelectionColors(
                    handleColor = contentColor,
                    backgroundColor = backgroundColor.copy(alpha = 0.5f)
                )
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    Column(
                        modifier = modifier
                            .verticalScroll(scrollState)
                            .padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        StatusCard(
                            status = delivery.status.title,
                            createdAt = delivery.createdAt.toSimpleDateWithTimeString(),
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        RouteCard(
                            fromPoint = delivery.loadingPoint.toStringAddress(),
                            toPoint = delivery.unloadingPoint.toStringAddress(),
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        DeadlinesCard(
                            plannedLoadingAt = delivery.plannedLoadingAt,
                            plannedUnloadingAt = delivery.plannedUnloadingAt,
                            actualLoadingAt = delivery.actualLoadingAt,
                            actualUnloadingAt = delivery.actualUnloadingAt,
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        CargoCard(
                            cargo = delivery.cargo,
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        VehicleCard(
                            vehicle = delivery.vehicle,
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ContactsCard(
                            customer = delivery.customer,
                            dispatcherFullName = delivery.dispatcherFullName,
                            dispatcherPhoneNumber = delivery.dispatcherPhoneNumber,
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        delivery.transportationDescription?.let {
                            AdditionalInfoCard(
                                description = it,
                                backgroundColor = backgroundColor,
                                contentColor = contentColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (delivery.status.id == 6 || delivery.status.id == 7) {
                            LazyRow(
                                state = lazyRowState,
                                contentPadding = PaddingValues(end = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                itemsIndexed(
                                    items = delivery.documents.map { it.imageUrl },
                                    key = { idx, _ -> idx }
                                ) { idx, image ->

                                    val sharedKey = remember(image) { "image_${idx}" }
                                    val sharedState = rememberSharedContentState(key = sharedKey)


                                    LoadableImage(
                                        documentUrl = image,
                                        onRetry = {
                                            lastImageRetryAttempt =
                                                Clock.System.now().toEpochMilliseconds()
                                        },
                                        lastRetryAttempt = lastImageRetryAttempt,
                                        onImageSelected = { fullscreenIndex = idx },
                                        showLoading = false,
                                        modifier = Modifier
                                            .sharedElement(
                                                sharedState,
                                                animatedVisibilityScope = this@AnimatedContent
                                            ),
                                    )

                                }
                            }
                        }
                        if (delivery.status.id == 6) {
                            val sentAt = delivery.updatedAt
                            WarningCard(
                                modifier = modifier,
                                icon = Icons.Outlined.ErrorOutline,
                                contentColor = contentColor,
                                containerColor = backgroundColor,
                                message = "Отправлено: ${sentAt.toSimpleDateWithTimeString()}\nДиспетчер проверяет загруженные документы. Ожидайте подтверждения.",
                            )
                        }

                        if (delivery.status.id == 7) {
                            val lastSent = delivery.documents.maxOf { it.uploadedAt }
                            val updatedAt = delivery.updatedAt

                            DocumentRejectCard(
                                rejectionReason = delivery.rejectionReason ?: "",
                                sentAt = lastSent.toSimpleDateWithTimeString(),
                                rejectedAt = updatedAt.toSimpleDateWithTimeString(),
                                backgroundColor = backgroundColor,
                                contentColor = contentColor
                            )
                        }

                        ActionButtons(
                            deliveryStatusId = delivery.status.id,
                            onContinueDeliveryClick = onContinueDeliveryClick,
                            onRetakeDocumentsClick = onRetakeDocumentsClick,
                            onAcceptClick = { showConfirmDialog = true },
                            onContactClick = {
                                openDialer(delivery.dispatcherPhoneNumber, context)
                            },
                            backgroundColor = backgroundColor,
                            contentColor = contentColor,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )
                    }
                }
            } else {
                if (targetIndex < delivery.documents.size) {
                    val sharedState = rememberSharedContentState(key = "image_$targetIndex")
                    val imageUrl = remember(delivery.documents, targetIndex) {
                        delivery.documents[targetIndex].imageUrl
                    }
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(coilContext)
                            .data(imageUrl)
                            .size(Size.ORIGINAL)
                            .scale(Scale.FILL)
                            .crossfade(true)
                            .build()
                    )
                    val zoomState = rememberZoomState(contentSize = painter.intrinsicSize)
                    Box(Modifier.fillMaxSize()) {
                        Image(
                            painter = painter,
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

@Composable
private fun StatusCard(
    status: String,
    createdAt: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "Статус доставки", fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .border(
                        width = 2.dp,
                        color = contentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = status,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
            InfoRow(
                title = createdAt,
                subtitle = "Дата и время создания",
                icon = Icons.Outlined.CalendarMonth,
                iconTint = contentColor,
                iconBackgroundColor = backgroundColor,
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}

@Composable
private fun RouteCard(
    fromPoint: String,
    toPoint: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Маршрут доставки",
                icon = Icons.Outlined.LocationOn,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            DeliveryRoute(
                fromPoint = fromPoint,
                toPoint = toPoint,
                color = contentColor,
                showOpenMapButton = true,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}

@Composable
private fun DeadlinesCard(
    plannedLoadingAt: LocalDateTime?,
    plannedUnloadingAt: LocalDateTime?,
    actualLoadingAt: LocalDateTime?,
    actualUnloadingAt: LocalDateTime?,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Сроки доставки",
                icon = Icons.Outlined.CalendarMonth,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            Column(modifier = Modifier.padding(18.dp)) {
                InfoRow(
                    title = plannedLoadingAt.asDeadlineText(),
                    subtitle = "Плановая загрузка",
                    icon = Icons.Outlined.CalendarMonth,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
                InfoRow(
                    title = actualLoadingAt.asDeadlineText(),
                    subtitle = "Фактическая загрузка",
                    icon = Icons.Outlined.CalendarMonth,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
                InfoRow(
                    title = plannedUnloadingAt.asDeadlineText(),
                    subtitle = "Плановая разгрузка",
                    icon = Icons.Outlined.CalendarMonth,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
                InfoRow(
                    title = actualUnloadingAt.asDeadlineText(),
                    subtitle = "Фактическая разгрузка",
                    icon = Icons.Outlined.CalendarMonth,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor
                )
            }
        }
    }
}

@Composable
private fun CargoCard(
    cargo: Cargo,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Информация о грузе",
                icon = Package2Icon,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Тип груза",
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Icon(imageVector = DeployedCodeIcon, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(text = cargo.type.name)
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Вес", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                            Text(text = cargo.weight.formatKg(), fontSize = 20.sp)
                        }
                    }
                    cargo.volume?.let { volume ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Объём",
                                    fontSize = 12.sp,
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Text(text = volume.formatM3(), fontSize = 20.sp)
                            }
                        }
                    }
                }
                cargo.description?.let { description ->
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
                    Text(text = "Описание груза", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                    Spacer(Modifier.height(4.dp))
                    Text(text = description, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Транспорт",
                icon = AppIcon,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            SelectionContainer {
                InfoRow(
                    title = vehicle.model,
                    subtitle = "Гос. номер: ${vehicle.licensePlate}",
                    icon = Icons.Outlined.LocationOn,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor,
                    modifier = Modifier.padding(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ContactsCard(
    customer: Customer,
    dispatcherFullName: String,
    dispatcherPhoneNumber: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {

    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Контакты",
                icon = Icons.Outlined.Person,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "Заказчик", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                Spacer(Modifier.height(8.dp))
                SelectionContainer {
                    InfoRow(
                        title = customer.organizationName,
                        subtitle = customer.phoneNumber,
                        icon = Icons.Outlined.Person,
                        iconTint = contentColor,
                        iconBackgroundColor = backgroundColor
                    )
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
                Text(text = "Диспетчер", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                Spacer(Modifier.height(8.dp))
                SelectionContainer {
                    InfoRow(
                        title = dispatcherFullName,
                        subtitle = dispatcherPhoneNumber,
                        icon = Icons.Outlined.Person,
                        iconTint = contentColor,
                        iconBackgroundColor = backgroundColor
                    )
                }
            }
        }
    }
}

@Composable
private fun AdditionalInfoCard(
    description: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {

    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Дополнительная информация",
                icon = Icons.AutoMirrored.Outlined.StickyNote2,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = description,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    deliveryStatusId: Int,
    onContinueDeliveryClick: () -> Unit,
    onRetakeDocumentsClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onContactClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (deliveryStatusId == 2) {
            Button(
                onClick = onAcceptClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Icon(imageVector = ConversionPathIcon, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Начать рейс")
            }
            Spacer(Modifier.height(4.dp))
        }
        if (deliveryStatusId == 3) {
            Button(
                onClick = onContinueDeliveryClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Icon(imageVector = Icons.Outlined.NearMe, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Продолжить доставку")
            }
            Spacer(Modifier.height(4.dp))
        }
        if (deliveryStatusId == 7) {
            Button(
                onClick = onRetakeDocumentsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Icon(imageVector = Icons.Outlined.AddAPhoto, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Переснять докумены")
            }
            Spacer(Modifier.height(4.dp))
        }

        OutlinedButton(
            onClick = onContactClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Outlined.Phone, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text = "Связаться с диспетчером")
        }
    }
}

@Composable
private fun DocumentRejectCard(
    rejectionReason: String,
    sentAt: String,
    rejectedAt: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column {
        WarningCard(
            modifier = modifier,
            icon = Icons.Outlined.ErrorOutline,
            contentColor = contentColor,
            containerColor = backgroundColor,
            message = rejectionReason,
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                    Text(text = "Отправлено", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                    Text(text = sentAt, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                    Text(text = "Отклонено", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                    Text(text = rejectedAt, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun LocalDateTime?.asDeadlineText(): String = this?.toSimpleDateWithTimeString() ?: "—"
