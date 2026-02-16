package com.merchpulse.feature.auth.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation(private val maxLength: Int) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= maxLength) text.text.substring(0, maxLength) else text.text
        var out = ""
        
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (maxLength == 11) { // Egypt XXX XXXX XXXX
                if (i == 2 || i == 6) out += " "
            } else if (maxLength == 10) { // USA XXX XXX XXXX
                if (i == 2 || i == 5) out += " "
            } else { // 9 digits XXX XXX XXX
                if (i == 2 || i == 5) out += " "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (maxLength == 11) {
                    if (offset <= 3) return offset
                    if (offset <= 7) return offset + 1
                    return offset + 2
                } else { // 9 or 10 digits
                    if (offset <= 3) return offset
                    if (offset <= 6) return offset + 1
                    return offset + 2
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (maxLength == 11) {
                    if (offset <= 3) return offset
                    if (offset <= 8) return offset - 1
                    return offset - 2
                } else {
                    if (offset <= 3) return offset
                    if (offset <= 7) return offset - 1
                    return offset - 2
                }
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
