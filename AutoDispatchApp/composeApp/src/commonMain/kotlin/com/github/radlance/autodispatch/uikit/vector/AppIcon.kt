package com.github.radlance.autodispatch.uikit.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcon: ImageVector
    get() {
        if (_AppIcon != null) {
            return _AppIcon!!
        }
        _AppIcon = ImageVector.Builder(
            name = "AppIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(280f, 800f)
                quadToRelative(-50f, 0f, -85f, -35f)
                reflectiveQuadToRelative(-35f, -85f)
                lineTo(60f, 680f)
                lineToRelative(18f, -80f)
                horizontalLineToRelative(113f)
                quadToRelative(17f, -19f, 40f, -29.5f)
                reflectiveQuadToRelative(49f, -10.5f)
                quadToRelative(26f, 0f, 49f, 10.5f)
                reflectiveQuadToRelative(40f, 29.5f)
                horizontalLineToRelative(167f)
                lineToRelative(84f, -360f)
                lineTo(182f, 240f)
                lineToRelative(4f, -17f)
                quadToRelative(6f, -28f, 27.5f, -45.5f)
                reflectiveQuadTo(264f, 160f)
                horizontalLineToRelative(456f)
                lineToRelative(-37f, 160f)
                horizontalLineToRelative(117f)
                lineToRelative(120f, 160f)
                lineToRelative(-40f, 200f)
                horizontalLineToRelative(-80f)
                quadToRelative(0f, 50f, -35f, 85f)
                reflectiveQuadToRelative(-85f, 35f)
                quadToRelative(-50f, 0f, -85f, -35f)
                reflectiveQuadToRelative(-35f, -85f)
                lineTo(400f, 680f)
                quadToRelative(0f, 50f, -35f, 85f)
                reflectiveQuadToRelative(-85f, 35f)
                close()
                moveTo(637f, 520f)
                horizontalLineToRelative(193f)
                lineToRelative(4f, -21f)
                lineToRelative(-74f, -99f)
                horizontalLineToRelative(-95f)
                lineToRelative(-28f, 120f)
                close()
                moveTo(618f, 247f)
                lineTo(620f, 240f)
                lineTo(536f, 600f)
                lineTo(538f, 593f)
                lineTo(572f, 447f)
                lineTo(618f, 247f)
                close()
                moveTo(20f, 533f)
                lineToRelative(20f, -80f)
                horizontalLineToRelative(220f)
                lineToRelative(-20f, 80f)
                lineTo(20f, 533f)
                close()
                moveTo(100f, 387f)
                lineTo(120f, 307f)
                horizontalLineToRelative(260f)
                lineToRelative(-20f, 80f)
                lineTo(100f, 387f)
                close()
                moveTo(280f, 720f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(320f, 680f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(280f, 640f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(240f, 680f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(280f, 720f)
                close()
                moveTo(680f, 720f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(720f, 680f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(680f, 640f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(640f, 680f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(680f, 720f)
                close()
            }
        }.build()

        return _AppIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AppIcon: ImageVector? = null
