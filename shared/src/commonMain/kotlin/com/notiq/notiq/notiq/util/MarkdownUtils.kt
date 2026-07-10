package com.notiq.notiq.notiq.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

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

    // Override mappings to favor being "inside" styled ranges at boundaries
    boldMatches.forEach {
        val contentStart = it.range.first + 2
        val contentEndPos = it.range.last - 1
        if (contentStart < original.length) {
            val transStart = originalToTransformed[contentStart]
            if (transStart < finalTransformedToOriginal.size) finalTransformedToOriginal[transStart] = contentStart
        }
        if (contentEndPos + 1 <= original.length) {
            val transEnd = originalToTransformed[contentEndPos + 1]
            if (transEnd < finalTransformedToOriginal.size) finalTransformedToOriginal[transEnd] = contentEndPos + 1
        }
    }
    // ... (Repeat optimization for other matches if necessary, but bold/color are most common)
    colorMatches.forEach {
        val openingTagLength = it.groupValues[1].length + 8
        val contentStart = it.range.first + openingTagLength
        val contentEndPos = it.range.last - 7
        if (contentStart < original.length) {
            val transStart = originalToTransformed[contentStart]
            if (transStart < finalTransformedToOriginal.size) finalTransformedToOriginal[transStart] = contentStart
        }
        if (contentEndPos + 1 <= original.length) {
            val transEnd = originalToTransformed[contentEndPos + 1]
            if (transEnd < finalTransformedToOriginal.size) finalTransformedToOriginal[transEnd] = contentEndPos + 1
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
    if (original.isEmpty()) return AnnotatedString("")
    
    // Quick check to avoid regex if no markdown symbols are present
    if (!original.contains("**") && !original.contains("_") && !original.contains("<u>") && !original.contains("<color=")) {
        return AnnotatedString(original)
    }

    val boldMatches = boldRegex.findAll(original).toList()
    val italicMatches = italicRegex.findAll(original).toList()
    val underlineMatches = underlineRegex.findAll(original).toList()
    val colorMatches = colorRegex.findAll(original).toList()

    val tagRanges = mutableListOf<IntRange>()
    boldMatches.forEach {
        tagRanges.add(IntRange(it.range.first, it.range.first + 1))
        tagRanges.add(IntRange(it.range.last - 1, it.range.last))
    }
    italicMatches.forEach {
        tagRanges.add(IntRange(it.range.first, it.range.first))
        tagRanges.add(IntRange(it.range.last, it.range.last))
    }
    underlineMatches.forEach {
        tagRanges.add(IntRange(it.range.first, it.range.first + 2))
        tagRanges.add(IntRange(it.range.last - 3, it.range.last))
    }
    colorMatches.forEach {
        val openingTagLength = it.groupValues[1].length + 8
        tagRanges.add(IntRange(it.range.first, it.range.first + openingTagLength - 1))
        tagRanges.add(IntRange(it.range.last - 7, it.range.last))
    }

    val transformed = StringBuilder()
    val originalToTransformed = IntArray(original.length + 1)

    val isTagArray = BooleanArray(original.length)
    tagRanges.forEach { range ->
        for (i in range) {
            if (i in isTagArray.indices) isTagArray[i] = true
        }
    }

    var tIdx = 0
    for (oIdx in 0..original.length) {
        val isTag = oIdx < original.length && isTagArray[oIdx]

        if (!isTag) {
            if (oIdx < original.length) {
                transformed.append(original[oIdx])
            }
            originalToTransformed[oIdx] = tIdx
            tIdx++
        } else {
            originalToTransformed[oIdx] = tIdx
        }
    }

    return buildAnnotatedString {
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
}
