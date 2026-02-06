package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.radlance.autodispatch.request.change.domain.PointDetailed
import com.github.radlance.autodispatch.request.change.domain.PointValidationError
import com.github.radlance.autodispatch.request.core.domain.Point
import com.github.radlance.autodispatch.uikit.vector.GlobalLocationPinIcon
import kotlinx.serialization.json.jsonPrimitive
import org.koin.compose.viewmodel.koinViewModel
import org.openstreetmap.gui.jmapviewer.Coordinate

@Composable
fun PointSelectionDialog(
    selectedCityName: String,
    onDismissRequest: () -> Unit,
    onConfirm: (Point) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PointSelectionViewModel = koinViewModel()
) {
    val fetchPointState by viewModel.fetchPointState.collectAsState()
    val points by viewModel.points.collectAsState()
    val pointValidationState by viewModel.validationState.collectAsState()

    var placeSuggestionFieldValue by rememberSaveable { mutableStateOf("") }

    var searchResult by remember { mutableStateOf<PointDetailed?>(null) }
    var markerLocation by remember { mutableStateOf<Coordinate?>(null) }
    var resultPoint by remember { mutableStateOf<Point?>(null) }
    var showMapView by remember { mutableStateOf(true) }

    val finalSelectedCoordinate: Coordinate? = remember(markerLocation, searchResult) {
        if (markerLocation != null) {
            markerLocation
        } else if (searchResult != null) {
            val box = searchResult!!.boundingBox
            val lat = (box[0].toDouble() + box[1].toDouble()) / 2.0
            val lon = (box[2].toDouble() + box[3].toDouble()) / 2.0
            Coordinate(lat, lon)
        } else {
            null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCoords(selectedCityName)
    }

    pointValidationState.Reduce(
        onLoading = {
            showMapView = false
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
        onSuccess = {
            showMapView = true
            viewModel.resetValidationState()
            onConfirm(resultPoint!!)
            onDismissRequest()
        },
        onError = { validationError ->
            AlertDialog(
                onDismissRequest = viewModel::resetValidationState,
                icon = {
                    Icon(imageVector = Icons.Outlined.WarningAmber, contentDescription = null)
                },
                title = {
                    Text(text = "Ошибка")
                },
                text = {
                    when (validationError) {
                        PointValidationError.Network ->
                            Text("Проверьте интернет-соединение")

                        PointValidationError.CityNotResolved ->
                            Text("Не удалось определить город по точке")

                        is PointValidationError.PointOutsideCity ->
                            Text(
                                "Точка находится вне выбранного города «${validationError.expectedCity}»"
                            )
                    }
                },
                dismissButton = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetValidationState()
                            showMapView = true
                        }
                    ) {
                        Text(text = "ОК")
                    }
                }
            )
        }
    )

    AlertDialog(
        modifier = modifier.fillMaxSize(),
        onDismissRequest = onDismissRequest,
        text = {
            fetchPointState.Reduce(
                onLoading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                onSuccess = { coords ->
                    Column {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CustomTextFieldWithDropdown(
                                labelText = "Найти место",
                                value = placeSuggestionFieldValue,
                                onValueChange = { value ->
                                    placeSuggestionFieldValue = value
                                    viewModel.searchPoint(placeSuggestionFieldValue)
                                },
                                placeholder = "Введите адрес",
                                suggestions = points.map { it.displayName },
                                onSuggestionSelected = { selected ->
                                    val p = points.first { it.displayName == selected }
                                    placeSuggestionFieldValue = selected

                                    val type = p.geoJson?.get("type")?.jsonPrimitive?.content
                                    val isPolygon = type == "Polygon" || type == "MultiPolygon"

                                    if (isPolygon) {
                                        searchResult = p
                                        markerLocation = null
                                    } else {
                                        searchResult = p

                                        val lat =
                                            (p.boundingBox[0].toDouble() + p.boundingBox[1].toDouble()) / 2.0
                                        val lon =
                                            (p.boundingBox[2].toDouble() + p.boundingBox[3].toDouble()) / 2.0
                                        markerLocation = Coordinate(lat, lon)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isRequired = false,
                                dropdownFontSize = 12.sp,
                                dropdownMaxLines = 3,
                                leadingIcon = GlobalLocationPinIcon,
                                showClearButton = false
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (showMapView) {
                                MapView(
                                    initialCenter = Coordinate(coords.lat, coords.lon),
                                    initialZoom = 10,
                                    searchResult = searchResult,
                                    markerPosition = markerLocation,
                                    onLocationSelected = { coord ->
                                        markerLocation = coord
                                        searchResult = null
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = {
                    placeSuggestionFieldValue = ""
                    searchResult = null
                    markerLocation = null
                }) {
                    Text("Очистить")
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDismissRequest) { Text("Отмена") }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        resultPoint = if (searchResult != null || markerLocation != null) {
                            Point(
                                address = searchResult?.displayName,
                                lat = searchResult?.lat ?: markerLocation!!.lat,
                                lon = searchResult?.lon ?: markerLocation!!.lon
                            )
                        } else null

                        resultPoint?.let { point ->
                            viewModel.confirmPointSelection(point, selectedCityName)
                        }
                    },
                    enabled = finalSelectedCoordinate != null
                ) {
                    Text(text = "Подтвердить выбор")
                }
            }
        },
        dismissButton = {}
    )
}