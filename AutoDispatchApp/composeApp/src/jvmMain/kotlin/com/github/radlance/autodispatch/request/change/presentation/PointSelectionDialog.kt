package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.uikit.vector.GlobalLocationPinIcon
import org.koin.compose.viewmodel.koinViewModel
import org.openstreetmap.gui.jmapviewer.Coordinate

@Composable
fun PointSelectionDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PointSelectionViewModel = koinViewModel()
) {
    // TODO добавить полигон на карту
    val fetchPointState by viewModel.fetchPointState.collectAsState()
    val points by viewModel.points.collectAsState()

    var placeSuggestionFieldValue by rememberSaveable { mutableStateOf("") }
    var selectedBounding by remember { mutableStateOf<List<String>?>(null) }
    var initialCenter by remember { mutableStateOf(Coordinate(55.75, 37.61)) }
    var initialZoom by remember { mutableStateOf(10) }

    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

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

                                initialCenter = Coordinate(p.lat.toDouble(), p.lon.toDouble())
                                selectedBounding = p.boundingBox
                                initialZoom = 15
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isRequired = false,
                            dropdownFontSize = 12.sp,
                            dropdownMaxLines = 3,
                            leadingIcon = GlobalLocationPinIcon
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        MapView(
                            initialCenter = Coordinate(coords.lat, coords.lon),
                            initialZoom = 10,
                            boundingBox = selectedBounding
                        ) { coord ->
                            latitude = coord.lat.toString()
                            longitude = coord.lon.toString()
                        }
                    }
                },
                onError = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorMessage(
                            message = it,
                            onRetry = viewModel::fetchCoords
                        )
                    }
                }
            )

        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismissRequest,
            ) {
                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Подтвердить выбор")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Отмена")
            }
        }
    )
}