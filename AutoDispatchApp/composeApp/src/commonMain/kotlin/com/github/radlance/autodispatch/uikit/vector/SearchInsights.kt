package com.github.radlance.autodispatch.uikit.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SearchInsightsIcon: ImageVector
    get() {
        if (_SearchInsightsIcon != null) {
            return _SearchInsightsIcon!!
        }
        _SearchInsightsIcon = ImageVector.Builder(
            name = "SearchInsightsIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(400f, 640f)
                quadToRelative(100f, 0f, 170f, -70f)
                reflectiveQuadToRelative(70f, -170f)
                quadToRelative(0f, -100f, -70f, -170f)
                reflectiveQuadToRelative(-170f, -70f)
                quadToRelative(-100f, 0f, -170f, 70f)
                reflectiveQuadToRelative(-70f, 170f)
                quadToRelative(0f, 100f, 70f, 170f)
                reflectiveQuadToRelative(170f, 70f)
                close()
                moveTo(360f, 520f)
                verticalLineToRelative(-280f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(280f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(220f, 520f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(200f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(500f, 520f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(824f, 880f)
                lineTo(597f, 653f)
                quadToRelative(-41f, 32f, -91f, 49.5f)
                reflectiveQuadTo(400f, 720f)
                quadToRelative(-134f, 0f, -227f, -93f)
                reflectiveQuadTo(80f, 400f)
                quadToRelative(0f, -134f, 93f, -227f)
                reflectiveQuadToRelative(227f, -93f)
                quadToRelative(134f, 0f, 227f, 93f)
                reflectiveQuadToRelative(93f, 227f)
                quadToRelative(0f, 56f, -17.5f, 106f)
                reflectiveQuadTo(653f, 597f)
                lineToRelative(227f, 227f)
                lineToRelative(-56f, 56f)
                close()
            }
        }.build()

        return _SearchInsightsIcon!!
    }

@Suppress("ObjectPropertyName")
private var _SearchInsightsIcon: ImageVector? = null
