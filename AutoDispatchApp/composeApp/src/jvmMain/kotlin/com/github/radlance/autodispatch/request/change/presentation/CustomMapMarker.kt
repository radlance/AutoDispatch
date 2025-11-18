package com.github.radlance.autodispatch.request.change.presentation

import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.MapMarkerDot
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Point
import javax.swing.ImageIcon

class CustomMapMarker(coord: Coordinate) : MapMarkerDot(coord.lat, coord.lon) {

    private val iconImage: Image? = ImageIcon(javaClass.getResource("/drawable/ic_map_marker.png")).image

    init {
        if (iconImage != null) {
            println("Image loaded successfully with size: ${iconImage.getWidth(null)}x${iconImage.getHeight(null)}")
        } else {
            println("Failed to load image...")
        }
    }

    override fun paint(g: Graphics, position: Point, radius: Int) {
        val g2d = g as Graphics2D
        if (iconImage != null) {
            val imageSize = 40
            val x = position.x - imageSize / 2
            val y = position.y - imageSize / 2

            g2d.drawImage(iconImage, x, y, imageSize, imageSize, null)
        } else {
            g2d.fillOval(position.x - radius, position.y - radius, 2 * radius, 2 * radius)
        }
    }
}