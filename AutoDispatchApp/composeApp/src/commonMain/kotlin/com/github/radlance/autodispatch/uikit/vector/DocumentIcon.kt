package com.github.radlance.autodispatch.uikit.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DocumentIcon: ImageVector
    get() {
        if (_DocumentIcon != null) {
            return _DocumentIcon!!
        }
        _DocumentIcon = ImageVector.Builder(
            name = "DocumentIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(320f, 520f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(-80f)
                lineTo(320f, 440f)
                verticalLineToRelative(80f)
                close()
                moveTo(320f, 640f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(-80f)
                lineTo(320f, 560f)
                verticalLineToRelative(80f)
                close()
                moveTo(320f, 760f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-80f)
                lineTo(320f, 680f)
                verticalLineToRelative(80f)
                close()
                moveTo(240f, 880f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(160f, 800f)
                verticalLineToRelative(-640f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(240f, 80f)
                horizontalLineToRelative(320f)
                lineToRelative(240f, 240f)
                verticalLineToRelative(480f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(720f, 880f)
                lineTo(240f, 880f)
                close()
                moveTo(520f, 360f)
                verticalLineToRelative(-200f)
                lineTo(240f, 160f)
                verticalLineToRelative(640f)
                horizontalLineToRelative(480f)
                verticalLineToRelative(-440f)
                lineTo(520f, 360f)
                close()
                moveTo(240f, 160f)
                verticalLineToRelative(200f)
                verticalLineToRelative(-200f)
                verticalLineToRelative(640f)
                verticalLineToRelative(-640f)
                close()
            }
        }.build()

        return _DocumentIcon!!
    }

@Suppress("ObjectPropertyName")
private var _DocumentIcon: ImageVector? = null
