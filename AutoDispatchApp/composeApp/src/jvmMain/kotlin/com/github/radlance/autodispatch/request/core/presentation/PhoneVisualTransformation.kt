package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.min

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }

        val formatted = buildString {
            append("+7 ")
            if (digits.isNotEmpty()) {
                append("(")
                append(digits.substring(0, min(3, digits.length)))
            }
            if (digits.length > 3) {
                append(") ")
                append(digits.substring(3, min(6, digits.length)))
            }
            if (digits.length > 6) {
                append("-")
                append(digits.substring(6, min(8, digits.length)))
            }
            if (digits.length > 8) {
                append("-")
                append(digits.substring(8, min(10, digits.length)))
            }
        }

        val offsetMapping = object : OffsetMapping {
            private val prefixLength = 3

            override fun originalToTransformed(offset: Int): Int {
                val adjusted = when {
                    offset <= 0 -> prefixLength
                    offset <= 3 -> prefixLength + 1 + offset
                    offset <= 6 -> prefixLength + 3 + offset
                    offset <= 8 -> prefixLength + 4 + offset
                    else -> prefixLength + 5 + offset
                }
                return min(adjusted, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= prefixLength) return 0

                val afterPrefix = formatted.drop(prefixLength)
                val digitsBefore = afterPrefix.take(offset - prefixLength).count { it.isDigit() }

                return min(digitsBefore, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
