package com.github.radlance.autodispatch.uikit.vector

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val FinanceIcon: ImageVector
    get() {
        if (_FinanceIcon != null) {
            return _FinanceIcon!!
        }
        _FinanceIcon = ImageVector.Builder(
            name = "FinanceIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(320f, 546f)
                verticalLineToRelative(-306f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(306f)
                lineToRelative(-60f, -56f)
                lineToRelative(-60f, 56f)
                close()
                moveTo(520f, 606f)
                verticalLineToRelative(-526f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(406f)
                lineTo(520f, 606f)
                close()
                moveTo(120f, 744f)
                verticalLineToRelative(-344f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(224f)
                lineTo(120f, 744f)
                close()
                moveTo(120f, 842f)
                lineTo(378f, 584f)
                lineTo(520f, 706f)
                lineTo(744f, 482f)
                horizontalLineToRelative(-64f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(200f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-64f)
                lineTo(524f, 814f)
                lineTo(382f, 692f)
                lineTo(232f, 842f)
                lineTo(120f, 842f)
                close()
            }
        }.build()

        return _FinanceIcon!!
    }

@Suppress("ObjectPropertyName")
private var _FinanceIcon: ImageVector? = null
