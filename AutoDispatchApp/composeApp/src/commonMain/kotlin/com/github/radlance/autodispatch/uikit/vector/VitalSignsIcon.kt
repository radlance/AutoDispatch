package com.github.radlance.autodispatch.uikit.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val VitalSignsIcon: ImageVector
    get() {
        if (_VitalSignsIcon != null) {
            return _VitalSignsIcon!!
        }
        _VitalSignsIcon = ImageVector.Builder(
            name = "VitalSignsIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(326f, 789f)
                quadToRelative(-15f, -11f, -22f, -28f)
                lineToRelative(-92f, -241f)
                lineTo(40f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(228f)
                lineToRelative(92f, 244f)
                lineToRelative(184f, -485f)
                quadToRelative(7f, -17f, 22f, -28f)
                reflectiveQuadToRelative(34f, -11f)
                quadToRelative(19f, 0f, 34f, 11f)
                reflectiveQuadToRelative(22f, 28f)
                lineToRelative(92f, 241f)
                horizontalLineToRelative(172f)
                verticalLineToRelative(80f)
                lineTo(692f, 520f)
                lineToRelative(-92f, -244f)
                lineToRelative(-184f, 485f)
                quadToRelative(-7f, 17f, -22f, 28f)
                reflectiveQuadToRelative(-34f, 11f)
                quadToRelative(-19f, 0f, -34f, -11f)
                close()
            }
        }.build()

        return _VitalSignsIcon!!
    }

@Suppress("ObjectPropertyName")
private var _VitalSignsIcon: ImageVector? = null
