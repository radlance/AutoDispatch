package com.github.radlance.autodispatch.uikit.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Package2Icon: ImageVector
    get() {
        if (_Package2Icon != null) {
            return _Package2Icon!!
        }
        _Package2Icon = ImageVector.Builder(
            name = "Package2Icon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(440f, 777f)
                verticalLineToRelative(-274f)
                lineTo(200f, 364f)
                verticalLineToRelative(274f)
                lineToRelative(240f, 139f)
                close()
                moveTo(520f, 777f)
                lineTo(760f, 638f)
                verticalLineToRelative(-274f)
                lineTo(520f, 503f)
                verticalLineToRelative(274f)
                close()
                moveTo(440f, 869f)
                lineTo(160f, 708f)
                quadToRelative(-19f, -11f, -29.5f, -29f)
                reflectiveQuadTo(120f, 639f)
                verticalLineToRelative(-318f)
                quadToRelative(0f, -22f, 10.5f, -40f)
                reflectiveQuadToRelative(29.5f, -29f)
                lineToRelative(280f, -161f)
                quadToRelative(19f, -11f, 40f, -11f)
                reflectiveQuadToRelative(40f, 11f)
                lineToRelative(280f, 161f)
                quadToRelative(19f, 11f, 29.5f, 29f)
                reflectiveQuadToRelative(10.5f, 40f)
                verticalLineToRelative(318f)
                quadToRelative(0f, 22f, -10.5f, 40f)
                reflectiveQuadTo(800f, 708f)
                lineTo(520f, 869f)
                quadToRelative(-19f, 11f, -40f, 11f)
                reflectiveQuadToRelative(-40f, -11f)
                close()
                moveTo(640f, 341f)
                lineTo(717f, 297f)
                lineTo(480f, 160f)
                lineTo(402f, 205f)
                lineTo(640f, 341f)
                close()
                moveTo(480f, 434f)
                lineTo(558f, 389f)
                lineTo(321f, 252f)
                lineTo(243f, 297f)
                lineTo(480f, 434f)
                close()
            }
        }.build()

        return _Package2Icon!!
    }

@Suppress("ObjectPropertyName")
private var _Package2Icon: ImageVector? = null
