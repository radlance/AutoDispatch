package com.github.radlance.autodispatch.delivery.route.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.common.utils.toSimpleDateWithTimeString
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.delivery.route.domain.DeliveryRouteAction
import com.github.radlance.autodispatch.platform.MapRouteDialog
import com.github.radlance.autodispatch.platform.getPlatformContext
import com.github.radlance.autodispatch.platform.openAppSettings
import com.github.radlance.autodispatch.platform.openDialer
import com.github.radlance.autodispatch.platform.rememberLocationPermissionController
import com.github.radlance.autodispatch.request.core.domain.Cargo
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.domain.Point
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.Package2Icon
import com.github.radlance.autodispatch.uikit.vector.VitalSignsIcon
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun DeliveryRoute(
    scrollState: ScrollState,
    delivery: DeliveryDetailed,
    navigateToDeliveryConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    deliveryViewModel: DeliveryDetailsViewModel,
    viewModel: DeliveryRouteViewModel = koinViewModel()
) {
    var selectedPoint by remember { mutableStateOf<Point?>(null) }
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val controller = rememberLocationPermissionController {
        hasPermission = it
    }
    val context = getPlatformContext()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val routeActionState by deliveryViewModel.routeActionState.collectAsStateWithLifecycle()
    val routeActionType by deliveryViewModel.routeActionType.collectAsStateWithLifecycle()

    val progressSteps = remember(delivery) {
        listOf(
            "Прибыл на погрузку" to delivery.arrivedLoadingAt,
            "Отправился с погрузки" to delivery.actualLoadingAt,
            "Прибыл на разгрузку" to delivery.arrivedUnloadingAt,
            "Документы отправлены" to delivery.actualUnloadingAt
        ).map { (label, time) ->
            ProgressStep(
                label = label,
                time = time?.toSimpleDateWithTimeString() ?: "",
                isCompleted = time != null
            )
        }
    }

    val progressValue = remember(progressSteps) {
        progressSteps.count { it.isCompleted } / 4f
    }

    val action = remember(delivery) {
        when {
            delivery.arrivedLoadingAt == null -> RouteAction.ArriveLoading
            delivery.actualLoadingAt == null -> RouteAction.DepartLoading
            delivery.arrivedUnloadingAt == null -> RouteAction.ArriveUnloading
            else -> RouteAction.UploadDocuments
        }
    }

    val buttonText = when (action) {
        RouteAction.ArriveLoading -> "Прибыл на погрузку"
        RouteAction.DepartLoading -> "Отправился с погрузки"
        RouteAction.ArriveUnloading -> "Прибыл на разгрузку"
        RouteAction.UploadDocuments -> "Загрузить документы"
    }

    val isActionLoading = routeActionState is FetchResultUiState.Loading
    val targetPoint = when (action) {
        RouteAction.ArriveLoading, RouteAction.DepartLoading -> delivery.loadingPoint
        RouteAction.ArriveUnloading, RouteAction.UploadDocuments -> delivery.unloadingPoint
    }

    val uiState = remember(currentLocation, targetPoint, action) {
        derivedStateOf {
            if (action == RouteAction.UploadDocuments) {
                return@derivedStateOf ArriveUi(
                    enabled = true,
                    text = buttonText
                )
            }

            if (currentLocation == null) {
                return@derivedStateOf ArriveUi(
                    enabled = false,
                    text = "Получите геолокацию"
                )
            }

            val distance = distanceMeters(
                currentLocation!!.lat, currentLocation!!.lon,
                targetPoint.lat, targetPoint.lon
            )

            if (distance > 300) {
                // TODO test data
//                ArriveUi(
//                    enabled = false,
//                    text = "Подъедьте ближе (${formatDistance(distance)})"
//                )
                ArriveUi(
                    enabled = true,
                    text = buttonText
                )
            } else {
                ArriveUi(
                    enabled = true,
                    text = buttonText
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (controller.hasPermission()) {
            hasPermission = true
        }
    }

    if (hasPermission == false) {
        AlertDialog(
            onDismissRequest = { hasPermission = null },
            icon = {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Доступ к локации") },
            text = {
                Text(
                    "Разрешите доступ к геолокации для корректной работы приложения.",
                    textAlign = TextAlign.Center
                )
            },
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
    } else if (hasPermission == true) {
        LaunchedEffect(hasPermission) {
            viewModel.fetchCurrentLocation()
        }
    }

    if (routeActionState is FetchResultUiState.Error<RequestError>) {
        val error = (routeActionState as FetchResultUiState.Error<RequestError>).error
        AlertDialog(
            onDismissRequest = deliveryViewModel::resetRouteActionState,
            icon = {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Ошибка") },
            text = { Text(error.message) },
            confirmButton = {
                TextButton(onClick = deliveryViewModel::resetRouteActionState) {
                    Text("ОК")
                }
            },
            dismissButton = {}
        )
    }

    LaunchedEffect(routeActionState, routeActionType) {
        if (routeActionState is FetchResultUiState.Success && routeActionType != null) {
            val actionType = routeActionType
            deliveryViewModel.resetRouteActionState()

            if (actionType == DeliveryRouteAction.ArriveUnloading) {
                navigateToDeliveryConfirmation()
            }
        }
    }

    selectedPoint?.let {
        MapRouteDialog(
            lat = it.lat,
            lon = it.lon,
            onDismiss = { selectedPoint = null }
        )
    }
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        RoutePoints(
            openRoute = { selectedPoint = it },
            loadingPoint = delivery.loadingPoint,
            unloadingPoint = delivery.unloadingPoint,
            showLoadingPoint = action == RouteAction.ArriveLoading || action == RouteAction.DepartLoading,
            showUnloadingPoint = action == RouteAction.ArriveUnloading || action == RouteAction.UploadDocuments,
            progress = progressValue,
            progressSteps = progressSteps
        )
        CargoCard(cargo = delivery.cargo)
        CustomerCard(customer = delivery.customer)
        ActionButtons(
            onRefreshLocationClick = {
                if (controller.hasPermission()) {
                    viewModel.fetchCurrentLocation()
                } else controller.askPermission()
            },
            onArrivedPlaceClick = {
                when (action) {
                    RouteAction.ArriveLoading -> deliveryViewModel.arriveLoading(delivery.id)
                    RouteAction.DepartLoading -> deliveryViewModel.departLoading(delivery.id)
                    RouteAction.ArriveUnloading -> deliveryViewModel.arriveUnloading(delivery.id)
                    RouteAction.UploadDocuments -> navigateToDeliveryConfirmation()
                }
            },
            arrivedButtonEnabled = uiState.value.enabled && !isActionLoading,
            buttonText = uiState.value.text
        )
    }
}

@Composable
private fun RoutePoints(
    openRoute: (address: Point) -> Unit,
    loadingPoint: Point,
    unloadingPoint: Point,
    showLoadingPoint: Boolean,
    showUnloadingPoint: Boolean,
    progress: Float,
    progressSteps: List<ProgressStep>
) {
    var isExpandedLoadingPoint by rememberSaveable { mutableStateOf(false) }
    var isOverflowLoadingPoint by rememberSaveable { mutableStateOf(false) }

    var isExpandedUnloadingPoint by rememberSaveable { mutableStateOf(false) }
    var isOverflowUnloadingPoint by rememberSaveable { mutableStateOf(false) }

    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.then(
                    if (isOverflowLoadingPoint) {
                        Modifier.clickable {
                            isExpandedLoadingPoint = !isExpandedLoadingPoint
                        }
                    } else Modifier
                ).padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = VitalSignsIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Прогресс доставки"
                    )
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                ProgressWithAnimationByButton(
                    progress = progress,
                    leftLabel = "загрузка",
                    rightLabel = "разгрузка",
                    steps = progressSteps
                )
            }
        }
    }

    if (showLoadingPoint) {
        Card {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.then(
                        if (isOverflowLoadingPoint) {
                            Modifier.clickable {
                                isExpandedLoadingPoint = !isExpandedLoadingPoint
                            }
                        } else Modifier
                    ).padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Точка погрузки"
                        )
                        Spacer(Modifier.weight(1f))
                        if (isOverflowLoadingPoint) {
                            IconButton(
                                onClick = { isExpandedLoadingPoint = !isExpandedLoadingPoint },
                                modifier = Modifier.size(24.dp)
                            ) {
                                val icon = if (isExpandedLoadingPoint) {
                                    Icons.Default.ExpandLess
                                } else Icons.Default.ExpandMore
                                Icon(imageVector = icon, contentDescription = null)
                            }
                        }
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = loadingPoint.toStringAddress(),
                        fontSize = 14.sp,
                        maxLines = if (isExpandedLoadingPoint) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            if (result.didOverflowHeight) {
                                isOverflowLoadingPoint = true
                            }
                        },
                        modifier = Modifier.animateContentSize()
                    )

                    Button(
                        onClick = { openRoute(loadingPoint) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.NearMe, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Проложить маршрут")
                    }
                }
            }
        }
    }

    if (showUnloadingPoint) {
        Card {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.then(
                        if (isOverflowUnloadingPoint) {
                            Modifier.clickable {
                                isExpandedUnloadingPoint = !isExpandedUnloadingPoint
                            }
                        } else Modifier
                    ).padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Точка разгрузки"
                        )
                        Spacer(Modifier.weight(1f))
                        if (isOverflowUnloadingPoint) {
                            IconButton(
                                onClick = { isExpandedUnloadingPoint = !isExpandedUnloadingPoint },
                                modifier = Modifier.size(24.dp)
                            ) {
                                val icon = if (isExpandedUnloadingPoint) {
                                    Icons.Default.ExpandLess
                                } else Icons.Default.ExpandMore
                                Icon(imageVector = icon, contentDescription = null)
                            }
                        }
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = unloadingPoint.toStringAddress(),
                        fontSize = 14.sp,
                        maxLines = if (isExpandedUnloadingPoint) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            if (result.didOverflowHeight) {
                                isOverflowUnloadingPoint = true
                            }
                        },
                        modifier = Modifier.animateContentSize()
                    )

                    Button(
                        onClick = { openRoute(unloadingPoint) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.NearMe, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Проложить маршрут")
                    }
                }
            }
        }
    }
}

@Composable
private fun CargoCard(
    cargo: Cargo,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(
                    imageVector = Package2Icon,
                    contentDescription = null
                )
                Spacer(Modifier.width(12.dp))
                Text(text = "Информация о грузе")
            }
            Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp)) {
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
            }
        }
    }
}

@Composable
private fun CustomerCard(customer: Customer, modifier: Modifier = Modifier) {
    val context = getPlatformContext()
    Card {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(18.dp)) {
            Column {
                Text(
                    text = "Заказчик",
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Text(text = customer.organizationName)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { openDialer(customer.phoneNumber, context) }) {
                Icon(imageVector = Icons.Outlined.Call, contentDescription = null)
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onRefreshLocationClick: () -> Unit,
    onArrivedPlaceClick: () -> Unit,
    arrivedButtonEnabled: Boolean,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedButton(onClick = onRefreshLocationClick, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Outlined.NearMe, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text = "Обновить геологацию")
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onArrivedPlaceClick,
            enabled = arrivedButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text = buttonText)
        }
    }
}

private sealed interface RouteAction {
    data object ArriveLoading : RouteAction
    data object DepartLoading : RouteAction
    data object ArriveUnloading : RouteAction
    data object UploadDocuments : RouteAction
}

fun distanceMeters(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val r = 6371000.0
    fun Double.toRad() = this * PI / 180.0

    val dLat = (lat2 - lat1).toRad()
    val dLon = (lon2 - lon1).toRad()

    val a = sin(dLat / 2).pow(2.0) +
            cos(lat1.toRad()) * cos(lat2.toRad()) *
            sin(dLon / 2).pow(2.0)

    return 2.0 * r * asin(sqrt(a))
}

fun formatDistance(meters: Double): String {
    return if (meters >= 1000) {
        val km = (meters / 1000 * 10).toInt() / 10.0
        "$km км"
    } else {
        "${meters.toInt()} м"
    }
}