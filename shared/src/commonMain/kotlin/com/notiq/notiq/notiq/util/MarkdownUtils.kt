package com.notiq.notiq.notiq.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em

internal val boldRegex = Regex("""(?s)\*\*(.*?)\*\*""")
internal val italicRegex = Regex("""(?s)_(.*?)_""")
internal val underlineRegex = Regex("""(?s)<u>(.*?)</u>""")
internal val colorRegex = Regex("""(?s)<color=(#[0-9a-fA-F]{6,8})>(.*?)</color>""")

fun parseColor(colorString: String): Color {
    return try {
        val hex = colorString.removePrefix("#")
        if (hex.length == 6) {
            Color(0xFF000000 or hex.toLong(16))
        } else if (hex.length == 8) {
            Color(hex.toLong(16))
        } else {
            Color.Unspecified
        }
    } catch (e: Exception) {
        Color.Unspecified
    }
}

data class MarkdownMetadata(
    val annotatedString: AnnotatedString,
    val originalToTransformed: IntArray,
    val transformedToOriginal: IntArray
)

fun getMarkdownMetadata(original: String): MarkdownMetadata {
    if (original.isEmpty()) {
        return MarkdownMetadata(AnnotatedString(""), IntArray(1) { 0 }, IntArray(1) { 0 })
    }

    val boldMatches = boldRegex.findAll(original).toList()
    val italicMatches = italicRegex.findAll(original).toList()
    val underlineMatches = underlineRegex.findAll(original).toList()
    val colorMatches = colorRegex.findAll(original).toList()

    val isTagArray = BooleanArray(original.length)
    
    boldMatches.forEach {
        for (i in it.range.first..it.range.first + 1) isTagArray[i] = true
        for (i in it.range.last - 1..it.range.last) isTagArray[i] = true
    }
    italicMatches.forEach {
        isTagArray[it.range.first] = true
        isTagArray[it.range.last] = true
    }
    underlineMatches.forEach {
        for (i in it.range.first..it.range.first + 2) isTagArray[i] = true
        for (i in it.range.last - 3..it.range.last) isTagArray[i] = true
    }
    colorMatches.forEach {
        val openingTagLength = it.groupValues[1].length + 8
        for (i in it.range.first until it.range.first + openingTagLength) isTagArray[i] = true
        for (i in it.range.last - 7..it.range.last) isTagArray[i] = true
    }

    val transformed = StringBuilder(original.length)
    val originalToTransformed = IntArray(original.length + 1)
    val transformedToOriginalList = IntArray(original.length + 1)

    var tIdx = 0
    for (oIdx in 0 until original.length) {
        if (!isTagArray[oIdx]) {
            transformed.append(original[oIdx])
            transformedToOriginalList[tIdx] = oIdx
            originalToTransformed[oIdx] = tIdx
            tIdx++
        } else {
            originalToTransformed[oIdx] = tIdx
        }
    }
    originalToTransformed[original.length] = tIdx
    transformedToOriginalList[tIdx] = original.length
    
    val finalTransformedToOriginal = transformedToOriginalList.copyOf(tIdx + 1)

    // Robust mapping for boundaries:
    // When the cursor is at the edge of a styled block, we want it to map 
    // to the "inside" of the tags so that typing continues the style.
    
    boldMatches.forEach {
        val openingLen = 2
        val closingLen = 2
        val contentStart = it.range.first + openingLen
        val contentEnd = it.range.last + 1 - closingLen
        
        val transStart = originalToTransformed[it.range.first]
        if (transStart < finalTransformedToOriginal.size) {
            finalTransformedToOriginal[transStart] = contentStart
        }
        
        val transEnd = originalToTransformed[it.range.last + 1]
        if (transEnd < finalTransformedToOriginal.size) {
            finalTransformedToOriginal[transEnd] = contentEnd
        }
    }
    
    italicMatches.forEach {
        val openingLen = 1
        val closingLen = 1
        val contentStart = it.range.first + openingLen
        val contentEnd = it.range.last + 1 - closingLen
        val transStart = originalToTransformed[it.range.first]
        if (transStart < finalTransformedToOriginal.size) finalTransformedToOriginal[transStart] = contentStart
        val transEnd = originalToTransformed[it.range.last + 1]
        if (transEnd < finalTransformedToOriginal.size) finalTransformedToOriginal[transEnd] = contentEnd
    }
    
    underlineMatches.forEach {
        val openingLen = 3
        val closingLen = 4
        val contentStart = it.range.first + openingLen
        val contentEnd = it.range.last + 1 - closingLen
        val transStart = originalToTransformed[it.range.first]
        if (transStart < finalTransformedToOriginal.size) finalTransformedToOriginal[transStart] = contentStart
        val transEnd = originalToTransformed[it.range.last + 1]
        if (transEnd < finalTransformedToOriginal.size) finalTransformedToOriginal[transEnd] = contentEnd
    }

    colorMatches.forEach {
        val openingTagLength = it.groupValues[1].length + 8
        val closingLen = 8
        val contentStart = it.range.first + openingTagLength
        val contentEnd = it.range.last + 1 - closingLen
        
        val transStart = originalToTransformed[it.range.first]
        if (transStart < finalTransformedToOriginal.size) {
            finalTransformedToOriginal[transStart] = contentStart
        }
        
        val transEnd = originalToTransformed[it.range.last + 1]
        if (transEnd < finalTransformedToOriginal.size) {
            finalTransformedToOriginal[transEnd] = contentEnd
        }
    }

    val annotatedString = buildAnnotatedString {
        append(transformed.toString())
        boldMatches.forEach {
            addStyle(SpanStyle(fontWeight = FontWeight.Bold), originalToTransformed[it.range.first], originalToTransformed[it.range.last + 1])
        }
        italicMatches.forEach {
            addStyle(SpanStyle(fontStyle = FontStyle.Italic), originalToTransformed[it.range.first], originalToTransformed[it.range.last + 1])
        }
        underlineMatches.forEach {
            addStyle(SpanStyle(textDecoration = TextDecoration.Underline), originalToTransformed[it.range.first], originalToTransformed[it.range.last + 1])
        }
        colorMatches.forEach {
            val color = parseColor(it.groupValues[1])
            addStyle(SpanStyle(color = color), originalToTransformed[it.range.first], originalToTransformed[it.range.last + 1])
        }
    }

    return MarkdownMetadata(annotatedString, originalToTransformed, finalTransformedToOriginal)
}

fun renderMarkdown(original: String): AnnotatedString {
    // Reuse mapping logic for consistency
    return getMarkdownMetadata(original).annotatedString
}

fun cleanEmptyTags(text: String): String {
    if (text.isEmpty()) return ""
    var result = text
    val regexes = listOf(boldRegex, italicRegex, underlineRegex, colorRegex)
    var changed: Boolean
    do {
        changed = false
        for (regex in regexes) {
            val matches = regex.findAll(result).filter { match ->
                if (regex == colorRegex) match.groupValues[2].isEmpty()
                else match.groupValues[1].isEmpty()
            }.toList()
            
            if (matches.isNotEmpty()) {
                for (match in matches.reversed()) {
                    result = result.removeRange(match.range)
                    changed = true
                }
            }
        }
    } while (changed)
    return result
}
