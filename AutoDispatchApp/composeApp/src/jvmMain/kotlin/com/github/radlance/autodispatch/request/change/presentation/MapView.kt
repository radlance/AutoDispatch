package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.JMapViewer
import org.openstreetmap.gui.jmapviewer.MapMarkerDot
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

@Composable
fun MapView(
    initialCenter: Coordinate,
    initialZoom: Int,
    searchResult: com.github.radlance.autodispatch.request.change.domain.Point? = null,
    markerPosition: Coordinate? = null,
    onLocationSelected: (Coordinate) -> Unit
) {
    val mapViewer = remember { JMapViewer() }

    var currentMarker by remember { mutableStateOf<MapMarkerDot?>(null) }
    // Храним все визуальные объекты (и линии, и полигоны) в одном списке
    var currentMapObjects by remember { mutableStateOf<List<MapPolygonImpl>>(emptyList()) }

    var dragged by remember { mutableStateOf<java.awt.Point?>(null) }
    val currentSearchResult by rememberUpdatedState(searchResult)

    // --- 1. Отрисовка Геометрии (Линии и Полигоны) ---
    LaunchedEffect(searchResult) {
        // Очистка
        currentMapObjects.forEach { mapViewer.removeMapPolygon(it) }
        currentMapObjects = emptyList()

        if (searchResult != null) {
            // Парсим геометрию
            if (searchResult.geoJson != null) {
                val newObjects = parseGeoJsonToMapObjects(searchResult.geoJson)
                newObjects.forEach { mapViewer.addMapPolygon(it) }
                currentMapObjects = newObjects
            }

            // Зум к объекту
            val box = searchResult.boundingBox
            val south = box[0].toDouble()
            val north = box[1].toDouble()
            val west = box[2].toDouble()
            val east = box[3].toDouble()

            val centerLat = (north + south) / 2.0
            val centerLon = (east + west) / 2.0

            val latDiff = north - south
            // Если объект очень маленький (точка), зумим близко, иначе вычисляем
            val newZoom = if (latDiff < 0.0001) 17 else calculateZoomLevel(latDiff)

            mapViewer.setDisplayPosition(Coordinate(centerLat, centerLon), newZoom)
        }
    }

    // --- 2. Отрисовка Маркера ---
    LaunchedEffect(markerPosition) {
        currentMarker?.let { mapViewer.removeMapMarker(it) }
        currentMarker = null

        if (markerPosition != null) {
            val marker = CustomMapMarker(markerPosition)
            mapViewer.addMapMarker(marker)
            currentMarker = marker
        }
    }

    LaunchedEffect(mapViewer) {
        mapViewer.apply {
            isScrollWrapEnabled = true
            setTileSource(OsmTileSource.Mapnik())
            setDisplayPosition(initialCenter, initialZoom)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    // Блокируем клик, ТОЛЬКО если выбран ПОЛИГОН (Регион).
                    // Если выбрана ЛИНИЯ (Улица) - разрешаем клик, чтобы уточнить номер дома.
                    if (currentSearchResult?.geoJson != null) {
                        val type = currentSearchResult!!.geoJson!!.get("type")?.jsonPrimitive?.content
                        if (type == "Polygon" || type == "MultiPolygon") {
                            return
                        }
                    }

                    if (e.button == MouseEvent.BUTTON1 && e.clickCount == 1) {
                        val iCoord = mapViewer.getPosition(e.point)
                        if (iCoord != null) {
                            onLocationSelected(Coordinate(iCoord.lat, iCoord.lon))
                        }
                    }
                }
                override fun mousePressed(e: MouseEvent) { dragged = e.point }
                override fun mouseReleased(e: MouseEvent) { dragged = null }
            })

            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(e: MouseEvent) {
                    dragged?.let { start ->
                        val current = e.point
                        moveMap(start.x - current.x, start.y - current.y)
                        dragged = current
                    }
                }
            })
        }
    }

    SwingPanel(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
        factory = { mapViewer }
    )
}
fun parseGeoJsonToMapObjects(geoJson: JsonObject): List<MapPolygonImpl> {
    val type = geoJson["type"]?.jsonPrimitive?.content ?: return emptyList()
    val coordinates = geoJson["coordinates"]?.jsonArray ?: return emptyList()

    val objects = mutableListOf<MapPolygonImpl>()

    try {
        when (type) {
            "Polygon" -> {
                objects.add(MapPolygonImpl(parseRing(coordinates[0].jsonArray)))
            }
            "MultiPolygon" -> {
                for (polyJson in coordinates) {
                    objects.add(MapPolygonImpl(parseRing(polyJson.jsonArray[0].jsonArray)))
                }
            }
            "LineString" -> {
                // Создаем нашу кастомную линию
                objects.add(MapPolyLine(parseRing(coordinates)))
            }
            "MultiLineString" -> {
                for (lineJson in coordinates) {
                    objects.add(MapPolyLine(parseRing(lineJson.jsonArray)))
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return objects
}

// Кастомный класс для рисования линий (не замкнутых)
class MapPolyLine(points: List<Coordinate>) : MapPolygonImpl(points) {
    init {
        color = java.awt.Color.BLUE
        stroke = java.awt.BasicStroke(3f)
        backColor = null // Без заливки
    }

    override fun paint(g: java.awt.Graphics, points: List<java.awt.Point>) {
        val g2d = g as java.awt.Graphics2D
        g2d.color = color
        g2d.stroke = stroke
        val xPoints = points.map { it.x }.toIntArray()
        val yPoints = points.map { it.y }.toIntArray()
        // Рисуем линию, не замыкая концы
        g2d.drawPolyline(xPoints, yPoints, points.size)
    }
}

// Вспомогательная (без изменений)
private fun parseRing(ring: JsonArray): List<Coordinate> {
    val coords = mutableListOf<Coordinate>()
    for (pointJson in ring) {
        val pointArray = pointJson.jsonArray
        val lon = pointArray[0].jsonPrimitive.double
        val lat = pointArray[1].jsonPrimitive.double
        coords.add(Coordinate(lat, lon))
    }
    return coords
}
private fun calculateZoomLevel(latDifference: Double): Int {
    return when {
        latDifference > 50.0 -> 2
        latDifference > 20.0 -> 4
        latDifference > 10.0 -> 5
        latDifference > 5.0 -> 6
        latDifference > 1.0 -> 8
        latDifference > 0.5 -> 10
        latDifference > 0.1 -> 12
        latDifference > 0.05 -> 14
        else -> 16
    }
}