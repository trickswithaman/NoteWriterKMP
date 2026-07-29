package com.notiq.notiq.notiq.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration

/**
 * Represents a style range applied to the text.
 */
data class StyleSpan(
    val start: Int,
    val end: Int,
    val style: SpanStyle
)

class RichTextState(
    initialText: String = ""
) {
    var value by mutableStateOf(TextFieldValue(initialText))
    
    var spans by mutableStateOf(listOf<StyleSpan>())
        private set

    // Tracks styles that will be applied to the next typed characters
    var currentTypingStyles by mutableStateOf(setOf<SpanStyle>())
        private set

    /**
     * Toggles a style on the current selection or at the cursor.
     */
    fun toggleStyle(style: SpanStyle) {
        val selection = value.selection
        if (selection.collapsed) {
            // If it's a color, we want to replace existing typing color
            if (style.color != Color.Unspecified) {
                val currentNoColor = currentTypingStyles.filter { it.color == Color.Unspecified }.toSet()
                currentTypingStyles = if (currentTypingStyles.any { it.color == style.color }) {
                    currentNoColor
                } else {
                    currentNoColor + style
                }
            } else {
                // Standard toggle for B/I/U
                currentTypingStyles = if (currentTypingStyles.contains(style)) {
                    currentTypingStyles - style
                } else {
                    currentTypingStyles + style
                }
            }
        } else {
            val start = selection.min
            val end = selection.max

            if (style.color != Color.Unspecified) {
                // If applying color, remove any other colors from this EXACT range first
                val existingSameRange = spans.find { it.start == start && it.end == end && it.style.color != Color.Unspecified }
                
                if (existingSameRange != null) {
                    spans = spans.filter { it != existingSameRange }
                    // If the new color is different, add it. If same, we just removed it (toggle off).
                    if (existingSameRange.style.color != style.color) {
                        spans = spans + StyleSpan(start, end, style)
                    }
                } else {
                    spans = spans + StyleSpan(start, end, style)
                }
            } else {
                // Standard toggle for B/I/U
                val existing = spans.find { it.start == start && it.end == end && it.style == style }
                if (existing != null) {
                    spans = spans.filter { it != existing }
                } else {
                    spans = spans + StyleSpan(start, end, style)
                }
            }
            updateValue(value)
        }
    }

    /**
     * Updates the TextFieldValue and handles span shifting and continuous styling.
     */
    fun updateValue(newValue: TextFieldValue) {
        val oldText = value.text
        val newText = newValue.text
        val oldSelection = value.selection
        val newSelection = newValue.selection
        
        if (oldText != newText) {
            val diff = newText.length - oldText.length
            val changeIndex = findChangeIndex(oldText, newText)
            
            if (changeIndex != -1) {
                // 1. Shift existing spans
                spans = spans.mapNotNull { span ->
                    var nStart = span.start
                    var nEnd = span.end
                    
                    if (diff > 0) { // Insertion
                        if (nStart >= changeIndex) nStart += diff
                        if (nEnd > changeIndex) nEnd += diff
                    } else { // Deletion
                        val deleteRangeStart = changeIndex
                        val deleteRangeEnd = changeIndex - diff // diff is negative
                        
                        // If span is fully inside deleted range, remove it
                        if (nStart >= deleteRangeStart && nEnd <= deleteRangeEnd) return@mapNotNull null
                        
                        // Adjust boundaries
                        if (nStart > deleteRangeStart) nStart = (nStart + diff).coerceAtLeast(deleteRangeStart)
                        if (nEnd > deleteRangeStart) nEnd = (nEnd + diff).coerceAtLeast(deleteRangeStart)
                    }
                    
                    if (nStart >= nEnd) null else span.copy(start = nStart, end = nEnd)
                }

                // 2. Apply currentTypingStyles to inserted text
                if (diff > 0 && currentTypingStyles.isNotEmpty()) {
                    val insertedSpans = currentTypingStyles.map { style ->
                        StyleSpan(changeIndex, changeIndex + diff, style)
                    }
                    spans = spans + insertedSpans
                }
                
                // 3. Merge adjacent spans of the same style
                mergeSpans()
            }
        } else if (newSelection != oldSelection && newSelection.collapsed) {
            // Cursor moved: Sync currentTypingStyles with the style at the new position
            updateTypingStylesFromCursor(newSelection.start)
        }
        
        // Build AnnotatedString
        val annotatedString = buildAnnotatedString {
            append(newText)
            spans.forEach { span ->
                addStyle(span.style, span.start, span.end)
            }
        }
        
        value = newValue.copy(annotatedString = annotatedString)
    }

    private fun mergeSpans() {
        if (spans.isEmpty()) return
        
        val newSpans = mutableListOf<StyleSpan>()
        val grouped = spans.groupBy { it.style }
        
        grouped.forEach { (_, styleSpans) ->
            if (styleSpans.size <= 1) {
                newSpans.addAll(styleSpans)
                return@forEach
            }
            
            val sorted = styleSpans.sortedBy { it.start }
            var current = sorted[0]
            
            for (i in 1 until sorted.size) {
                val next = sorted[i]
                if (next.start <= current.end) {
                    current = current.copy(end = maxOf(current.end, next.end))
                } else {
                    newSpans.add(current)
                    current = next
                }
            }
            newSpans.add(current)
        }
        spans = newSpans
    }

    private fun updateTypingStylesFromCursor(cursorPosition: Int) {
        if (cursorPosition == 0) {
            currentTypingStyles = emptySet()
            return
        }
        val pos = cursorPosition - 1
        currentTypingStyles = spans.filter { pos in it.start until it.end }.map { it.style }.toSet()
    }
    
    private fun findChangeIndex(old: String, new: String): Int {
        var i = 0
        while (i < old.length && i < new.length && old[i] == new[i]) {
            i++
        }
        return i
    }

    fun isStyleActive(style: SpanStyle): Boolean {
        val selection = value.selection
        if (selection.collapsed) {
            return currentTypingStyles.contains(style)
        }
        return spans.any { 
            it.start <= selection.min && it.end >= selection.max && it.style == style 
        }
    }

    fun getActiveColor(): Color {
        val selection = value.selection
        if (selection.collapsed) {
            return currentTypingStyles.find { it.color != Color.Unspecified }?.color ?: Color.Unspecified
        }
        return spans.find { 
            it.start <= selection.min && it.end >= selection.max && it.style.color != Color.Unspecified 
        }?.style?.color ?: Color.Unspecified
    }

    fun removeColorStyle() {
        val selection = value.selection
        if (selection.collapsed) {
            currentTypingStyles = currentTypingStyles.filter { it.color == Color.Unspecified }.toSet()
        } else {
            val start = selection.min
            val end = selection.max
            spans = spans.filterNot { it.start == start && it.end == end && it.style.color != Color.Unspecified }
            updateValue(value)
        }
    }

    /**
     * Imports Markdown text and converts markers to StyleSpans.
     */
    fun fromMarkdown(markdown: String) {
        // Avoid redundant loading if already in sync
        if (markdown == toMarkdown()) return

        val metadata = getMarkdownMetadata(markdown)
        val cleanText = metadata.annotatedString.text
        
        val newSpans = mutableListOf<StyleSpan>()
        
        // Extract spans from AnnotatedString
        metadata.annotatedString.spanStyles.forEach { range ->
            newSpans.add(StyleSpan(range.start, range.end, range.item))
        }
        
        spans = newSpans
        // We update the value without using updateValue() logic to avoid span shifting here
        val annotatedString = buildAnnotatedString {
            append(cleanText)
            spans.forEach { span ->
                addStyle(span.style, span.start, span.end)
            }
        }
        value = TextFieldValue(annotatedString = annotatedString)
    }

    /**
     * Exports current text and spans to Markdown format.
     */
    fun toMarkdown(): String {
        val rawText = value.text
        if (spans.isEmpty()) return rawText

        val markers = mutableListOf<Marker>()
        spans.forEach { span ->
            val (prefix, suffix) = getMarkersForStyle(span.style)
            if (prefix.isNotEmpty()) {
                markers.add(Marker(span.start.coerceIn(0, rawText.length), prefix, true, span.end.coerceIn(0, rawText.length)))
                markers.add(Marker(span.end.coerceIn(0, rawText.length), suffix, false, span.start.coerceIn(0, rawText.length)))
            }
        }

        markers.sortWith(compareBy<Marker> { it.pos }
            .thenBy { if (it.isOpening) 1 else 0 }
            .thenBy { if (it.isOpening) -it.otherPos else it.otherPos })

        val result = StringBuilder()
        var currentPos = 0
        markers.forEach { marker ->
            if (marker.pos >= currentPos && marker.pos <= rawText.length) {
                result.append(rawText.substring(currentPos, marker.pos))
                result.append(marker.text)
                currentPos = marker.pos
            }
        }
        if (currentPos < rawText.length) {
            result.append(rawText.substring(currentPos))
        }
        
        return result.toString()
    }

    private data class Marker(val pos: Int, val text: String, val isOpening: Boolean, val otherPos: Int)

    private fun getMarkersForStyle(style: SpanStyle): Pair<String, String> {
        return when {
            style.fontWeight == FontWeight.Bold -> "**" to "**"
            style.fontStyle == FontStyle.Italic -> "_" to "_"
            style.textDecoration == TextDecoration.Underline -> "<u>" to "</u>"
            style.color != Color.Unspecified -> {
                val r = (style.color.red * 255).toInt().toHexString()
                val g = (style.color.green * 255).toInt().toHexString()
                val b = (style.color.blue * 255).toInt().toHexString()
                "<color=#$r$g$b>" to "</color>"
            }
            else -> "" to ""
        }
    }

    private fun Int.toHexString(): String {
        val s = this.toString(16)
        return if (s.length == 1) "0$s" else s
    }
}

val BoldStyle = SpanStyle(fontWeight = FontWeight.Bold)
val ItalicStyle = SpanStyle(fontStyle = FontStyle.Italic)
val UnderlineStyle = SpanStyle(textDecoration = TextDecoration.Underline)
