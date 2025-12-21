package com.github.radlance.autodispatch.platform

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun MapRouteDialog(
    lat: Double,
    lon: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    val apps = remember(lat, lon) { findMapApps(context, lat, lon) }

    LaunchedEffect(apps) {
        when (apps.size) {
            0 -> {
                runCatching {
                    val uri = "geo:$lat,$lon?q=$lat,$lon".toUri()
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                onDismiss()
            }

            1 -> {
                context.startActivity(apps.first().intent)
                onDismiss()
            }
        }
    }

    if (apps.size > 1) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {

                Text(
                    text = "Построить маршрут через",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )

                LazyColumn {
                    items(apps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    context.startActivity(app.intent)
                                    onDismiss()
                                }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = app.icon,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                app.name,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun findMapApps(context: Context, lat: Double, lon: Double): List<AndroidMapApp> {
    val pm = context.packageManager
    val result = mutableListOf<AndroidMapApp>()

    val googleUri = "google.navigation:q=$lat,$lon".toUri()
    val google = Intent(Intent.ACTION_VIEW, googleUri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (google.resolveActivity(pm) != null) {
        result.add(
            AndroidMapApp(
                "Google Maps",
                google,
                pm.getAppIconBmp("com.google.android.apps.maps")
            )
        )
    }

    val yandexUri = "yandexmaps://build_route_on_map?lat_to=$lat&lon_to=$lon&what=auto".toUri()
    val yandex = Intent(Intent.ACTION_VIEW, yandexUri).apply {
        setPackage("ru.yandex.yandexmaps")
    }
    if (yandex.resolveActivity(pm) != null) {
        result.add(
            AndroidMapApp(
                "Яндекс Карты",
                yandex,
                pm.getAppIconBmp("ru.yandex.yandexmaps")
            )
        )
    }

    val dgisUri = "dgis://2gis.ru/routeSearch/rsType/car/to/$lon,$lat".toUri()
    val dgis = Intent(Intent.ACTION_VIEW, dgisUri).apply {
        setPackage("ru.dublgis.dgismobile")
    }
    if (dgis.resolveActivity(pm) != null) {
        result.add(
            AndroidMapApp(
                "2ГИС",
                dgis,
                pm.getAppIconBmp("ru.dublgis.dgismobile")
            )
        )
    }

    return result
}