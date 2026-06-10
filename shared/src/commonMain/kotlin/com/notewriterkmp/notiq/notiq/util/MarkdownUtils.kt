package com.notewriterkmp.notiq.notiq.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

fun renderMarkdown(original: String): AnnotatedString {
    val boldMatches = Regex("""(?s)\*\*(.*?)\*\*""").findAll(original).toList()
    val italicMatches = Regex("""(?s)_(.*?)_""").findAll(original).toList()
    val underlineMatches = Regex("""(?s)<u>(.*?)</u>""").findAll(original).toList()

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

    var tIdx = 0
    for (oIdx in 0..original.length) {
        val isTag = tagRanges.any { oIdx in it } && oIdx < original.length

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
