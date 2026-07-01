package com.notiq.notiq.notiq.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

internal val boldRegex = Regex("""(?s)\*\*(.*?)\*\*""")
internal val italicRegex = Regex("""(?s)_(.*?)_""")
internal val underlineRegex = Regex("""(?s)<u>(.*?)</u>""")

data class MarkdownMetadata(
    val annotatedString: AnnotatedString,
    val originalToTransformed: IntArray,
    val transformedToOriginal: List<Int>
)

fun getMarkdownMetadata(original: String): MarkdownMetadata {
    val boldMatches = boldRegex.findAll(original).toList()
    val italicMatches = italicRegex.findAll(original).toList()
    val underlineMatches = underlineRegex.findAll(original).toList()

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

    val transformed = StringBuilder()
    val originalToTransformed = IntArray(original.length + 1)
    val transformedToOriginal = mutableListOf<Int>()

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
                transformedToOriginal.add(oIdx)
            }
            originalToTransformed[oIdx] = tIdx
            tIdx++
        } else {
            originalToTransformed[oIdx] = tIdx
        }
    }
    transformedToOriginal.add(original.length)

    // Override mappings to favor being "inside" styled ranges at boundaries
    boldMatches.forEach {
        val contentStart = it.range.first + 2
        val contentEndPos = it.range.last + 1 - 2
        transformedToOriginal[originalToTransformed[contentStart]] = contentStart
        transformedToOriginal[originalToTransformed[contentEndPos]] = contentEndPos
    }
    italicMatches.forEach {
        val contentStart = it.range.first + 1
        val contentEndPos = it.range.last + 1 - 1
        transformedToOriginal[originalToTransformed[contentStart]] = contentStart
        transformedToOriginal[originalToTransformed[contentEndPos]] = contentEndPos
    }
    underlineMatches.forEach {
        val contentStart = it.range.first + 3
        val contentEndPos = it.range.last + 1 - 4
        transformedToOriginal[originalToTransformed[contentStart]] = contentStart
        transformedToOriginal[originalToTransformed[contentEndPos]] = contentEndPos
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
    }

    return MarkdownMetadata(annotatedString, originalToTransformed, transformedToOriginal)
}

fun renderMarkdown(original: String): AnnotatedString {
    if (original.isEmpty()) return AnnotatedString("")
    
    // Quick check to avoid regex if no markdown symbols are present
    if (!original.contains("**") && !original.contains("_") && !original.contains("<u>")) {
        return AnnotatedString(original)
    }

    val boldMatches = boldRegex.findAll(original).toList()
    val italicMatches = italicRegex.findAll(original).toList()
    val underlineMatches = underlineRegex.findAll(original).toList()

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
    }
}
