package com.github.radlance.autodispatch.presentation

import kotlinx.css.Color
import kotlinx.css.CssBuilder
import kotlinx.css.Display
import kotlinx.css.FontWeight
import kotlinx.css.LinearDimension
import kotlinx.css.Margin
import kotlinx.css.Padding
import kotlinx.css.TextAlign
import kotlinx.css.background
import kotlinx.css.backgroundColor
import kotlinx.css.body
import kotlinx.css.borderRadius
import kotlinx.css.boxShadow
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.lineHeight
import kotlinx.css.margin
import kotlinx.css.marginTop
import kotlinx.css.maxWidth
import kotlinx.css.padding
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.LineHeight
import kotlinx.css.properties.TextDecoration
import kotlinx.css.px
import kotlinx.css.rgb
import kotlinx.css.textAlign
import kotlinx.css.textDecoration

object EmailStyles {
    fun customer(): String = CssBuilder().apply {
        body {
            margin = Margin(0.px)
            padding = Padding(0.px)
            backgroundColor = Color("#f3f4f6")
            fontFamily = "Inter, Arial, sans-serif"
        }

        rule(".card") {
            maxWidth = 600.px
            margin = Margin(
                vertical = 24.px,
                horizontal = LinearDimension.auto
            )
            backgroundColor = Color.white
            borderRadius = 12.px
            boxShadow += BoxShadow(
                offsetY = 12.px,
                offsetX = 0.px,
                blurRadius = 28.px,
                color = rgb(red = 0, green = 0, blue = 0, alpha = 0.25)
            )
        }

        rule(".header") {
            background = "linear-gradient(135deg, #6200EE, #7C4DFF)"
            padding = Padding(28.px)
            textAlign = TextAlign.center
        }

        rule(".content") {
            padding = Padding(32.px)
            fontSize = 15.px
            lineHeight = LineHeight("1.6")
            color = Color("#1f2937")
        }

        rule(".button") {
            display = Display.inlineBlock
            marginTop = 24.px
            backgroundColor = Color("#6200EE")
            color = Color.white
            padding = Padding(vertical = 14.px, horizontal = 28.px)
            borderRadius = 8.px
            textDecoration = TextDecoration.none
            fontWeight = FontWeight.w500
        }

        rule(".footer") {
            padding = Padding(16.px)
            textAlign = TextAlign.center
            fontSize = 12.px
            color = Color("#6b7280")
            backgroundColor = Color("#f9fafb")
        }
    }.toString()

    fun driver(): String = CssBuilder().apply {
        body {
            margin = Margin(0.px)
            padding = Padding(0.px)
            backgroundColor = Color("#020617")
            fontFamily = "Inter, Arial, sans-serif"
        }

        rule(".card") {
            maxWidth = 600.px
            margin = Margin(
                vertical = 24.px,
                horizontal = LinearDimension.auto
            )
            backgroundColor = Color.white
            borderRadius = 12.px
            boxShadow += BoxShadow(
                offsetX = 0.px,
                offsetY = 12.px,
                blurRadius = 28.px,
                color = rgb(red = 0, green = 0, blue = 0, alpha = 0.25)
            )
        }

        rule(".header") {
            backgroundColor = Color("#020617")
            padding = Padding(24.px)
            color = Color.white
            textAlign = TextAlign.center
        }

        rule(".content") {
            padding = Padding(28.px)
            fontSize = 15.px
            lineHeight = LineHeight("1.6")
            color = Color("#111827")
            textAlign = TextAlign.center
        }

        rule(".button") {
            display = Display.inlineBlock
            marginTop = 24.px
            backgroundColor = Color("#16a34a")
            color = Color.white
            padding = Padding(vertical = 14.px, horizontal = 26.px)
            borderRadius = 8.px
            textDecoration = TextDecoration.none
            fontWeight = FontWeight.w600
        }

        rule(".footer") {
            padding = Padding(14.px)
            textAlign = TextAlign.center
            fontSize = 12.px
            color = Color("#9ca3af")
            backgroundColor = Color("#f9fafb")
        }
    }.toString()
}